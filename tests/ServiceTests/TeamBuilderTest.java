package ServiceTests;

import Gaming_Club_Model.Participant;
import Gaming_Club_Model.PersonalityType;
import Gaming_Club_Model.Team;
import Service.TeamBuilder;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TeamBuilderTest {

    private List<Participant>createTestParticipants(){
        return Arrays.asList(
                new Participant("P001", "Alice", "alice@edu.com", "0711111111", "Valorant", 8, "Strategist", 95),
                new Participant("P002", "Bob", "bob@edu.com", "0722222222", "CS:GO", 6, "Attacker", 75),
                new Participant("P003", "Charlie", "charlie@edu.com", "0733333333", "DOTA 2", 7, "Defender", 65),
                new Participant("P004", "Diana", "diana@edu.com", "0744444444", "Valorant", 9, "Supporter", 85),
                new Participant("P005", "Eve", "eve@edu.com", "0755555555", "CS:GO", 5, "Coordinator", 70),
                new Participant("P006", "Frank", "frank@edu.com", "0766666666", "DOTA 2", 8, "Strategist", 90)
        );
    }

    @Test
    public void testTeamFormation_Basic(){
        List<Participant> participants = createTestParticipants();
        TeamBuilder builder = new TeamBuilder(participants,3);

        List<Team> teams = builder.formBalancedTeams();

        assertNotNull(teams);
        assertFalse(teams.isEmpty());
        assertEquals(2,teams.size());
    }

    @Test
    public void testTeamFormation_AdvancedConstraints(){
        List<Participant> participants = createTestParticipants();
        TeamBuilder builder = new TeamBuilder(participants,3);

        List<Team> teams = builder.formAdvancedTeams();
        assertNotNull(teams);

        for(Team team : teams){
            assertTrue(team.countPersonalityType(PersonalityType.LEADER)<=1);
            assertTrue(team.getUniqueGames().size() >=1);
            assertTrue(team.getUniqueRoles().size() >= Math.min(3, team.getUniqueGames().size()));
        }
    }

    @Test
    public void testInvalidTeamSize(){
        List<Participant> participants = createTestParticipants();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new TeamBuilder(participants,10);
        });

        assertTrue(exception.getMessage().contains("cannot be greater than number of participants"));
    }
}
