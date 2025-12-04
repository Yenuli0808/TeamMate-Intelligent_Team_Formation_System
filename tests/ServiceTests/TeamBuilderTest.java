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

    @Test
    public void testPersonalityScoring_FirstLeaderGetsHighScore(){
        List<Participant> participants = Arrays.asList(
                //Leaders
                new Participant("P001", "Leader1", "leader1@edu.com", "0711111111", "Valorant", 8, "Strategist", 95),
                new Participant("P002", "Leader2", "leader2@edu.com", "0722222222", "CS:GO", 7, "Attacker", 92),

                //Thinkers
                new Participant("P003", "Thinker1", "thinker1@edu.com", "0733333333", "DOTA 2", 6, "Defender", 65),
                new Participant("P004", "Thinker2", "thinker2@edu.com", "0744444444", "FIFA", 5, "Supporter", 60),

                //Balanced
                new Participant("P005", "Balanced1", "balanced1@edu.com", "0755555555", "Basketball", 7, "Coordinator", 80),
                new Participant("P006", "Balanced2", "balanced2@edu.com", "0766666666", "Valorant", 6, "Attacker", 78)
        );
        TeamBuilder builder = new TeamBuilder(participants,3);
        List<Team> teams = builder.formAdvancedTeams();

        assertEquals("Should form 2 teams from 6 participants",2,teams.size());

        //checking each team composition
        for(Team team : teams){
            int leaders = team.countPersonalityType(PersonalityType.LEADER);
            int thinkers = team.countPersonalityType(PersonalityType.THINKER);
            int balanced = team.countPersonalityType(PersonalityType.BALANCED);

            assertTrue("Each team should have 0-1 leaders", leaders <= 1);
            assertTrue("Each team should have 1-2 thinkers", thinkers >= 1 && thinkers <= 2);
            assertTrue("Each team should have balanced players", balanced >= 1);

            assertTrue("Each team should have multiple roles", team.getUniqueRoles().size() >= 2);
        }
    }

    @Test
    public void testRoleScoring_NewRoleGetsHighScore() {
        List<Participant> participants = Arrays.asList(
                new Participant("P001", "P1", "p1@edu.com", "0711111111", "Valorant", 8, "Strategist", 85),
                new Participant("P002", "P2", "p2@edu.com", "0722222222", "CS:GO", 7, "Attacker", 80),
                new Participant("P003", "P3", "p3@edu.com", "0733333333", "DOTA 2", 6, "Defender", 75),
                new Participant("P004", "P4", "p4@edu.com", "0744444444", "FIFA", 5, "Supporter", 70),
                new Participant("P005", "P5", "p5@edu.com", "0755555555", "Basketball", 6, "Coordinator", 78)
        );

        TeamBuilder builder = new TeamBuilder(participants, 5);
        List<Team> teams = builder.formAdvancedTeams();

        assertNotNull(teams);
        assertFalse(teams.isEmpty());

        // Assert that all teams have good role diversity
        for (Team formedTeam : teams) {
            int roleCount = formedTeam.getUniqueRoles().size();
            assertTrue("Each team should have at least 3 roles, but got " + roleCount,
                    roleCount >= 3);
        }
    }

    @Test
    public void testRoleScoring_DuplicateRoleGetsPenalty() {
        List<Participant> participants = Arrays.asList(
                new Participant("P001", "Strategist1", "s1@edu.com", "0711111111", "Valorant", 8, "Strategist", 85),
                new Participant("P002", "Strategist2", "s2@edu.com", "0722222222", "CS:GO", 7, "Strategist", 80), // Same role
                new Participant("P003", "Attacker", "a1@edu.com", "0733333333", "DOTA 2", 6, "Attacker", 75),
                new Participant("P004", "Defender", "d1@edu.com", "0744444444", "FIFA", 5, "Defender", 70),
                new Participant("P005", "Supporter", "sup1@edu.com", "0755555555", "Basketball", 6, "Supporter", 78),
                new Participant("P006", "Coordinator", "c1@edu.com", "0766666666", "Valorant", 7, "Coordinator", 82)
        );

        // Use team size 3 to form 2 teams
        TeamBuilder builder = new TeamBuilder(participants, 3);
        List<Team> teams = builder.formAdvancedTeams();

        assertNotNull(teams);
        assertEquals("Should form 2 teams from 6 participants", 2, teams.size());

        // check that strategists are distributed between teams
        int totalStrategists = 0;
        for (Team team : teams) {
            long strategistCount = team.getMembers().stream()
                    .filter(p -> p.getPreferredRole().equals("Strategist"))
                    .count();
            totalStrategists += strategistCount;

            // With multiple teams, algorithm should avoid putting both strategists in same team
            assertTrue("Should not have multiple strategists in same team",
                    strategistCount <= 1);
        }

        // Verify both strategists were used
        assertEquals("Should use both strategists across teams", 2, totalStrategists);
    }



}
