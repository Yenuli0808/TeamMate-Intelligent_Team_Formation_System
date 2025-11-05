package Service;

import Gaming_Club_Model.Participant;
import Gaming_Club_Model.PersonalityType;
import Gaming_Club_Model.Team;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVHandler {
    public List<Participant> loadParticipants(String filename) throws IOException {
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

    public void saveTeamsToCSV(List<Team> teams, String filename) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(filename));
        writer.println("TeamID,TeamName,Members,AverageSkill,PersonalityDistribution,Games,Roles");

        for (Team team : teams) {
            // Create members list
            String members = String.join(";",
                    team.getMembers().stream()
                            .map(Participant::getId)
                            .toArray(String[]::new));

            // Create personality distribution
            String personalityDist = String.format("Leaders:%d-Balanced:%d-Thinkers:%d",
                    team.countPersonalityType(PersonalityType.LEADER),
                    team.countPersonalityType(PersonalityType.BALANCED),
                    team.countPersonalityType(PersonalityType.THINKER));

            // Create games and roles lists
            String games = String.join(";", team.getUniqueGames());
            String roles = String.join(";", team.getUniqueRoles());

            writer.printf("%s,%s,%s,%.2f,%s,%s,%s%n",
                    team.getId(), team.getName(), members,
                    team.getAverageSkill(), personalityDist, games, roles);
        }
        writer.close();
    }
}
