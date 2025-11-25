package ServiceTests;

import Gaming_Club_Model.Participant;
import Gaming_Club_Model.PersonalityType;
import Gaming_Club_Model.Team;
import Service.PersonalityClassifier;
import Service.TeamBuilder;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class PersonalityClassifierTest {

    @Test
    public void testPersonalityClassification_BoundaryValues(){
        assertEquals(PersonalityType.LEADER, PersonalityClassifier.classify(90));
        assertEquals(PersonalityType.LEADER, PersonalityClassifier.classify(100));
        assertEquals(PersonalityType.BALANCED, PersonalityClassifier.classify(70));
        assertEquals(PersonalityType.BALANCED, PersonalityClassifier.classify(89));
        assertEquals(PersonalityType.THINKER, PersonalityClassifier.classify(50));
        assertEquals(PersonalityType.THINKER, PersonalityClassifier.classify(69));
    }

    @Test
    public void testPersonalityClassification_InvalidScores(){
        assertThrows(IllegalArgumentException.class, () -> PersonalityClassifier.classify(45));
        assertThrows(IllegalArgumentException.class, () -> PersonalityClassifier.classify(101));
    }

    @Test
    public void testSurveyScoreCalculation(){
        int[] responses = {5,4,5,4,5};
        int score = PersonalityClassifier.calculateFromSurvey(responses);

        assertEquals(92, score);
        assertEquals(PersonalityType.LEADER, PersonalityClassifier.classify(score));
    }

    @Test
    public void testInvalidSurveyResponses(){
        int[] invalidResponses = {6,4,5,4,5};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            PersonalityClassifier.calculateFromSurvey(invalidResponses);
        });
        assertTrue(exception.getMessage().contains("Responses must be 1-5"));
    }

    @Test
    public void testFixedAlgorithm_MeetsAllRequirements() {
        // Test with your actual dataset
        List<Participant> participants = TestData.TestDataFactory.createDiverseTestParticipants();
        TeamBuilder builder = new TeamBuilder(participants, 5);

        List<Team> teams = builder.formAdvancedTeams();

        // Basic validation
        assertNotNull(teams);
        assertFalse(teams.isEmpty());

        int teamsWithGoodPersonality = 0;
        int teamsWithGoodRoles = 0;
        int teamsWithGoodGames = 0;

        for (Team team : teams) {
            int leaders = team.countPersonalityType(PersonalityType.LEADER);
            int thinkers = team.countPersonalityType(PersonalityType.THINKER);
            int roles = team.getUniqueRoles().size();
            int games = team.getUniqueGames().size();

            // Check personality constraints
            boolean goodPersonality = (leaders >= 0 && leaders <= 2) &&
                    (thinkers >= 1);
            // Check role constraints
            boolean goodRoles = roles >= 3;
            // Check game variety
            boolean goodGames = games >= 2;

            if (goodPersonality) teamsWithGoodPersonality++;
            if (goodRoles) teamsWithGoodRoles++;
            if (goodGames) teamsWithGoodGames++;

            System.out.printf("%s: %dL %dT %d roles %d games | Personality: %s | Roles: %s | Games: %s%n",
                    team.getName(), leaders, thinkers, roles, games,
                    goodPersonality ? "✅" : "❌",
                    goodRoles ? "✅" : "❌",
                    goodGames ? "✅" : "❌"
            );
        }
        System.out.printf("\nSUMMARY: Personality: %d/%d, Roles: %d/%d, Games: %d/%d%n",
                teamsWithGoodPersonality, teams.size(),
                teamsWithGoodRoles, teams.size(),
                teamsWithGoodGames, teams.size());

        assertTrue("Most teams should have good personality mix",
                teamsWithGoodPersonality >= teams.size() * 0.8); // 80% success
        assertTrue("Most teams should have good role diversity",
                teamsWithGoodRoles >= teams.size() * 0.9); // 90% success
        assertTrue("All teams should have game variety",
                teamsWithGoodGames == teams.size()); // 100% success
    }

}
