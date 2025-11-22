package Service;

import Constant.Constant;
import Gaming_Club_Model.Participant;
import Gaming_Club_Model.PersonalityType;
import Gaming_Club_Model.Team;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CSVHandler {

    private static final Logger logger = Logger.getLogger(CSVHandler.class.getName());

    public enum FileSource {
        LOCAL_FILE,
        UPLOAD_FILE,
        DATA_DIRECTORY
    }

    //Load participants from CSV file with automatic file location detection
    public List<Participant> loadParticipants(String filename) throws IOException {
        return loadParticipants(FileSource.LOCAL_FILE, filename);
    }

    public List<Participant> loadParticipants(FileSource source, String filePath) throws IOException {
        logger.info("Loading participants from: " + filePath + " (Source: " + source + ")");

        String actualPath = resolveFilePath(source, filePath);
        System.out.println("Loading from: " + actualPath);

        return loadParticipantsFromFile(actualPath);
    }

    private String resolveFilePath(FileSource source, String filePath) {
        switch (source) {
            case DATA_DIRECTORY:
                return "data/" + filePath;
            case UPLOAD_FILE:
                return filePath;
            case LOCAL_FILE:
            default:
                File directFile = new File(filePath);
                if (!directFile.exists()) {
                    File dataDirFile = new File("data/" + filePath);
                    if (dataDirFile.exists()) {
                        return "data/" + filePath;
                    }
                }
                return filePath;
        }
    }

    /**
     * Core file loading logic (same as before)
     */
    private List<Participant> loadParticipantsFromFile(String filename) throws IOException {
        File file = new File(filename);

        //validating file existence
        if (!file.exists()) {
            String errorMsg = "CSV file not found: " + filename +
                    "\nPlease ensure the file exists in one of these locations:" +
                    "\n- " + filename +
                    "\n- data/" + filename +
                    "\n- Project root directory";
            logger.severe(errorMsg);
            throw new FileNotFoundException(errorMsg);
        }

        //Validate file type
        if(! filename.toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("File must be a CSV file: "+filename);
        }

        // Validating file size
        if(file.length() == 0) {
            throw new IOException("Empty CSV file: "+filename);
        }

        List<Participant> participants = new ArrayList<>();
        int lineNumber = 0;
        int successCount = 0;
        int errorCount = 0;

        try(BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;

            //Read and validating headers
            line =  reader.readLine();
            lineNumber++;
            if(line == null) {
                throw new IOException("Empty CSV file or header missing ");
            }

            if(!isValidHeader(line)){
                System.out.println("Warning: CSV header may not match expected format");
                System.out.println("Expected: ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType");
                System.out.println("Found: " + line);
            }

            // Processing Data Rows
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                Participant participant = null;
                try{
                    if(line.trim().isEmpty()){
                        continue;    // this will skip the empty lines
                    }
                    participant = parseParticipantLine(line, lineNumber);
                    if(participant !=null){
                        participants.add(participant);
                        successCount++;
                    }else{
                        errorCount++;
                    }
                }catch (Exception e){
                    errorCount++;
                    System.out.println("Skipping invalid line \" + lineNumber + \": \" + e.getMessage()");
                    logger.warning("Line"+ lineNumber + "error: " + e.getMessage()+"-Data: "+line);
                }
            }
        }catch (IOException e){
            logger.severe("File reading error: "+e.getMessage());
            throw new IOException("Failed to read CSV file: "+e.getMessage());
        }

        if(participants.isEmpty()){
            throw new IOException("No valid participants found in CSV file after processing " + lineNumber + " lines");
        }

        System.out.println("CSV Load Summary:");
        System.out.println("- Successfully loaded: " + successCount + " participants");
        System.out.println("- Errors/Skipped: " + errorCount + " lines");
        System.out.println("- Total valid participants: " + participants.size());

        logger.info("Successfully loaded " + participants.size() + " participants from " + filename);
        return participants;
    }

    private Participant parseParticipantLine(String line, int lineNumber){
        try{
            String[] parts = line.split(",");

            // validating column count
            if(parts.length <8){
                throw new IllegalArgumentException("Insufficient columns. Expected 8, found " + parts.length);
            }

            for(int i = 0; i < parts.length; i++){
                parts[i] = parts[i].trim();
            }

            String id = validateField(parts[0], "ID", lineNumber);
            String name = validateField(parts[1], "Name", lineNumber);
            String email = validateEmail(parts[2], lineNumber);
            String preferredGame = validateGame(parts[3], lineNumber);
            int skillLevel = validateSkillLevel(parts[4], lineNumber);
            String preferredRole = validateRole(parts[5],lineNumber);
            int personalityScore = validatePersonalityScore(parts[6], lineNumber);

            Participant participant = new Participant(id,name,email,"N/A",preferredGame,skillLevel,preferredRole,personalityScore);

            if (!participant.validate()){
                throw new IllegalArgumentException("Participant validation failed");
            }
            return participant;
        } catch (Exception e) {
            System.out.println("Line " + lineNumber + " error: " + e.getMessage());
            return null;
        }
    }

    private String validateField(String value, String fieldName, int lineNumber) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        return value.trim();
    }

    private String validateEmail(String email, int lineNumber) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
        return email.trim();
    }

    private int validateSkillLevel(String skillStr, int lineNumber) {
        try {
            int skill = Integer.parseInt(skillStr);
            if (skill < 1 || skill > 10) {
                throw new IllegalArgumentException("Skill level must be between 1-10, found: " + skill);
            }
            return skill;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid skill level (must be integer 1-10): " + skillStr);
        }
    }

    private int validatePersonalityScore(String scoreStr, int lineNumber) {
        try {
            int score = Integer.parseInt(scoreStr);
            if (score < 0 || score > 100) {
                throw new IllegalArgumentException("Personality score must be between 0-100, found: " + score);
            }
            return score;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid personality score (must be integer 0-100): " + scoreStr);
        }
    }

    private String validateGame(String game, int lineNumber) {
        String validatedGame = validateField(game, "Preferred Game", lineNumber);

        // Check if game is in valid list
        boolean isValid = false;
        for (String validGame : Constant.VALID_GAMES) {
            if (validGame.equalsIgnoreCase(validatedGame)) {
                isValid = true;
                break;
            }
        }

        if (!isValid) {
            throw new IllegalArgumentException(Constant.INVALID_GAME_MESSAGE);
        }
        return validatedGame;
    }

    private String validateRole(String role, int lineNumber) {
        String validatedRole = validateField(role, "Preferred Role", lineNumber);

        // Check if role is in valid list
        boolean isValid = false;
        for (String validRole : Constant.VALID_ROLES) {
            if (validRole.equalsIgnoreCase(validatedRole)) {
                isValid = true;
                break;
            }
        }
        if (!isValid) {
            throw new IllegalArgumentException(Constant.INVALID_ROLE_MESSAGE);
        }
        return validatedRole;
    }

    private boolean isValidHeader(String header) {
        String[] expectedHeaders = {"ID", "Name", "Email", "PreferredGame", "SkillLevel", "PreferredRole", "PersonalityScore", "PersonalityType"};
        String[] actualHeaders = header.split(",");

        if (actualHeaders.length < expectedHeaders.length) {
            return false;
        }

        for (int i = 0; i < expectedHeaders.length; i++) {
            if (!actualHeaders[i].trim().equalsIgnoreCase(expectedHeaders[i])) {
                return false;
            }
        }
        return true;
    }


    //Save teams to csv
    public void saveTeamsToCSV(List<Team> teams, String filename) throws IOException {
        saveTeamsToCSV(FileSource.LOCAL_FILE, teams, filename);
    }

    public void saveTeamsToCSV(FileSource source, List<Team> teams, String filePath) throws IOException {
        if (teams == null || teams.isEmpty()) {
            throw new IllegalArgumentException("No teams to save");
        }

        String actualPath = resolveFilePath(source, filePath);
        logger.info("Saving " + teams.size() + " teams to: " + actualPath);
        System.out.println("Saving teams to: " + actualPath);

        saveTeamsToFile(teams, actualPath);
    }

    private void saveTeamsToFile(List<Team> teams, String filename) throws IOException {
        File outputFile = new File(filename);
        File parentDir = outputFile.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Cannot create output directory: " + parentDir.getAbsolutePath());
            }
        }

        if (outputFile.exists() && !outputFile.canWrite()) {
            throw new IOException("No write permission for file: " + filename);
        }

        try(PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write header
            writer.println("TeamID,TeamName,Members,AverageSkill,PersonalityDistribution,Games,Roles");

            for (Team team : teams) {
                try {
                    String csvLine = formatTeamForCSV(team);
                    writer.println(csvLine);
                } catch (Exception e) {
                    logger.warning("Failed to write team " + team.getId() + ": " + e.getMessage());
                    System.out.println("⚠️  Warning: Failed to write team " + team.getId() + " - " + e.getMessage());
                }
            }

            System.out.println("Successfully saved " + teams.size() + " teams to: " + filename);
            logger.info("Successfully saved " + teams.size() + " teams to " + filename);
        }
        catch (IOException e){
            logger.severe("Failed to save teams to CSV: " + e.getMessage());
            throw new IOException("Failed to save teams to CSV: " + e.getMessage(), e);
        }
    }

    private String formatTeamForCSV(Team team) {
        try {
            // Create members list
            String members = String.join(";",
                    team.getMembers().stream()
                            .map(Participant::getId)
                            .toArray(String[]::new)
            );
            // Create personality distribution
            String personalityDist = String.format("Leaders:%d-Balanced:%d-Thinkers:%d",
                    team.countPersonalityType(PersonalityType.LEADER),
                    team.countPersonalityType(PersonalityType.BALANCED),
                    team.countPersonalityType(PersonalityType.THINKER)
            );
            // Create games and roles lists
            String games = String.join(";", team.getUniqueGames());
            String roles = String.join(";", team.getUniqueRoles());

            return String.format("%s,%s,%s,%.2f,%s,%s,%s",
                    team.getId(),
                    team.getName(),
                    members,
                    team.getAverageSkill(),
                    personalityDist,
                    games,
                    roles
            );
        }
        catch (Exception e) {
            throw new RuntimeException("Error formatting team " + team.getId() + " for CSV: " + e.getMessage(), e);
        }
    }

    public void exportParticipantsToCSV(List<Participant> participants, String filename) throws IOException {
        if (participants == null || participants.isEmpty()) {
            throw new IllegalArgumentException("No participants to export");
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write header
            writer.println("ID,Name,Email,Phone,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType");

            // Write participant data
            for (Participant participant : participants) {
                try {
                    writer.println(participant.toCSVString());
                } catch (Exception e) {
                    logger.warning("Failed to export participant " + participant.getId() + ": " + e.getMessage());
                }
            }

            System.out.println("Successfully exported " + participants.size() + " participants to: " + filename);
            logger.info("Exported " + participants.size() + " participants to " + filename);

        } catch (IOException e) {
            logger.severe("Failed to export participants: " + e.getMessage());
            throw new IOException("Failed to export participants: " + e.getMessage(), e);
        }
    }

    //method to check if a CSV file is valid before processing
    public boolean validateCSVFile(String filename) {
        try {
            File file = new File(filename);
            if (!file.exists() || !file.canRead()) {
                return false;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String header = reader.readLine();
                if (header == null) {
                    return false;
                }

                // Check if we can read at least one data line
                String firstDataLine = reader.readLine();
                return firstDataLine != null && !firstDataLine.trim().isEmpty();
            }

        } catch (Exception e) {
            return false;
        }
    }

    public String getCSVFileInfo(String filename) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                return "File not found: " + filename;
            }

            int lineCount = 0;
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                while (reader.readLine() != null) {
                    lineCount++;
                }
            }

            return String.format("File: %s | Size: %d bytes | Lines: %d",
                    filename, file.length(), lineCount);

        } catch (Exception e) {
            return "Error reading file info: " + e.getMessage();
        }
    }

    public void appendParticipantToCSV(Participant participant, String filename) throws IOException {
        appendParticipantToCSV(FileSource.LOCAL_FILE,participant,filename);
    }

    public void appendParticipantToCSV(FileSource source, Participant participant, String filePath) throws IOException {
        String actualPath = resolveFilePath(source,filePath);

        File file = new File(actualPath);

        if (!file.exists()) {
            createNewCSVWithHeader(actualPath);   //this will create file if it doesn't exist
        }
        if(!participant.validate()){
            throw new IllegalArgumentException("Participant data is invalid");
        }

        try(FileWriter fw = new FileWriter(actualPath,true);
        PrintWriter writer = new PrintWriter(fw)) {
            writer.println(participant.toCSVString());

            System.out.println("✓ Successfully added participant to CSV: " + participant.getId());
            logger.info("Appended participant " + participant.getId() + " to " + actualPath);
        }catch (IOException e) {
            logger.severe("Failed to append participant to CSV: " + e.getMessage());
            throw new IOException("Failed to save participant data: " + e.getMessage(), e);
        }
    }

    private void createNewCSVWithHeader(String filename) throws IOException {
        try(PrintWriter writer = new PrintWriter(new FileWriter(filename))){
            writer.println("ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType");
            System.out.println("Created new CSV file with headers: " + filename);
        }
    }

    public boolean isParticipantIdExists(String participantId, String filename) throws IOException {
        String actualPath = resolveFilePath(FileSource.LOCAL_FILE,filename);
        File file = new File(actualPath);

        if (!file.exists()) {
            return false;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(actualPath))) {
            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                if(line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                if(parts.length > 0 && parts[0].trim().equalsIgnoreCase(participantId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<String> getAllParticipantIds(String filename) throws IOException {
        List<String> ids = new ArrayList<>();
        String actualPath = resolveFilePath(FileSource.LOCAL_FILE,filename);
        File file = new File(actualPath);

        if (!file.exists()) {
            return ids;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(actualPath))) {
            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                if(line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                if(parts.length >0){
                    ids.add(parts[0].trim());
                }
            }
        }
        return ids;
    }

    /**
     * Update existing participant in CSV file
     */
    public void updateParticipantInCSV(Participant updatedParticipant, String filename) throws IOException {
        String actualPath = resolveFilePath(FileSource.LOCAL_FILE, filename);

        // Read all participants from CSV
        List<Participant> allParticipants = loadParticipants(actualPath);

        // Find and replace the participant with same ID
        boolean found = false;
        for (int i = 0; i < allParticipants.size(); i++) {
            if (allParticipants.get(i).getId().equalsIgnoreCase(updatedParticipant.getId())) {
                allParticipants.set(i, updatedParticipant);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Participant not found for update: " + updatedParticipant.getId());
        }

        // Write all participants back to CSV
        exportParticipantsToCSV(allParticipants, actualPath);
        System.out.println("✓ Successfully updated participant: " + updatedParticipant.getId());
    }

}
