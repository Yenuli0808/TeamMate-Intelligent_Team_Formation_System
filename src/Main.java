import Gaming_Club_Model.Participant;
import Gaming_Club_Model.Team;
import Service.PersonalityClassifier;
import Service.TeamBuilder;

import java.util.Arrays;
import java.util.List;

public class Main{
    public static void main(String[] args) {
        System.out.println("=== TeamMate: Intelligent Team Formation System ===");
        System.out.println("\n Demonstrating Core OOP Concepts and Functionality \n");

        demonstratePersonalityClassification();
        demonstrateParticipantCreation();
        demonstrateTeamFormation();

        System.out.println("\n=== Core Logic Implementation Complete ===");
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
}