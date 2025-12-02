package Gaming_Club_Model;

import Constant.Constant;
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
    private static final String PARTICIPANTS_CSV = "participants_sample.csv";

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
            this.currentParticipants = csvHandler.loadParticipants(filename);    //sequence 1.3
            System.out.println("SUCCESS: Uploaded " + currentParticipants.size() + " participants from " + filename);   //sequence 1.6
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
        // sequence msg 4-6: validation against constants
        if (currentParticipants == null || currentParticipants.isEmpty()) {
            throw new IllegalStateException("Please upload participant data first");
        }

        if(teamSize < Constant.MIN_TEAM_SIZE){  // sequence :5,7.1(set team formation parameters)
            throw new IllegalArgumentException("Team size cannot be less than "+Constant.MIN_TEAM_SIZE);
        }
        if(teamSize > Constant.MAX_TEAM_SIZE){  //sequence :6,7.2(set team formation parameters)
            throw new IllegalArgumentException("Team size cannot be greater than "+Constant.MAX_TEAM_SIZE);
        }
        if (teamSize > currentParticipants.size()) {
            throw new IllegalArgumentException("Team size (" + teamSize + ") cannot be greater than number of participants (" + currentParticipants.size() + ")");
        }

        System.out.println("\nORGANIZER ACTION: Setting formation parameters...");
        this.teamBuilder = new TeamBuilder(currentParticipants, teamSize);    //sequence msg:7
        System.out.println("SUCCESS: Team size set to " + teamSize + " players per team");
    }

    /**
     * USE CASE: Run Team Formation Algorithm
     * Organizer initiates the team formation process
     */
    public List<Team> runTeamFormation() {
        if (teamBuilder == null) {     //sequence no 2(run team formation)
            throw new IllegalStateException("Please set formation parameters first");
        }
        if (currentParticipants == null || currentParticipants.isEmpty()) {   //sequence no 2.2(run team formation)
            throw new IllegalStateException("No participant data available");
        }

        System.out.println("\nORGANIZER ACTION: Running team formation algorithm...");

        this.currentTeams = teamBuilder.formAdvancedTeams();  // sequence no 2.1, 2.3: run team formation

        // Check if we need to use advanced for better constraints
        if (!meetsMinimumConstraints(currentTeams)) {
            System.out.println("Basic algorithm has constraint issues, trying advanced...");
            this.currentTeams = teamBuilder.formAdvancedTeams();
        }

        teamBuilder.checkRoleDiversity(currentTeams);   //sequence no 2.4: run team formation
        System.out.println("SUCCESS: Formed " + currentTeams.size() + " balanced teams");
        return currentTeams;
    }

    private boolean meetsMinimumConstraints(List<Team> teams) {    // sequence no 2.2: run team formation
        for (Team team : teams) {
            // Check role diversity
            // sequence no 2.2.1: run team formation
            if (team.getUniqueRoles().size() < 3) {
                return false;
            }
            // Check basic personality mix
            // sequence no 2.2.2: run team formation
            if (team.countPersonalityType(PersonalityType.LEADER) > 2 ||
                    team.countPersonalityType(PersonalityType.THINKER) == 0) {
                return false;
            }
        }
        return true;
    }

    public void runTeamFormationConcurrently() {
        if (teamBuilder == null) {
            throw new IllegalStateException("Please set formation parameters first");
        }

        System.out.println("Starting team formation in background thread...");

        teamBuilder.formTeamsConcurrently().thenAccept(teams -> {     //sequence 3.1, 3.1.2:(run team formation)
            synchronized (this) {
                this.currentTeams = teams;
            }
            System.out.println("✓ Background formation completed: " + teams.size() + " teams");
        });
    }

    /**
     * USE CASE: View Formation Results
     * Organizer reviews the formed teams
     */
    public void viewFormationResults() {
        if (currentTeams == null || currentTeams.isEmpty()) {   //sequence 2.1(view team formation result)
            System.out.println("No teams available to display"); //sequence 2.1.1(view team formation result)
            return;
        }

        System.out.println("\nORGANIZER ACTION: Viewing formation results...");
        System.out.println("=== TEAM FORMATION RESULTS ===");
        for (int i = 0; i < currentTeams.size(); i++) {
            Team team = currentTeams.get(i);
            System.out.println("\n" + team.toDisplayString());  //sequence 2.2.2, 2.2.2.1(view formation results)
            System.out.println("Games: " + team.getUniqueGames());  //sequence 2.2.3, 2.2.3.1(view formation results)
            System.out.println("Roles: " + team.getUniqueRoles());  //sequence 2.2.4, 2.2.4.1(view formation results)
            System.out.println("Personalities: " +
                    team.countPersonalityType(PersonalityType.LEADER) + " Leaders, " +
                    team.countPersonalityType(PersonalityType.BALANCED) + " Balanced, " +
                    team.countPersonalityType(PersonalityType.THINKER) + " Thinkers");  //sequence 2.2.5.1...4(view formation results)
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
            csvHandler.saveTeamsToCSV(currentTeams, filename);   //sequence 2.2(save teams)
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

    public List<Participant> refreshParticipantData() {
        try {
            System.out.println("\nORGANIZER ACTION: Refreshing participant data...");
            this.currentParticipants = csvHandler.loadParticipants(PARTICIPANTS_CSV);
            System.out.println("SUCCESS: Refreshed " + currentParticipants.size() + " participants");
            return currentParticipants;
        } catch (Exception e) {
            System.out.println("FAILED: Refresh data - " + e.getMessage());
            throw new RuntimeException("Data refresh failed", e);
        }
    }

    public void showParticipantStatistics() {
        if (currentParticipants == null) {
            System.out.println("No participant data loaded. Please upload CSV first.");
            return;
        }

        System.out.println("\n=== PARTICIPANT STATISTICS ===");
        System.out.println("Total Participants: " + currentParticipants.size());

        // Count by personality type
        long leaders = currentParticipants.stream()
                .filter(p -> p.getPersonalityType() == PersonalityType.LEADER)
                .count();
        long balanced = currentParticipants.stream()
                .filter(p -> p.getPersonalityType() == PersonalityType.BALANCED)
                .count();
        long thinkers = currentParticipants.stream()
                .filter(p -> p.getPersonalityType() == PersonalityType.THINKER)
                .count();

        System.out.println("Personality Distribution:");
        System.out.println("  - Leaders: " + leaders);
        System.out.println("  - Balanced: " + balanced);
        System.out.println("  - Thinkers: " + thinkers);

        // Game distribution
        System.out.println("Unique Games: " +
                currentParticipants.stream()
                        .map(Participant::getPreferredGame)
                        .distinct()
                        .count());
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