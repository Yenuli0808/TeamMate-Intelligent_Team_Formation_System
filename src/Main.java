import Constant.Constant;
import Gaming_Club_Model.Organizer;
import Gaming_Club_Model.Participant;
import Gaming_Club_Model.PersonalityType;
import Gaming_Club_Model.Team;
import Service.*;
import Service.Formattable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static CSVHandler csvHandler = new CSVHandler();
    private static List<Team> currentTeams = null;
    private static List<Participant> currentParticipants = null;
    private static final String PARTICIPANTS_CSV = "participants_sample.csv";

    public static void main(String[] args) {
        System.out.println("=====TeamMate: Intelligent Team Formation System=====");
        System.out.println("===================================================");

        showMainMenu();
    }

    private static void showMainMenu() {
        while (true) {
            System.out.println("\n==== MAIN MENU ===");
            System.out.println("1. Organizer Portal");
            System.out.println("2.Participant Portal");
            System.out.println("3. Run System Demonstration");
            System.out.println("4. Exist");
            System.out.print("Select option (1-4): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    organizerPortal();
                    break;
                case "2":
                    participantPortal();
                    break;
                case "3":
                    runAllDemonstration();
                    break;
                case "4":
                    System.out.println("Thank You For Using TeamMate!");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void organizerPortal() {
        System.out.println("===== ORGANIZER PORTAL =====");

        Organizer organizer = new Organizer("ORG001","Tournament Manager");

        while (true) {
            System.out.println("\nOrganizer Menu:");
            System.out.println("1. Upload CSV File");
            System.out.println("2. Set Team Formation Parameters");
            System.out.println("3. Run Team Formation Algorithm");
            System.out.println("4. View Formation Results");
            System.out.println("5. Save Teams to CSV");
            System.out.println("6. Generate Advanced Report");
            System.out.println("7. Refresh Participant Data");
            System.out.println("8. Show Participant Statistics");
            System.out.println("9. Return to Main Menu");
            System.out.print("Select option (1-9): ");

            String choice = scanner.nextLine().trim();

            try{
                switch (choice) {
                    case "1":
                        System.out.println("Enter CSV file path (e.g., participants_sample.csv): ");
                        String csvFile = scanner.nextLine().trim();
                        currentParticipants = organizer.uploadCSV(csvFile);
                        break;
                    case "2":
                        System.out.print("Enter Team Size (min: " + Constant.MIN_TEAM_SIZE + ", max: " + Constant.MAX_TEAM_SIZE + "): ");
                        int teamSize;
                        while (true){
                            try{
                                String teamSIzeInput = scanner.nextLine().trim();
                                teamSize = Integer.parseInt(teamSIzeInput);

                                organizer.setFormationParameters(teamSize);
                                break;
                            }catch (NumberFormatException e){
                                System.out.println("Please enter a valid number.");
                                System.out.print("Enter Team Size (min: " + Constant.MIN_TEAM_SIZE + ", max: " + Constant.MAX_TEAM_SIZE + "): ");
                            }catch (IllegalArgumentException e){
                                System.out.println( e.getMessage());
                                System.out.print("Enter Team Size (min: " + Constant.MIN_TEAM_SIZE + ", max: " + Constant.MAX_TEAM_SIZE + "): ");
                            }catch (IllegalStateException e) {
                                System.out.println( e.getMessage());
                                break;
                            }
                        }
                        break;
                    case "3":
                        currentTeams = organizer.runTeamFormation();
                        break;
                    case "4":
                        organizer.viewFormationResults();
                        break;
                    case "5":
                        System.out.print("Enter output file name (e.g., my_teams.csv): ");
                        String outputFile = scanner.nextLine().trim();
                        organizer.saveTeams(outputFile);
                        break;
                    case "6":
                        organizer.generateAdvancedReport();
                        break;
                    case "7":
                        currentParticipants = organizer.refreshParticipantData();
                        break;
                    case "8":
                        organizer.showParticipantStatistics();
                        break;
                    case "9":
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            }catch(Exception e){
                System.out.println("ERROR: " + e.getMessage());
                System.out.println("Please try again.");
            }
        }
    }

    private static void participantPortal() {
        System.out.println("===== PARTICIPANT PORTAL =====");
        System.out.println("1. Complete Survey");
        System.out.println("2. View Team Assignment");
        System.out.print("Select option (1-2): ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                completeSurvey();
                break;
            case "2":
                viewTeamAssignment();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void completeSurvey() {
        System.out.println("===== COMPLETE SURVEY =====");

        try {
            // Check if CSV file exists or create new one
            File csvFile = new File(PARTICIPANTS_CSV);
            if (!csvFile.exists()) {
                System.out.println("Creating new participants database...");
            }

            // Get existing participant IDs to avoid duplicates
            List<String> existingIds = new ArrayList<>();
            try {
                existingIds = csvHandler.getAllParticipantIds(PARTICIPANTS_CSV);
            } catch (IOException e) {
                System.out.println("Could not read existing participants, starting fresh...");
            }

            // Participant ID with validation
            String id;
            while (true) {
                System.out.print("Enter Participant ID (e.g., P102): ");
                id = scanner.nextLine().trim().toUpperCase();

                if (!id.matches("P\\d+")) {
                    System.out.println("Invalid ID format! Must start with 'P' followed by numbers (e.g., P102)");
                    continue;
                }
                if (id.isEmpty()) {
                    System.out.println("Participant ID cannot be empty. Please try again.");
                    continue;
                }

                if (existingIds.contains(id)) {
                    System.out.println("Participant ID '" + id + "' already exists. Please use a different ID.");
                    System.out.println("Available IDs: " + existingIds);
                    continue;
                }
                break;
            }

            //Participant name validation
            String name;
            while (true) {
                System.out.print("Enter Name: ");
                name = scanner.nextLine().trim();

                if (name.isEmpty()) {
                    System.out.println("Name cannot be empty. Please try again.");
                    continue;
                }
                if (!name.matches("[a-zA-Z\\s]+")) {
                    System.out.println("Name can only contain letters and spaces. Please try again.");
                    continue;
                }
                break;
            }

            //Email validation
            String email;
            while (true) {
                System.out.print("Enter Email: ");
                email = scanner.nextLine().trim();

                if (email.isEmpty()) {
                    System.out.println("Email cannot be empty. Please try again.");
                    continue;
                }
                if (!email.contains("@")) {
                    System.out.println("Valid email required (must contain '@'). Please try again.");
                    continue;
                }
                break;
            }

            //Phone Number Validation
            String phone;
            while (true) {
                System.out.print("Enter Phone Number: ");
                phone = scanner.nextLine().trim();
                if (!phone.matches("\\d{10}")) {
                    System.out.println("Phone number must be exactly 10 digits. Please try again.");
                    continue;
                }
                break;
            }

            System.out.println("\n--- Personality Survey ---");
            System.out.println("Rate each statement from 1 (Strongly Disagree) to 5 (Strongly Agree)");

            int[] responses = new int[5];
            String[] questions = {
                    "I enjoy taking the lead and guiding others during group activities.",
                    "I prefer analyzing situations and coming up with strategic solutions.",
                    "I work well with others and enjoy collaborative teamwork.",
                    "I am calm under pressure and can help maintain team morale.",
                    "I like making quick decisions and adapting in dynamic situations."
            };

            //rating validation
            for (int i = 0; i < 5; i++) {
                while (true) {
                    System.out.printf("\nQ%d: %s\n", i + 1, questions[i]);
                    System.out.print("Rating (1-5): ");
                    String ratingInput = scanner.nextLine().trim();

                    try {
                        int rating = Integer.parseInt(ratingInput);
                        if (rating < 1 || rating > 5) {
                            System.out.println("Rating must be between 1-5. Please try again.");
                            continue;
                        }
                        responses[i] = rating;
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid number between 1-5.");
                    }
                }
            }

            //Preferred Game Validation
            String game;
            while (true) {
                System.out.print("===========================================================================");
                System.out.print("\nGame selection options: Chess, FIFA, BasketBall, CS:GO, DOTA 2,Volorant");
                System.out.print("\nEnter Preferred Game: ");
                game = scanner.nextLine().trim();

                if (game.isEmpty()) {
                    System.out.println("Preferred game cannot be empty. Please try again.");
                    continue;
                }
                break;
            }

            //Preferred Role Validation
            String role;
            while (true) {
                System.out.print("=======================================================================================================");
                System.out.print("\nRole selection Options: \n ");
                System.out.println("1.Strategist: Focuses on tactics and planning. Keeps the bigger picture in mind during gameplay\n" +
                        "2.Attacker:Frontline player. Good reflexes, offensive tactics, quick execution.\n" +
                        "3.Defender: Protects and supports team stability. Good under pressure and team-focused\n" +
                        "4.Supporter: Jack-of-all-trades. Adapts roles, ensures smooth coordination\n" +
                        "5.Coordinator: Communication lead. Keeps the team informed and organized in real time\n");
                System.out.print("Enter Preferred Role: ");
                role = scanner.nextLine().trim();

                if (role.isEmpty()) {
                    System.out.println("Preferred role cannot be empty. Please try again.");
                    continue;
                }
                break;
            }

            //Skill Level Validation
            int skill;
            while (true) {
                System.out.print("========================================================================================================");
                System.out.print("\n Enter Skill Level (1-10): ");
                String skillInput = scanner.nextLine().trim();

                try {
                    skill = Integer.parseInt(skillInput);
                    if (skill < 1 || skill > 10) {
                        System.out.println("Skill level must be between 1-10. Please try again.");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number between 1-10.");
                }
            }

            // Processing Survey
            int personalityScore = PersonalityClassifier.calculateFromSurvey(responses);
            PersonalityType personalityType = PersonalityClassifier.classify(personalityScore);

            // Creating Participant
            Participant participant = new Participant(id, name, email, phone, game, skill, role, personalityScore);

            // Save to CSV file
            csvHandler.appendParticipantToCSV(participant, PARTICIPANTS_CSV);

            //Update current participants list if it exists
            if (currentParticipants != null) {
                currentParticipants.add(participant);
                System.out.println("Participant added to current session data");
            }

            System.out.println("\nSURVEY COMPLETED SUCCESSFULLY!");
            System.out.println("Personality Score: " + personalityScore);
            System.out.println("Personality Type: " + personalityType);
            System.out.println("Participant: " + participant.toDisplayString());
            System.out.println("Data saved to: " + PARTICIPANTS_CSV);

            // Show what happens next
            System.out.println("\nNext Steps:");
            System.out.println("1. Return to Organizer Portal");
            System.out.println("2. Upload CSV again to include new participant");
            System.out.println("3. Refresh Participant Data ");

        } catch (Exception e) {
            System.out.println("Survey Error: " + e.getMessage());
            System.out.println("Please try again with valid data.");
        }
    }

    private static void viewTeamAssignment(){
        System.out.println("===== VIEW TEAM ASSIGNMENT =====");

        // Check if teams are available
        if (currentTeams == null || currentTeams.isEmpty()) {
            System.out.println("No teams have been formed yet.");
            System.out.println("Please ask the organizer to run team formation first.");
            return;
        }

        // Initialize participant portal
        ParticipantPortal portal = new ParticipantPortal();
        portal.initializeData(currentTeams, currentParticipants);

        //Get ALL participant IDs from CSV file (including those not in current teams)
        List<String> allParticipantIdsInSystem = currentParticipants.stream()
                .map(Participant::getId)
                .collect(Collectors.toList());
        List<String> participantsWithTeams = new ArrayList<>(portal.getParticipantTeams().keySet());

        while(true){
            System.out.print("\nEnter Participant ID (e.g., P001) or 'exit' to quit: ");
            String searchInput = scanner.nextLine().trim();

            if (searchInput.equalsIgnoreCase("exit")) {
                System.out.println("Returning to main menu...");
                return;
            }

            // Validate ID format
            if (!isValidParticipantIdFormat(searchInput)) {
                System.out.println("Invalid ID format! Must start with 'P' followed by numbers (e.g., P001, P102)");
                showAvailableParticipants(participantsWithTeams, allParticipantIdsInSystem);
                continue;
            }
            String formattedId = searchInput.toUpperCase();

            // SCENARIO 1: Participant NOT in CSV (haven't completed survey)
            if(!allParticipantIdsInSystem.contains(formattedId)){
                System.out.println("\nParticipant ID '" + formattedId + "' not found in system.");
                System.out.println("This participant has not completed the survey yet.");
                System.out.println("  2. Fill out the personality survey and preferences");
                System.out.println("  3. Ask organizer to run team formation");
                System.out.println("  4. Come back here to view your team assignment");

                System.out.print("\nTry another Participant ID? (yes/no): ");
                String tryAgain = scanner.nextLine().trim();
                if (tryAgain.equalsIgnoreCase("yes") || tryAgain.equalsIgnoreCase("y")){
                    continue;
                }else{
                    return;
                }
            }

            // SCENARIO 2: Participant in CSV but NO team assignment
            if (!participantsWithTeams.contains(formattedId)){
                System.out.println("\n Participant '" + formattedId + "' found in system (survey completed).");
                System.out.println(" But no team assignment found for this participant.");

                System.out.println("\n Possible reasons:");
                System.out.println("  1. Team formation didn't include all participants");
                System.out.println("  2. Team size constraints excluded some participants");
                System.out.println("  3. Participant was added after last team formation");

                System.out.println("\n Suggestions:");
                System.out.println("  - Ask organizer to run team formation again");
                System.out.println("  - Check if all participant data is correct");

                System.out.print("\nTry another Participant ID? (yes/no): ");
                String tryAgain = scanner.nextLine().trim();
                if (tryAgain.equalsIgnoreCase("yes") || tryAgain.equalsIgnoreCase("y")){
                    continue;
                }else{
                    return;
                }
            }

            // SCENARIO 3: Participant has team assignment - SUCCESS!
            List<Participant> foundParticipants = portal.findParticipant(formattedId);
            if(!foundParticipants.isEmpty()){
                Participant participant = foundParticipants.get(0);
                System.out.println("\n Found participant: " + participant.toDisplayString());

                String teamDetails = portal.getTeamAssignmentDetails(participant.getId());
                System.out.println("\n" + teamDetails);
                break;
            }
            else {
                System.out.println("Unexpected error: Participant found but couldn't retrieve details.");
                continue;
            }
        }
    }

    private static void showAvailableParticipants(List<String> participantsWithTeams, List<String> allParticipantIdsInCSV) {
        System.out.println("\n Available Participants:");

        System.out.println(" Participants with teams (" + participantsWithTeams.size() + "):");
        if (participantsWithTeams.isEmpty()) {
            System.out.println("   None - run team formation first");
        } else {
            int maxToShow = Math.min(5, participantsWithTeams.size());
            for (int i = 0; i < maxToShow; i++) {
                System.out.println("   - " + participantsWithTeams.get(i));
            }
            if (participantsWithTeams.size() > 5) {
                System.out.println("   ... and " + (participantsWithTeams.size() - 5) + " more");
            }
        }

        System.out.println("\nTotal participants who completed survey: " + allParticipantIdsInCSV.size());
        System.out.println(" New participants should complete the survey first");
    }


    private static boolean isValidParticipantIdFormat(String id) {
        return id != null && id.matches("P\\d+");
    }

    private static void runAllDemonstration() {
        System.out.println("\n=== SYSTEM DEMONSTRATIONS ===");

        demonstratePersonalityClassification();
        demonstrateParticipantCreation();
        demonstrateTeamFormation();
        demonstrateInterfaces();
        demonstrateFileHandling();
        demonstrateAdvancedAlgorithm();
        demonstrateConcurrency();
        demonstrateOrganizerWorkflow();
        demonstrateCompleteCSVWorkflow();

        System.out.println("\n All demonstrations completed successfully!");
    }

    private static void demonstratePersonalityClassification() {
        System.out.println("\n1. PERSONALITY CLASSIFICATION DEMONSTRATION");
        System.out.println("--------------------------------------------");

        int[] testScores = {95,80,65,45,100,75};

        for (int score: testScores) {
            try{
                var personality = PersonalityClassifier.classify(score);
                System.out.printf("Score %3d -> %-8s(Range: %d-%d)%n",score,personality,personality.getMinScore(),personality.getMaxScore());
            }catch (IllegalArgumentException e){
                System.out.printf("Score %3d -> ERROR: %s%n", score, e.getMessage());
            }
        }
        System.out.println();
    }

    private static void demonstrateParticipantCreation() {
        System.out.println("\n2. PARTICIPANT CREATION DEMONSTRATION");
        System.out.println("------------------------------------------");

        try{
            Participant participant1 = new Participant("P001","John Doe","john@university.edu","0731093108","Valorant",8,"Strategist",85);
            System.out.println("Participant 1 created: " + participant1);

            try{
                Participant invalid= new Participant("P002","Jane Smith","jane@university.edu","0701053109","CS:G0",15,"Attacker",85);
            }catch (IllegalArgumentException e){
                System.out.println("Rejected Invalid Participant: " + e.getMessage());
            }
        }catch (Exception e){
            System.out.println("Error creating Participant: " + e.getMessage());
        }

        System.out.println();
    }

    private static void demonstrateTeamFormation() {
        System.out.println("\n3. TEAM FORMATION ALGORITHM DEMONSTRATION");
        System.out.println("----------------------------------------");

        try{
            List<Participant> participants = Arrays.asList(
                    new Participant("P001", "Alice", "alice@edu.com","07167875649", "Valorant", 8, "Strategist", 95),
                    new Participant("P002", "Bob", "bob@edu.com","07123149065", "CS:GO", 6, "Attacker", 75),
                    new Participant("P003", "Charlie", "charlie@edu.com","0724865213", "DOTA 2", 7, "Defender", 65),
                    new Participant("P004", "Diana", "diana@edu.com","0701053409", "Valorant", 9, "Supporter", 85),
                    new Participant("P005", "Eve", "eve@edu.com","0741065169", "CS:GO", 5, "Coordinator", 70),
                    new Participant("P006", "Frank", "frank@edu.com","0771053109", "DOTA 2", 8, "Strategist", 90)
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
            System.out.println("Error in team formation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void demonstrateInterfaces() {
        System.out.println("\n4. INTERFACES DEMONSTRATION");
        System.out.println("--------------------------------------");

        List<Participant> participants = Arrays.asList(
                new Participant("P001", "Alice", "alice@edu.com","0751053107", "Valorant", 8, "Strategist", 95),
                new Participant("P002", "Bob", "bob@edu.com","0741053199", "CS:GO", 6, "Attacker", 75)
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

    private static void demonstrateFileHandling() {
        System.out.println("\n5. FILE HANDLING DEMONSTRATION");
        System.out.println("--------------------------------------");

        try{
            CSVHandler csvHandler = new CSVHandler();

            System.out.println("Loading Participant from CSV file...");
            List<Participant> participants = csvHandler.loadParticipants("participants_sample.csv");
            System.out.printf("Successfully loaded %d participants from CSV file%n", participants.size());

            TeamBuilder builder = new TeamBuilder(participants, 4);
            List<Team> teams = builder.formBalancedTeams();

            System.out.println("Saving teams to output CSV...");
            csvHandler.saveTeamsToCSV(teams, "formed_teams.csv");
            System.out.println("SUCCESS: Saved " + teams.size() + " teams to 'formed_teams.csv'");

            System.out.println("\nSample of loaded data (first 3 participants):");
            for (int i = 0; i < Math.min(3, participants.size()); i++) {
                System.out.println("  - " + participants.get(i).toDisplayString());
            }
        }catch (Exception e){
            System.out.println("FILE HANDLING ERROR: " + e.getMessage());
            System.out.println("Note: Make sure 'participants_sample.csv' is in your project root directory");
        }
    }

    private static void demonstrateAdvancedAlgorithm(){
        System.out.println("\n6. ADVANCED ALGORITHM DEMONSTRATION");
        System.out.println("-------------------------------------");

        List<Participant> participants = Arrays.asList(
                new Participant("P001", "Alice", "alice@edu.com","0711153109", "Valorant", 8, "Strategist", 95),
                new Participant("P002", "Bob", "bob@edu.com","0721253109", "Valorant", 6, "Attacker", 75),
                new Participant("P003", "Charlie", "charlie@edu.com","0731353109", "Valorant", 7, "Defender", 65),
                new Participant("P004", "Diana", "diana@edu.com", "0741453109","CS:GO", 9, "Supporter", 85),
                new Participant("P005", "Eve", "eve@edu.com","0751553109", "CS:GO", 5, "Coordinator", 92),
                new Participant("P006", "Frank", "frank@edu.com","0761653109", "DOTA 2", 8, "Strategist", 88)
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

    private static void demonstrateConcurrency(){
        System.out.println("\n7. BASIC CONCURRENCY DEMONSTRATION");
        System.out.println("-----------------------------------");

        List<Participant> participants = Arrays.asList(
                new Participant("P001", "Alice", "alice@edu.com","0771753109", "Valorant", 8, "Strategist", 95),
                new Participant("P002", "Bob", "bob@edu.com","0791953109", "CS:GO", 6, "Attacker", 75),
                new Participant("P003", "Charlie", "charlie@edu.com","0781853109", "DOTA 2", 7, "Defender", 65)
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

    private static void demonstrateOrganizerWorkflow(){
        System.out.println("\n8. ORGANIZER WORKFLOW DEMONSTRATION");
        System.out.println("====================================");

        try {
            Organizer organizer = new Organizer("ORG001", "Tournament Manager");

            organizer.uploadCSV("participants_sample.csv");
            organizer.setFormationParameters(4);
            organizer.runTeamFormation();
            organizer.viewFormationResults();
            organizer.generateAdvancedReport();
            organizer.saveTeams("organized_teams.csv");

            System.out.println("\n ORGANIZER WORKFLOW COMPLETED!");
        } catch (Exception e) {
            System.out.println("Organizer workflow failed: " + e.getMessage());
        }
    }

    private static void demonstrateCompleteCSVWorkflow(){
        System.out.println("\n9. COMPLETE CSV WORKFLOW DEMONSTRATION");
        System.out.println("======================================");

        try {
            CSVHandler csvHandler = new CSVHandler();

            System.out.println("1. Loading participants from CSV...");
            List<Participant> participants = csvHandler.loadParticipants("participants_sample.csv");
            System.out.println(" Loaded: " + participants.size() + " participants");

            System.out.println("2. Forming balanced teams...");
            TeamBuilder builder = new TeamBuilder(participants, 4);
            List<Team> teams = builder.formAdvancedTeams();
            System.out.println(" Formed: " + teams.size() + " teams");

            System.out.println("3. Saving teams to output CSV...");
            csvHandler.saveTeamsToCSV(teams, "complete_workflow_teams.csv");
            System.out.println(" Saved: complete_workflow_teams.csv");

            System.out.println("\n4. Sample Team Output:");
            if (!teams.isEmpty()) {
                System.out.println(teams.get(0).toDetailedString());
            }

            System.out.println("\n✓ CSV WORKFLOW COMPLETED SUCCESSFULLY!");
        } catch (Exception e) {
            System.out.println("CSV workflow failed: " + e.getMessage());
        }

    }

}