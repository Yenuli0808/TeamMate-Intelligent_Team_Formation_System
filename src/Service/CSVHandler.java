package Service;

import Gaming_Club_Model.Participant;
import Gaming_Club_Model.PersonalityType;
import Gaming_Club_Model.Team;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CSVHandler {
    public enum FileSource{
        LOCAL("Local File System"),
        NETWORK("Network Location"),
        CLOUD("Cloud Storage");

        private String description;
        FileSource(String description){
            this.description = description;
        }
        public String getDescription(){
            return description;
        }
    }

    // In here main method is supporting different file sources
    // and demonstrate method overloading and strategy pattern

    public List<Participant> loadParticipants(FileSource source, String filePath) throws IOException{
        System.out.println("Loading from"+source.getDescription()+": "+filePath);

        switch (source){
            case LOCAL:
                return loadFromLocalFile(filePath);
            case NETWORK:
                return loadFromNetworkLocation(filePath);
            case CLOUD:
                return loadFromCloudStorage(filePath);
            default:
                throw new IllegalArgumentException("Unsupported file source: "+source);
        }
    }

    //Overloaded method for backward compatibility
    //Defaults to LOCAL file source
    public List<Participant> loadParticipants(String filename) throws IOException {
        return loadParticipants(FileSource.LOCAL, filename);
    }

    //Local file system implementation (original functionality)
    private List<Participant> loadFromLocalFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("Local file not found: " + filePath);
        }
        if (!file.canRead()) {
            throw new IOException("No read permission for file: " + filePath);
        }
        return readParticipantsFromStream(new FileReader(file));
    }

    //Network location implementation (simulated)
    private List<Participant> loadFromNetworkLocation(String networkPath) throws IOException {
        System.out.println("🔄 Accessing network location: " + networkPath);

        // Simulate network delay
        simulateProcessingDelay(500);

        // For demonstration, treat network paths starting with "http" or "\\"
        if (networkPath.startsWith("http")) {
            return loadFromURL(networkPath);
        } else if (networkPath.startsWith("\\\\")) {
            return loadFromNetworkShare(networkPath);
        } else {
            throw new IOException("Unsupported network path format: " + networkPath);
        }
    }

    //Cloud storage implementation (simulated)
    private List<Participant> loadFromCloudStorage(String cloudPath) throws IOException {
        System.out.println("☁️  Accessing cloud storage: " + cloudPath);

        // Simulate cloud API call delay
        simulateProcessingDelay(800);

        // For demonstration purposes - in real implementation,
        // you would integrate with cloud storage APIs
        if (cloudPath.startsWith("s3://") || cloudPath.contains("cloud")) {
            // Simulate downloading from cloud and reading local cache
            String localCachePath = "cache/" + Paths.get(cloudPath).getFileName();
            return loadFromLocalFile(localCachePath);
        } else {
            throw new IOException("Unsupported cloud path format: " + cloudPath);
        }
    }

    private List<Participant> loadFromURL(String urlString) throws IOException {
        try {
            URL url = new URL(urlString);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            return readParticipantsFromStream(reader);
        } catch (Exception e) {
            throw new IOException("Failed to load from URL: " + urlString, e);
        }
    }

    private List<Participant> loadFromNetworkShare(String networkPath) throws IOException {
        // Simulate network share access
        // In real implementation, you might use JCIFS or similar library
        Path path = Paths.get(networkPath.replace("\\\\", "//"));
        if (!Files.exists(path)) {
            throw new FileNotFoundException("Network share not accessible: " + networkPath);
        }

        return readParticipantsFromStream(new FileReader(path.toFile()));
    }

    //Generic method to read participants from any Reader
    //Demonstrates code reuse and separation of concerns
    private List<Participant> readParticipantsFromStream(Reader reader) throws IOException {
        List<Participant> participants = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(reader);

        try {
            String line = bufferedReader.readLine(); // Skip header

            while ((line = bufferedReader.readLine()) != null) {
                try {
                    Participant participant = parseParticipantLine(line);
                    if (participant != null) {
                        participants.add(participant);
                    }
                } catch (Exception e) {
                    System.out.println("⚠️  Skipping invalid line: " + line + " - " + e.getMessage());
                }
            }
        } finally {
            bufferedReader.close();
        }

        return participants;
    }

    private Participant parseParticipantLine(String line) {
        String[] parts = line.split(",");
        if (parts.length < 8) {
            throw new IllegalArgumentException("Insufficient data fields. Expected 8, got " + parts.length);
        }

        // Enhanced validation
        validateField(parts[0], "ID");
        validateField(parts[1], "Name");
        validateField(parts[2], "Email");
        validateField(parts[3], "Preferred Game");
        validateField(parts[5], "Preferred Role");

        int skillLevel = parseIntWithValidation(parts[4].trim(), "Skill Level", 1, 10);
        int personalityScore = parseIntWithValidation(parts[6].trim(), "Personality Score", 0, 100);

        return new Participant(
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim(),
                parts[3].trim(),
                skillLevel,
                parts[5].trim(),
                personalityScore
        );
    }

    private void validateField(String field, String fieldName) {
        if (field == null || field.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
    }

    private int parseIntWithValidation(String value, String fieldName, int min, int max) {
        try {
            int num = Integer.parseInt(value);
            if (num < min || num > max) {
                throw new IllegalArgumentException(fieldName + " must be between " + min + " and " + max);
            }
            return num;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid " + fieldName + ": " + value);
        }
    }

    public void saveTeamsToCSV(FileSource source, List<Team> teams, String filePath) throws IOException {
        System.out.println("Saving to " + source.getDescription() + ": " + filePath);

        switch (source) {
            case LOCAL:
                saveToLocalFile(teams, filePath);
                break;
            case NETWORK:
                saveToNetworkLocation(teams, filePath);
                break;
            case CLOUD:
                saveToCloudStorage(teams, filePath);
                break;
            default:
                throw new IllegalArgumentException("Unsupported file source: " + source);
        }
    }

    public void saveTeamsToCSV(List<Team> teams, String filename) throws IOException {
        saveTeamsToCSV(FileSource.LOCAL, teams, filename);
    }

    private void saveToLocalFile(List<Team> teams, String filePath) throws IOException {
        // Create directory if it doesn't exist
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writeTeamsToCSV(teams, writer);
        }
    }

    private void saveToNetworkLocation(List<Team> teams, String networkPath) throws IOException {
        System.out.println("🔄 Saving to network location: " + networkPath);
        simulateProcessingDelay(500);
        saveToLocalFile(teams, networkPath); // For demo, treat as local file
    }

    private void saveToCloudStorage(List<Team> teams, String cloudPath) throws IOException {
        System.out.println("☁️  Saving to cloud storage: " + cloudPath);
        simulateProcessingDelay(800);
        saveToLocalFile(teams, cloudPath); // For demo, treat as local file
    }

    private void writeTeamsToCSV(List<Team> teams, PrintWriter writer) {
        writer.println("TeamID,TeamName,Members,AverageSkill,PersonalityDistribution,Games,Roles");

        for (Team team : teams) {
            String members = String.join(";",
                    team.getMembers().stream()
                            .map(Participant::getId)
                            .toArray(String[]::new));

            String personalityDist = String.format("Leaders:%d-Balanced:%d-Thinkers:%d",
                    team.countPersonalityType(PersonalityType.LEADER),
                    team.countPersonalityType(PersonalityType.BALANCED),
                    team.countPersonalityType(PersonalityType.THINKER));

            String games = String.join(";", team.getUniqueGames());
            String roles = String.join(";", team.getUniqueRoles());

            writer.printf("%s,%s,%s,%.2f,%s,%s,%s%n",
                    team.getId(), team.getName(), members,
                    team.getAverageSkill(), personalityDist, games, roles);
        }
    }

    private void simulateProcessingDelay(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static FileSource[] getAvailableFileSources() {
        return FileSource.values();
    }

    public static String getFileSourceDescription(FileSource source) {
        return source.getDescription();
    }

}














//package Service;
//
//import Gaming_Club_Model.Participant;
//import Gaming_Club_Model.PersonalityType;
//import Gaming_Club_Model.Team;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class CSVHandler {
//    public List<Participant> loadParticipants(String filename) throws IOException {
//        File file = new File(filename);
//        if (!file.exists()) {
//            throw new FileNotFoundException("CSV file not found: " + filename);
//        }
//        List<Participant> participants = new ArrayList<>();
//        BufferedReader reader = new BufferedReader(new FileReader(filename));
//        String line = reader.readLine(); // Skip header
//
//        while ((line = reader.readLine()) != null) {
//            try {
//                String[] parts = line.split(",");
//                if (parts.length >= 8) {
//                    Participant participant = new Participant(
//                            parts[0].trim(),
//                            parts[1].trim(),
//                            parts[2].trim(),
//                            parts[3].trim(),
//                            Integer.parseInt(parts[4].trim()),
//                            parts[5].trim(),
//                            Integer.parseInt(parts[6].trim())
//                    );
//                    participants.add(participant);
//                }
//            } catch (Exception e) {
//                System.out.println("Skipping invalid line: " + line + " - " + e.getMessage());
//            }
//        }
//        reader.close();
//        return participants;
//    }
//
//    public void saveTeamsToCSV(List<Team> teams, String filename) throws IOException {
//        PrintWriter writer = new PrintWriter(new FileWriter(filename));
//        writer.println("TeamID,TeamName,Members,AverageSkill,PersonalityDistribution,Games,Roles");
//
//        for (Team team : teams) {
//            // Create members list
//            String members = String.join(";",
//                    team.getMembers().stream()
//                            .map(Participant::getId)
//                            .toArray(String[]::new));
//
//            // Create personality distribution
//            String personalityDist = String.format("Leaders:%d-Balanced:%d-Thinkers:%d",
//                    team.countPersonalityType(PersonalityType.LEADER),
//                    team.countPersonalityType(PersonalityType.BALANCED),
//                    team.countPersonalityType(PersonalityType.THINKER));
//
//            // Create games and roles lists
//            String games = String.join(";", team.getUniqueGames());
//            String roles = String.join(";", team.getUniqueRoles());
//
//            writer.printf("%s,%s,%s,%.2f,%s,%s,%s%n",
//                    team.getId(), team.getName(), members,
//                    team.getAverageSkill(), personalityDist, games, roles);
//        }
//        writer.close();
//    }
//}
