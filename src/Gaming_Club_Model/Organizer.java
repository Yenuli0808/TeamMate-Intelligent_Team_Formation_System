package Gaming_Club_Model;

import Service.CSVHandler;
import Service.TeamBuilder;

import java.util.List;

/**
 * Represents a tournament organizer who manages team formation process.
 * Implements the Organizer actor from use case diagrams.
 */
public class Organizer extends BaseEntity {
    private CSVHandler csvHandler;
    private TeamBuilder teamBuilder;
    private List<Team> currentTeams;
    private List<Participant> currentParticipants;

    public Organizer(String id, String name) {
        super(id, name);
        this.csvHandler = new CSVHandler();
        System.out.println("Organizer created: " + name + " (ID: " + id + ")");
    }

    // ===== USE CASE IMPLEMENTATIONS =====

    /**
     * USE CASE: Upload CSV File
     * Organizer uploads participant data from CSV file
     */
    public List<Participant> uploadCSV(String filename) {
        try {
            System.out.println("\nORGANIZER ACTION: Uploading CSV file...");
            this.currentParticipants = csvHandler.loadParticipants(filename);
            System.out.println("SUCCESS: Uploaded " + currentParticipants.size() + " participants from " + filename);
            return currentParticipants;
        } catch (Exception e) {
            System.out.println("FAILED: CSV upload - " + e.getMessage());
            throw new RuntimeException("CSV upload failed", e);
        }
    }

    /**
     * USE CASE: Define Team Formation Parameters
     * Organizer sets team size and formation constraints
     */
    public void setFormationParameters(int teamSize) {
        if (currentParticipants == null || currentParticipants.isEmpty()) {
            throw new IllegalStateException("Please upload participant data first");
        }

        System.out.println("\nORGANIZER ACTION: Setting formation parameters...");
        this.teamBuilder = new TeamBuilder(currentParticipants, teamSize);
        System.out.println("SUCCESS: Team size set to " + teamSize + " players per team");
    }

    /**
     * USE CASE: Run Team Formation Algorithm
     * Organizer initiates the team formation process
     */
    public List<Team> runTeamFormation() {
        if (teamBuilder == null) {
            throw new IllegalStateException("Please set formation parameters first");
        }

        System.out.println("\nORGANIZER ACTION: Running team formation algorithm...");

        this.currentTeams = teamBuilder.formAdvancedTeams();
        System.out.println("SUCCESS: Formed " + currentTeams.size() + " balanced teams");
        return currentTeams;
    }

    /**
     * USE CASE: View Formation Results
     * Organizer reviews the formed teams
     */
    public void viewFormationResults() {
        if (currentTeams == null || currentTeams.isEmpty()) {
            System.out.println("No teams available to display");
            return;
        }

        System.out.println("\nORGANIZER ACTION: Viewing formation results...");
        System.out.println("=== TEAM FORMATION RESULTS ===");
        for (int i = 0; i < currentTeams.size(); i++) {
            Team team = currentTeams.get(i);
            System.out.println("\n" + team.toDisplayString());
            System.out.println("Games: " + team.getUniqueGames());
            System.out.println("Roles: " + team.getUniqueRoles());
            System.out.println("Personalities: " +
                    team.countPersonalityType(PersonalityType.LEADER) + " Leaders, " +
                    team.countPersonalityType(PersonalityType.BALANCED) + " Balanced, " +
                    team.countPersonalityType(PersonalityType.THINKER) + " Thinkers");
        }
    }

    /**
     * USE CASE: Save Teams to CSV
     * Organizer exports teams to CSV file for record keeping
     */
    public void saveTeams(String filename) {
        if (currentTeams == null || currentTeams.isEmpty()) {
            throw new IllegalStateException("No teams formed yet");
        }

        try {
            System.out.println("\nORGANIZER ACTION: Saving teams to CSV...");
            csvHandler.saveTeamsToCSV(currentTeams, filename);
            System.out.println("SUCCESS: Saved " + currentTeams.size() + " teams to " + filename);
        } catch (Exception e) {
            System.out.println("FAILED: Save teams - " + e.getMessage());
            throw new RuntimeException("Team save failed", e);
        }
    }

    /**
     * USE CASE: Get detailed team analysis
     * Organizer requests advanced team reports
     */
    public void generateAdvancedReport() {
        if (teamBuilder == null || currentTeams == null) {
            throw new IllegalStateException("No teams available for analysis");
        }

        System.out.println("\nORGANIZER ACTION: Generating advanced team report...");
        teamBuilder.printAdvancedReport(currentTeams);
    }


    @Override
    public boolean validate() {
        return id != null && !id.isEmpty() && name != null && !name.isEmpty();
    }

    // ===== GETTERS =====
    public List<Participant> getCurrentParticipants() {
        return currentParticipants;
    }

    public List<Team> getCurrentTeams() {
        return currentTeams;
    }

    public int getParticipantCount() {
        return currentParticipants != null ? currentParticipants.size() : 0;
    }

    public int getTeamCount() {
        return currentTeams != null ? currentTeams.size() : 0;
    }

    @Override
    public String toString() {
        return String.format("Organizer[ID: %s, Name: %s, Participants: %d, Teams: %d]",
                id, name, getParticipantCount(), getTeamCount());
    }
}