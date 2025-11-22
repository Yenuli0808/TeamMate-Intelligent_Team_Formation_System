package IntegrationTests;

import Gaming_Club_Model.Organizer;
import Gaming_Club_Model.Participant;
import Gaming_Club_Model.Team;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class OrganizerWorkflowTest {

    @Test
    public void testCompleteOrganizerWorkFlow() throws IOException {
        String existingFile = "participants_sample.csv";
        File file = new File(existingFile);

        if (!file.exists()) {
            System.out.println("Test skipped - participants_sample.csv not found");
            return;
        }

        Organizer organizer = new Organizer("ORG001","Test Organizer");

        List<Participant> participants = organizer.uploadCSV(existingFile);
        assertNotNull(participants);
        assertFalse(participants.isEmpty());

        organizer.setFormationParameters(4);

        List<Team> teams = organizer.runTeamFormation();
        assertNotNull(teams);
        assertFalse(teams.isEmpty());

        File outputFile = File.createTempFile("output_teams", ".csv");
        organizer.saveTeams(outputFile.getAbsolutePath());
        assertTrue(outputFile.exists());
    }
    
}
