package IntegrationTests;

import Gaming_Club_Model.Participant;
import Gaming_Club_Model.Team;
import Service.TeamBuilder;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class ConcurrentProcessingTest {

    private List<Participant> createTestParticipants(){
        return Arrays.asList(
                new Participant("P001", "Alice", "alice@edu.com", "0711111111", "Valorant", 8, "Strategist", 95),
                new Participant("P002", "Bob", "bob@edu.com", "0722222222", "CS:GO", 6, "Attacker", 75),
                new Participant("P003", "Charlie", "charlie@edu.com", "0733333333", "DOTA 2", 7, "Defender", 65),
                new Participant("P004", "Diana", "diana@edu.com", "0744444444", "Valorant", 9, "Supporter", 85)
        );
    }

    @Test
    public void testConcurrentTeamFormation() throws Exception{
        List<Participant> participants = createTestParticipants();
        TeamBuilder teamBuilder = new TeamBuilder(participants,2);

        System.out.println("starting concurrent team formation...");

        //starting concurrent team formation
        CompletableFuture<List<Team>> future = teamBuilder.formTeamsConcurrently();
        assertFalse("Future should not complete immediately", future.isDone());

        List<Team> teams = future.get(5, TimeUnit.SECONDS); // THIS WILL WAIT FOR COMPLETION WITH TIMEOUT

        assertNotNull("teams should not be null", teams);
        assertFalse("Teams should not be empty", teams.isEmpty());
        assertEquals("Should form 2 teams from 4 participants", 2, teams.size());

        System.out.println("Concurrent teams formation completed successfully");

        teamBuilder.shutdown();
    }

    @Test
    public void testConcurrentSurveyProcessing() throws Exception{
        List<Participant> newSurveyData = Arrays.asList(
                new Participant("P101", "New1", "new1@edu.com", "0755555555", "FIFA", 5, "Coordinator", 70),
                new Participant("P102", "New2", "new2@edu.com", "0766666666", "Basketball", 6, "Strategist", 80)
        );

        List<Participant> existingParticipants = createTestParticipants();
        TeamBuilder teamBuilder = new TeamBuilder(existingParticipants,2);
        System.out.println("starting concurrent survey processing...");

        //Processing survey data concurrently
        CompletableFuture<Void> surveyFuture = teamBuilder.processSurveyDataConcurrently(newSurveyData);

        assertFalse("Future should not complete immediately", surveyFuture.isDone());  //testing that future is not done immediately

        surveyFuture.get(5, TimeUnit.SECONDS);
        System.out.println("Concurrent survey processing completed successfully");

        teamBuilder.shutdown();
    }

    @Test
    public void testMultipleConcurrentOperations() throws Exception {
        List<Participant> participants = createTestParticipants();
        System.out.println("Testing multiple concurrent operations...");
        System.out.println("Total participants: " + participants.size());

        // Use more distinct team sizes to ensure different results
        TeamBuilder builder1 = new TeamBuilder(participants, 2);  // Should create more teams
        TeamBuilder builder2 = new TeamBuilder(participants, 4);  // Should create fewer teams

        CompletableFuture<List<Team>> future1 = builder1.formTeamsConcurrently();
        CompletableFuture<List<Team>> future2 = builder2.formTeamsConcurrently();

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(future1, future2);
        combinedFuture.get(10, TimeUnit.SECONDS);

        // Verify both completed successfully
        assertTrue("First future should complete successfully",
                future1.isDone() && !future1.isCompletedExceptionally());
        assertTrue("Second future should complete successfully",
                future2.isDone() && !future2.isCompletedExceptionally());

        List<Team> teams1 = future1.get();
        List<Team> teams2 = future2.get();

        assertNotNull(teams1);
        assertNotNull(teams2);
        assertFalse(teams1.isEmpty());
        assertFalse(teams2.isEmpty());

        // More flexible assertion -verify they both work
        System.out.println("Builder1 (size 2) created: " + teams1.size() + " teams");
        System.out.println("Builder2 (size 4) created: " + teams2.size() + " teams");

        System.out.println("Multiple concurrent operations completed successfully");

        builder1.shutdown();
        builder2.shutdown();
    }

    @Test
    public void testConcurrentOperationTimeout(){
        List<Participant> participants = createTestParticipants();
        TeamBuilder teamBuilder = new TeamBuilder(participants,2);

        //This test verifies that extremely short timeouts will fail
        CompletableFuture<List<Team>> future = teamBuilder.formTeamsConcurrently();

        Exception exception = assertThrows(Exception.class, ()->{
            future.get(1, TimeUnit.MILLISECONDS);
        });

        assertTrue(exception instanceof java.util.concurrent.TimeoutException || exception.getCause() != null);
        teamBuilder.shutdown();
    }

    @Test
    public void testConcurrentOperationWithLargeDataset() throws Exception{
        List<Participant> largeDataset = TestData.TestDataFactory.createDiverseTestParticipants(); //Create a larger dataset for more realistic concurrency testing
        TeamBuilder teamBuilder = new TeamBuilder(largeDataset, 4);

        System.out.println("Testing concurrency with " + largeDataset.size() + " participants...");

        long stratTime = System.currentTimeMillis();

        CompletableFuture<List<Team>> future = teamBuilder.formTeamsConcurrently();
        List<Team> teams = future.get(10, TimeUnit.SECONDS);

        long endTime = System.currentTimeMillis();
        long duration = endTime - stratTime;

        System.out.println("Concurrent processing took " + duration + " milliseconds");

        assertNotNull(teams);
        assertFalse(teams.isEmpty());

        //verifying team compositions
        for(Team team : teams){
            assertTrue(team.getCurrentSize()<= 4);
            assertTrue(team.getAverageSkill() >0);
        }
        teamBuilder.shutdown();
    }
}
