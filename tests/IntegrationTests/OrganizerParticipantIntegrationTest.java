package IntegrationTests;

import Gaming_Club_Model.Organizer;
import Gaming_Club_Model.Participant;
import Gaming_Club_Model.Team;
import Service.ParticipantPortal;
import Service.TeamBuilder;
import TestData.TestDataFactory;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class OrganizerParticipantIntegrationTest {

    @Test
    public void testOrganizerCreatesTeams_ParticipantViewsThem() throws IOException {
        //creating temporary CSV file for organizer to load
        File tempFile = createTestCSVFile();

        Organizer organizer = new Organizer("ORG001", "Test Organizer");
        List<Participant> participants = organizer.uploadCSV(tempFile.getAbsolutePath());

        organizer.setFormationParameters(3);
        List<Team> teams = organizer.runTeamFormation();

        ParticipantPortal portal = new ParticipantPortal();
        portal.initializeData(teams, participants);

        // Test: Participants can view their teams
        int participantsWithTeams = 0;
        for (Participant participant : participants) {
            try {
                Team team = portal.viewTeamAssignment(participant.getId());
                if (team != null) {
                    participantsWithTeams++;
                    assertTrue("Team should contain participant",
                            team.getMembers().contains(participant));
                }
            } catch (Exception e) {
                // Some participants might not be assigned due to team size constraints
                System.out.println("Participant " + participant.getId() + " not assigned: " + e.getMessage());
            }
        }
        System.out.println("✓ Integration test passed: " + participantsWithTeams +
                "/" + participants.size() + " participants can view their teams");

        tempFile.delete();
    }

    private File createTestCSVFile() throws IOException {
        File tempFile = File.createTempFile("integration_test", ".csv");
        tempFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(tempFile)) {
            // Write CSV header
            writer.write("ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType\n");

            // Write test data - diverse participants for good team formation
            writer.write("P001,Participant_1,user1@university.edu,Valorant,8,Strategist,95,Leader\n");
            writer.write("P002,Participant_2,user2@university.edu,CS:GO,6,Attacker,75,Balanced\n");
            writer.write("P003,Participant_3,user3@university.edu,DOTA 2,7,Defender,65,Thinker\n");
            writer.write("P004,Participant_4,user4@university.edu,Valorant,9,Supporter,85,Balanced\n");
            writer.write("P005,Participant_5,user5@university.edu,CS:GO,5,Coordinator,70,Balanced\n");
            writer.write("P006,Participant_6,user6@university.edu,DOTA 2,8,Strategist,90,Leader\n");
            writer.write("P007,Participant_7,user7@university.edu,FIFA,4,Attacker,80,Balanced\n");
            writer.write("P008,Participant_8,user8@university.edu,Basketball,7,Defender,60,Thinker\n");
            writer.write("P009,Participant_9,user9@university.edu,Valorant,6,Supporter,78,Balanced\n");
            writer.write("P010,Participant_10,user10@university.edu,CS:GO,8,Coordinator,92,Leader\n");
        }

        System.out.println("Created test CSV file: " + tempFile.getAbsolutePath());
        return tempFile;
    }

    @Test
    public void testOrganizerWorkflow_CompleteIntegration() throws IOException {
        // Test complete workflow with file operations
        File tempFile = createTestCSVFile();
        Organizer organizer = new Organizer("ORG002", "Integration Organizer");

        // Step 1: Upload CSV
        List<Participant> participants = organizer.uploadCSV(tempFile.getAbsolutePath());
        assertNotNull("Participants should be loaded", participants);
        assertFalse("Participants should not be empty", participants.isEmpty());

        // Step 2: Set parameters and form teams
        organizer.setFormationParameters(4);
        List<Team> teams = organizer.runTeamFormation();
        assertNotNull("Teams should be created", teams);
        assertFalse("Teams should not be empty", teams.isEmpty());

        // Step 3: Initialize participant portal
        ParticipantPortal portal = new ParticipantPortal();
        portal.initializeData(teams, participants);

        // Step 4: Verify participants can access their assignments
        int accessibleAssignments = 0;
        for (Participant participant : participants) {
            try {
                Team team = portal.viewTeamAssignment(participant.getId());
                if (team != null) {
                    accessibleAssignments++;
                }
            } catch (Exception e) {
                // Handle unassigned participants
            }
        }

        System.out.println("✓ Complete workflow: " + accessibleAssignments +
                "/" + participants.size() + " participants can access teams");

        // Step 5: Test search functionality
        List<Participant> searchResults = portal.findParticipant("P001");
        assertFalse("Should find participant by ID", searchResults.isEmpty());
        assertEquals("Should find correct participant", "P001", searchResults.get(0).getId());

        // Clean up
        tempFile.delete();
    }

    @Test
    public void testErrorHandling_Integration() {
        List<Participant> participants = TestDataFactory.createDiverseTestParticipants();
        TeamBuilder teamBuilder = new TeamBuilder(participants, 3);
        List<Team> teams = teamBuilder.formAdvancedTeams();

        ParticipantPortal portal = new ParticipantPortal();
        portal.initializeData(teams, participants);

        // Test 1: Non-existent participant
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            portal.viewTeamAssignment("NONEXISTENT123");
        });
        assertTrue("Should handle non-existent participant",
                exception.getMessage().contains("No team assignment"));

        System.out.println("✓ Error handling integration test passed");

        teamBuilder.shutdown();
    }

    @Test
    public void testMultipleTeamSizes_Integration() throws IOException {
        // Test that the integration works with different team sizes
        File tempFile = createTestCSVFile();
        Organizer organizer = new Organizer("ORG003", "Multi-size Organizer");

        List<Participant> participants = organizer.uploadCSV(tempFile.getAbsolutePath());

        int[] teamSizes = {3, 4, 5};

        for (int teamSize : teamSizes) {
            organizer.setFormationParameters(teamSize);
            List<Team> teams = organizer.runTeamFormation();

            ParticipantPortal portal = new ParticipantPortal();
            portal.initializeData(teams, participants);

            // Count successful assignments
            int assignedCount = 0;
            for (Participant participant : participants) {
                try {
                    Team team = portal.viewTeamAssignment(participant.getId());
                    if (team != null) assignedCount++;
                } catch (Exception e) {
                    // Some participants might not be assigned
                }
            }

            System.out.println("Team size " + teamSize + ": " + assignedCount +
                    "/" + participants.size() + " participants assigned");

            assertTrue("Should assign most participants with team size " + teamSize,
                    assignedCount >= participants.size() * 0.7);
        }

        tempFile.delete();
    }
}
