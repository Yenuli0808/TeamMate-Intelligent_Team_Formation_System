package ServiceTests;

import Gaming_Club_Model.Participant;
import Gaming_Club_Model.Team;
import Service.CSVHandler;
import Service.TeamBuilder;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CSVHandlerTest {

    @Test
    public void testCSVParsing() throws IOException {
        File tempFile = File.createTempFile("test_participants",".csv");
        tempFile.deleteOnExit();

        try(FileWriter writer =  new FileWriter(tempFile)) {
            writer.write("ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType\n");
            writer.write("P101,Participant_101,user101@university.edu,Valorant,8,Strategist,85,Balanced\n");
            writer.write("P102,Participant_102,user102@university.edu,CS:GO,6,Attacker,75,Balanced\n");
        }

        CSVHandler csvHandler = new CSVHandler();
        List<Participant> participants = csvHandler.loadParticipants(tempFile.getAbsolutePath());

        assertEquals(2,participants.size());
        assertEquals("P101",participants.get(0).getId());
        assertEquals("Valorant",participants.get(0).getPreferredGame());
    }

    @Test
    public void testTeamExport() throws IOException {
        List<Participant> participants = Arrays.asList(
                new Participant("P001", "Alice", "alice@edu.com", "0711111111", "Valorant", 8, "Strategist", 95),
                new Participant("P002", "Bob", "bob@edu.com", "0722222222", "CS:GO", 6, "Attacker", 75)
        );

        TeamBuilder builder = new TeamBuilder(participants,2);
        List<Team> teams  = builder.formBalancedTeams();

        File tempFile = File.createTempFile("test_teams",".csv");
        tempFile.deleteOnExit();

        CSVHandler csvHandler = new CSVHandler();
        csvHandler.saveTeamsToCSV(teams,tempFile.getAbsolutePath());

        assertTrue(tempFile.exists());
        assertTrue(tempFile.length() > 0);
    }
}
