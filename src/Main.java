import Gaming_Club_Model.Organizer;
import Gaming_Club_Model.Participant;
import Gaming_Club_Model.Team;
import Service.*;

import java.util.Arrays;
import java.util.List;

public class Main{
    public static void main(String[] args) {
        System.out.println("=== TeamMate: Intelligent Team Formation System ===");
        System.out.println("\n Demonstrating Core OOP Concepts and Functionality \n");

        demonstratePersonalityClassification();
        demonstrateParticipantCreation();
        demonstrateTeamFormation();
        demonstrateInterfaces();
        demonstrateFileHandling();
        demonstrateAdvancedAlgorithm();
        demonstrateConcurrency();

        demonstrateOrganizerWorkflow();
        demonstrateCompleteCSVWorkflow();

    }

    private static void demonstratePersonalityClassification(){
        System.out.println("1. PERSONALITY CLASSIFICATION DEMONSTRATION");
        System.out.println("--------------------------------------------");

        int[] testScores = {95,80,65,45,100,75};

        for( int score: testScores ){
            try{
                var personality = PersonalityClassifier.classify(score);
                System.out.printf("Score %3d -> %-8s(Range: %d-%d)%n",score,personality,personality.getMinScore(),personality.getMaxScore());
            }catch (IllegalArgumentException e){
                System.out.printf("Score %3d -> ERROR: %s%n", score, e.getMessage());
            }
        }
        System.out.println();
    }

    private static void demonstrateParticipantCreation(){
        System.out.println("2. PARTICIPANT CREATION DEMONSTRATION");
        System.out.println("------------------------------------------");

        try{
            Participant participant1 = new Participant("P001","John Doe","john@university.edu","Valorant",8,"Strategist",85);
            System.out.println("Participant 1 created: " + participant1);

            try{
                Participant invalid= new Participant("P002","Jane Smith","jane@university.edu","CS:G0",15,"Attacker",85);
            }catch (IllegalArgumentException e){
                System.out.println("Rejected Invalid Participant: " + e.getMessage());
            }
        }catch (Exception e){
            System.out.println("Error creating Participant: " + e.getMessage());
        }
        System.out.println();
    }

    private static void demonstrateTeamFormation(){
        System.out.println("3. TEAM FORMATION ALGORITHM DEMONSTRATION");
        System.out.println("----------------------------------------");

        try{
            List<Participant> participants = Arrays.asList(
                    new Participant("P001", "Alice", "alice@edu.com", "Valorant", 8, "Strategist", 95),
                    new Participant("P002", "Bob", "bob@edu.com", "CS:GO", 6, "Attacker", 75),
                    new Participant("P003", "Charlie", "charlie@edu.com", "DOTA 2", 7, "Defender", 65),
                    new Participant("P004", "Diana", "diana@edu.com", "Valorant", 9, "Supporter", 85),
                    new Participant("P005", "Eve", "eve@edu.com", "CS:GO", 5, "Coordinator", 70),
                    new Participant("P006", "Frank", "frank@edu.com", "DOTA 2", 8, "Strategist", 90)
            );
            System.out.printf("Forming teams from %d participants...%n", participants.size());

            TeamBuilder teamBuilder = new TeamBuilder(participants,3);
            List<Team> teams = teamBuilder.formBalancedTeams();

            System.out.printf("Successfully formed %d teams:%n%n", teams.size());

            for(Team team: teams){
                System.out.println(team.getTeamDetails());
                System.out.println("---");
            }
        } catch (Exception e) {
            System.out.println("✗ Error in team formation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void demonstrateInterfaces(){
        System.out.println("4. INTERFACES DEMONSTRATION");
        System.out.println("--------------------------------------");

        List<Participant> participants = Arrays.asList(
                new Participant("P001", "Alice", "alice@edu.com", "Valorant", 8, "Strategist", 95),
                new Participant("P002", "Bob", "bob@edu.com", "CS:GO", 6, "Attacker", 75)
        );

        System.out.println("Formattable Interface Examples:");
        for (Participant p : participants) {
            displayFormattableObject(p);
        }

        TeamFormationStrategy strategy = new TeamBuilder(participants, 2);
        System.out.println("\nTeamFormationStrategy: " + strategy.getStrategyName());
        List<Team> teams = strategy.formTeams();

        for (Team team : teams) {
            displayFormattableObject(team);
        }
    }

    private static void displayFormattableObject(Formattable obj){
        System.out.println(" - " + obj.toDisplayString());
    }

    private static void demonstrateFileHandling(){
        System.out.println("5. FILE HANDLING DEMONSTRATION");
        System.out.println("--------------------------------------");

        try{
            CSVHandler csvHandler = new CSVHandler();

            System.out.println("Loading Participant from CSV file...");
            List<Participant> participants = csvHandler.loadParticipants("participants_sample.csv");
            System.out.printf("Successfully loaded %d participants%n", participants.size()+"from CSV file");

            TeamBuilder builder = new TeamBuilder(participants, 4);
            List<Team> teams = builder.formBalancedTeams();  // Declare teams here

            System.out.println("Saving teams to output CSV...");
            csvHandler.saveTeamsToCSV(teams, "formed_teams.csv");
            System.out.println("SUCCESS: Saved " + teams.size() + " teams to 'formed_teams.csv'");

            System.out.println("\nSample of loaded data (first 3 participants):");
            for (int i = 0; i < Math.min(3, participants.size()); i++) {
                System.out.println("  - " + participants.get(i).toDisplayString());
            }
        }catch (Exception e){
            System.out.println("FILE HANDLING ERROR: " + e.getMessage());
            System.out.println("Note:Make sure 'participants_sample.csv' is in your project root directory");
        }
    }

    private static void demonstrateAdvancedAlgorithm(){
        System.out.println("6. ADVANCED ALGORITHM DEMONSTRATION");
        System.out.println("-------------------------------------");

        List<Participant> participants = Arrays.asList(
                new Participant("P001", "Alice", "alice@edu.com", "Valorant", 8, "Strategist", 95),
                new Participant("P002", "Bob", "bob@edu.com", "Valorant", 6, "Attacker", 75),
                new Participant("P003", "Charlie", "charlie@edu.com", "Valorant", 7, "Defender", 65),
                new Participant("P004", "Diana", "diana@edu.com", "CS:GO", 9, "Supporter", 85),
                new Participant("P005", "Eve", "eve@edu.com", "CS:GO", 5, "Coordinator", 92),
                new Participant("P006", "Frank", "frank@edu.com", "DOTA 2", 8, "Strategist", 88)
        );

        System.out.println("Using Advanced Algorithm with 3 Simple Rules:");
        System.out.println("1. One leader per team maximum");
        System.out.println("2. Maximum 2 players from same game");
        System.out.println("3. Prefer different roles for diversity");

        TeamBuilder advancedBuilder = new TeamBuilder(participants, 3);
        List<Team> advancedTeams = advancedBuilder.formAdvancedTeams();

        System.out.println("\nADVANCED ALGORITHM: Formed " + advancedTeams.size() + " optimally balanced teams");
        advancedBuilder.printAdvancedReport(advancedTeams);

        System.out.println("\n--- COMPARISON: Basic vs Advanced ---");
        List<Team> basicTeams = advancedBuilder.formBalancedTeams();
        System.out.println("Basic Algorithm: Focuses only on skill balance");
        System.out.println("Advanced Algorithm: Adds game variety + role diversity + personality mix");
    }

    private static void demonstrateConcurrency() {
        System.out.println("\n7. BASIC CONCURRENCY DEMONSTRATION");
        System.out.println("-----------------------------------");

        List<Participant> participants = Arrays.asList(
                new Participant("P001", "Alice", "alice@edu.com", "Valorant", 8, "Strategist", 95),
                new Participant("P002", "Bob", "bob@edu.com", "CS:GO", 6, "Attacker", 75),
                new Participant("P003", "Charlie", "charlie@edu.com", "DOTA 2", 7, "Defender", 65)
        );
        System.out.println("Starting team formation in background thread...");

        Thread backgroundThread = new Thread(() -> {
            try {
                Thread.sleep(1000);
                TeamBuilder builder = new TeamBuilder(participants, 2);
                List<Team> teams = builder.formBalancedTeams();
                System.out.println("✓ Background thread completed: Formed " + teams.size() + " teams");
            } catch (Exception e) {
                System.out.println("✗ Background thread error: " + e.getMessage());
            }
        });

        backgroundThread.start();
        System.out.println("Main thread continues working while teams form in background...");
        System.out.println("Doing other work...");

        try {
            backgroundThread.join();
            System.out.println("✓ Concurrency demonstration completed!");
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted");
        }
    }

    private static void demonstrateOrganizerWorkflow() {
        System.out.println("\n8. ORGANIZER WORKFLOW DEMONSTRATION");
        System.out.println("====================================");
        System.out.println("Demonstrating all Organizer use cases from UML diagrams...");

        try {
            // Create organizer (actor from use cases)
            Organizer organizer = new Organizer("ORG001", "Tournament Manager");

            // USE CASE 1: Upload CSV File
            organizer.uploadCSV("participants_sample.csv");

            // USE CASE 2: Define Team Formation Parameters
            organizer.setFormationParameters(4);

            // USE CASE 3: Run Team Formation Algorithm
            organizer.runTeamFormation();

            // USE CASE 5: View Formation Results
            organizer.viewFormationResults();

            // USE CASE: Generate Advanced Report
            organizer.generateAdvancedReport();

            // USE CASE 4: Save Teams to CSV
            organizer.saveTeams("organized_teams.csv");

            System.out.println("\nORGANIZER WORKFLOW COMPLETED!");
            System.out.println("All use cases successfully executed by Organizer actor");

        } catch (Exception e) {
            System.out.println("Organizer workflow failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void demonstrateCompleteCSVWorkflow() {
        System.out.println("\n📊 COMPLETE CSV WORKFLOW DEMONSTRATION");
        System.out.println("======================================");

        try {
            CSVHandler csvHandler = new CSVHandler();

            // 1. Load from input CSV
            System.out.println("1. Loading participants from CSV...");
            List<Participant> participants = csvHandler.loadParticipants("participants_sample.csv");
            System.out.println(" Loaded: " + participants.size() + " participants");

            // 2. Process teams
            System.out.println("2.Forming balanced teams...");
            TeamBuilder builder = new TeamBuilder(participants, 4);
            List<Team> teams = builder.formAdvancedTeams();
            System.out.println(" Formed: " + teams.size() + " teams");

            // 3. Save to output CSV
            System.out.println("3.Saving teams to output CSV...");
            csvHandler.saveTeamsToCSV(teams, "formed_teams.csv");
            System.out.println("Saved: formed_teams.csv");

            // 4. Show sample output
            System.out.println("\n4.Sample Team Output:");
            if (!teams.isEmpty()) {
                System.out.println(teams.get(0).toDetailedString());
            }

            System.out.println("\n🎉 CSV WORKFLOW COMPLETED SUCCESSFULLY!");
            System.out.println("Input: participants_sample.csv → Processing → Output: formed_teams.csv");

        } catch (Exception e) {
            System.out.println("CSV workflow failed: " + e.getMessage());
        }
    }
}