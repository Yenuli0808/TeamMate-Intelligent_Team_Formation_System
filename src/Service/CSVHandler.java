package Service;

import Gaming_Club_Model.Participant;
import Gaming_Club_Model.PersonalityType;
import Gaming_Club_Model.Team;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVHandler {

    // Simple enum for file source - just for demonstration
    public enum FileSource {
        LOCAL_FILE,  // File from project directory
        UPLOADED_FILE // File uploaded by user
    }

    /**
     * Enhanced method with simple file source selection
     */
    public List<Participant> loadParticipants(FileSource source, String filePath) throws IOException {
        System.out.println("Loading participants from: " + filePath);
        System.out.println("Source type: " + source);

        // For LOCAL_FILE, use relative path from project root
        // For UPLOADED_FILE, use the provided path directly
        String actualPath = (source == FileSource.LOCAL_FILE) ?
                filePath : // Relative path like "data/participants.csv"
                filePath;  // Absolute path for uploaded files

        return loadParticipantsFromFile(actualPath);
    }

    /**
     * Original method kept for backward compatibility
     */
    public List<Participant> loadParticipants(String filename) throws IOException {
        return loadParticipants(FileSource.LOCAL_FILE, filename);
    }

    /**
     * Core file loading logic (same as before)
     */
    private List<Participant> loadParticipantsFromFile(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new FileNotFoundException("CSV file not found: " + filename);
        }

        List<Participant> participants = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = reader.readLine(); // Skip header

        while ((line = reader.readLine()) != null) {
            try {
                String[] parts = line.split(",");
                if (parts.length >= 8) {
                    Participant participant = new Participant(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            parts[3].trim(),
                            Integer.parseInt(parts[4].trim()),
                            parts[5].trim(),
                            Integer.parseInt(parts[6].trim())
                    );
                    participants.add(participant);
                }
            } catch (Exception e) {
                System.out.println("Skipping invalid line: " + line + " - " + e.getMessage());
            }
        }
        reader.close();
        return participants;
    }

    /**
     * Save teams with file source option
     */
    public void saveTeamsToCSV(FileSource source, List<Team> teams, String filePath) throws IOException {
        System.out.println("Saving teams to: " + filePath);
        System.out.println("Source type: " + source);

        // Use the same save logic regardless of source
        saveTeamsToFile(teams, filePath);
    }

    /**
     * Original save method kept for backward compatibility
     */
    public void saveTeamsToCSV(List<Team> teams, String filename) throws IOException {
        saveTeamsToCSV(FileSource.LOCAL_FILE, teams, filename);
    }

    /**
     * Core save logic (same as before)
     */
    private void saveTeamsToFile(List<Team> teams, String filename) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(filename));
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
        writer.close();
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
