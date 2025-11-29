package Service;

import Constant.Constant;
import Gaming_Club_Model.Participant;
import Gaming_Club_Model.PersonalityType;
import Gaming_Club_Model.Survey;
import Gaming_Club_Model.Team;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class TeamBuilder implements TeamFormationStrategy {
    private int teamSize;
    private final List<Participant> participants;
    private TeamFormationConstraint constraints;
    private ExecutorService executor;

    public TeamBuilder(List<Participant> participants, int teamSize) {
        // Message 7.1-7.2: Internal validation( set team formation parameters)
        if(participants == null || participants.isEmpty()) {
            throw new IllegalArgumentException("Participants cannot be empty");
        }
        if(teamSize < Constant.MIN_TEAM_SIZE) {
            throw new IllegalArgumentException("Team size cannot be less than " + Constant.MIN_TEAM_SIZE + ". Minimum team size is " + Constant.MIN_TEAM_SIZE + " players.");
        }
        if(teamSize > Constant.MAX_TEAM_SIZE) {
            throw new IllegalArgumentException("Team size cannot be greater than " + Constant.MAX_TEAM_SIZE + ". Maximum team size is " + Constant.MAX_TEAM_SIZE + " players.");
        }
        if(teamSize > participants.size()) {
            throw new IllegalArgumentException("Team size cannot be greater than number of participants");
        }

        this.participants = Collections.synchronizedList(new ArrayList<>(participants));
        this.teamSize = teamSize;
        this.constraints = new DefaultTeamConstraints();
        this.executor = Executors.newFixedThreadPool(2); // For concurrency
    }

    // ===== STRATEGY PATTERN IMPLEMENTATION =====
    @Override
    public List<Team> formTeams() {
        return formBalancedTeams();
    }

    @Override
    public String getStrategyName() {
        return "Advanced Balanced Team Formation";
    }

    // DIFFERENT FORMATION STRATEGIES
    public List<Team> formBalancedTeams() {
        int teamCount = (int) Math.ceil((double) participants.size() / teamSize);
        List<Team> teams = createEmptyTeams(teamCount);
        List<Participant> sortedParticipants = sortBySkill(participants);
        distributeParticipants(teams, sortedParticipants);
        return teams;
    }

    public List<Team> formAdvancedTeams() {
        int teamCount = (int) Math.ceil((double) this.participants.size() / teamSize);
        List<Team> teams = createEmptyTeams(teamCount);  // sequence 5.1: run team formation

        System.out.println("Applying Advanced Constraints:");
        System.out.println("1. One leader per team maximum");
        System.out.println("2. Max " + constraints.getMaxSameGame() + " players from same game");
        System.out.println("3. At least " + constraints.getMinDifferentRoles() + " different roles");
        System.out.println("4. Balanced personality mix");

        // Advanced distribution with constraints
        distributeWithAdvancedConstraints(teams);
        return teams;
    }

    // CONCURRENT PROCESSING
    public CompletableFuture<List<Team>> formTeamsConcurrently() {    //sequence 5:run team formation
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("\nProcessing team formation in background thread...");
            try {
                // Simulate processing time for large datasets
                Thread.sleep(500);
                System.out.println("✓ Background processing completed\n");
                return formAdvancedTeams();
            } catch (InterruptedException e) {
                throw new RuntimeException("Team formation interrupted", e);
            }
        }, executor);
    }

    public CompletableFuture<Void> processSurveyDataConcurrently(List<Participant> surveyData) {
        return CompletableFuture.runAsync(() -> {
            System.out.println("\nProcessing survey data for " + surveyData.size() + " participants...");
            try {
                // Simulate survey data processing
                Thread.sleep(300);
                this.participants.addAll(surveyData);
                System.out.println("✓ Survey data processing completed");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Survey processing interrupted", e);
            }
        }, executor);
    }

    // ===== ADVANCED CONSTRAINT-BASED DISTRIBUTION =====
    private void distributeWithAdvancedConstraints(List<Team> teams) {  //sequence 5.2-run team formation
        // Phase 1: Distribute leaders first (one per team)
        distributeLeaders(teams);

        // Phase 2: Distribute remaining participants with constraints
        distributeRemainingWithConstraints(teams);
    }

    private void distributeLeaders(List<Team> teams) {
        List<Participant> leaders = participants.stream()
                .filter(p -> p.getPersonalityType() == PersonalityType.LEADER)
                .toList();

        int teamIndex = 0;
        for (Participant leader : leaders) {
            if (teamIndex < teams.size()) {
                Team team = teams.get(teamIndex);
                if (!team.isFull() && constraints.satisfiesAllConstraints(team, leader)) {
                    team.addMember(leader);
                    teamIndex++;
                }
            }
        }
    }

    private void distributeRemainingWithConstraints(List<Team> teams) {
        List<Participant> remaining = participants.stream()
                .filter(p -> !isInAnyTeam(teams, p))
                .sorted((p1, p2) -> Integer.compare(p2.getSkillLevel(), p1.getSkillLevel())) // High skill first
                .toList();

        for (Participant player : remaining) {
            Team bestTeam = findOptimalTeam(teams, player);
            if (bestTeam != null) {
                bestTeam.addMember(player);
            } else {
                // Fallback: add to first available team
                teams.stream()
                        .filter(team -> !team.isFull())
                        .findFirst()
                        .ifPresent(team -> team.addMember(player));
            }
        }
    }

    private Team findOptimalTeam(List<Team> teams, Participant player) {
        // Score teams based on how well they match constraints
        Map<Team, Integer> teamScores = new HashMap<>();

        for (Team team : teams) {
            if (!team.isFull() && constraints.satisfiesBasicConstraints(team, player)) {
                int score = calculateTeamScore(team, player);
                teamScores.put(team, score);
            }
        }

        return teamScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

   private int calculateTeamScore(Team team, Participant newPlayer) {
       int score = 0;

       // PERSONALITY BALANCE
       int currentLeaders = team.countPersonalityType(PersonalityType.LEADER);
       int currentThinkers = team.countPersonalityType(PersonalityType.THINKER);
       int currentBalanced = team.countPersonalityType(PersonalityType.BALANCED);

       // PERSONALITY SCORING
       if (newPlayer.getPersonalityType() == PersonalityType.LEADER) {
           if (currentLeaders == 0) {
               score += 100;  //  Team needs a leader
           } else if (currentLeaders == 1) {
               score += 20;   //  Second leader okay if needed
           } else {
               score -= 80;   // PENALTY: Third+ leader
           }
       }

       if (newPlayer.getPersonalityType() == PersonalityType.THINKER) {
           if (currentThinkers == 0) {
               score += 80;   // Team needs at least one thinker
           } else if (currentThinkers == 1) {
               score += 50;   // Good: Second thinker is ideal
           } else {
               score += 10;   // Low: Third thinker is okay
           }
       }

       if (newPlayer.getPersonalityType() == PersonalityType.BALANCED) {
           if (currentBalanced < 2) {
               score += 60;   //  Team needs balanced players
           } else if (currentBalanced < 3) {
               score += 30;   //  More balanced players good
           } else {
               score += 5;    //  Enough balanced players already
           }
       }

       // ROLE DIVERSITY
       int currentRoleCount = team.getUniqueRoles().size();
       boolean addsNewRole = !team.getUniqueRoles().contains(newPlayer.getPreferredRole());

       if (addsNewRole) {
           if (currentRoleCount < 2) {
               score += 90;   // Team desperately needs role diversity
           } else if (currentRoleCount < 3) {
               score += 70;   // Team needs to reach minimum 3 roles
           } else {
               score += 30;   // Additional role diversity is beneficial
           }
       } else {
           if (currentRoleCount < 3) {
               score -= 40;   // if team have few roles and it has same role
           }
       }

       // GAME VARIETY
       long sameGameCount = countSameGame(team, newPlayer);
       if (sameGameCount == 0) {
           score += 25;  // Different game
       } else if (sameGameCount == 1) {
           score += 10;  // Second player with same game
       } else {
           score -= 30;  // wrong: Third+ player with same game
       }

       // SKILL BALANCE
       double currentAvg = team.getAverageSkill();
       double globalAvg = calculateGlobalAverage();

       // Calculate what average would be after adding this player
       int teamSize = team.getCurrentSize();
       double newPotentialAvg = (currentAvg * teamSize + newPlayer.getSkillLevel()) / (teamSize + 1);

       // Prefer teams that are closer to global average after adding player
       double distanceFromGlobal = Math.abs(newPotentialAvg - globalAvg);
       if (distanceFromGlobal < 0.5) score += 40;
       else if (distanceFromGlobal < 1.0) score += 20;
       else if (distanceFromGlobal > 2.0) score -= 20;

       // Rescue very low skill teams
       if (currentAvg < 4.0 && newPlayer.getSkillLevel() > 5) {
           score += 50;
       }

       // Prevent very high skill teams
       if (currentAvg > 7.0 && newPlayer.getSkillLevel() > 7) {
           score -= 40;
       }

       // TEAM SIZE CONSIDERATION
       if (team.getCurrentSize() < 2) {
           // For first 2 players, be more flexible with constraints
           score += 15;
       }

       return score;
   }

    private double calculateGlobalAverage() {
        if (participants.isEmpty()) return 0.0;
        int totalSkill = participants.stream()
                .mapToInt(Participant::getSkillLevel)
                .sum();
        return (double) totalSkill / participants.size();
    }

    public void checkRoleDiversity(List<Team> teams) {
        System.out.println("\n=== ROLE DIVERSITY CHECK ===");
        for (Team team : teams) {
            int roleCount = team.getUniqueRoles().size();
            int minRequired = Math.min(Constant.MIN_DIFFERENT_ROLES, team.getMaxSize());
            String status = roleCount >= minRequired ? "✅ PASS" : "⚠️ IDEAL NOT MET";
            System.out.printf("%s: %d unique roles (ideal: %d) - %s%n",
                    team.getName(), roleCount, minRequired, status);
        }
    }

    private int calculatePersonalityScore(Team team, Participant newPlayer) {
        int score = 0;
        PersonalityType newType = newPlayer.getPersonalityType();

        int currentLeaders = team.countPersonalityType(PersonalityType.LEADER);
        int currentThinkers = team.countPersonalityType(PersonalityType.THINKER);
        int currentBalanced = team.countPersonalityType(PersonalityType.BALANCED);

        // Prefer balanced personality distribution
        if (newType == PersonalityType.LEADER && currentLeaders == 0) score += 25;
        if (newType == PersonalityType.THINKER && currentThinkers < 2) score += 15;
        if (newType == PersonalityType.BALANCED && currentBalanced < 3) score += 10;

        return score;
    }

    // CORE DISTRIBUTION LOGIC
    private void distributeParticipants(List<Team> teams, List<Participant> participants) {
        int teamCount = teams.size();
        int participantIndex = 0;

        // Snake distribution for balanced skills
        while (participantIndex < participants.size()) {
            // Forward pass
            for (int i = 0; i < teamCount && participantIndex < participants.size(); i++) {
                Team team = teams.get(i);
                if (!team.isFull()) {
                    team.addMember(participants.get(participantIndex));
                    participantIndex++;
                }
            }
            // Backward pass
            for (int i = teamCount - 1; i >= 0 && participantIndex < participants.size(); i--) {
                Team team = teams.get(i);
                if (!team.isFull()) {
                    team.addMember(participants.get(participantIndex));
                    participantIndex++;
                }
            }
        }
    }

    //UTILITY METHODS
    private List<Team> createEmptyTeams(int teamCount) {
        List<Team> teams = new ArrayList<>();
        for (int i = 0; i < teamCount; i++) {
            String teamId = "T" + (i + 1);
            String teamName = "Team " + (i + 1);   // sequence 5.1(run team formation)
            teams.add(new Team(teamId, teamName, teamSize));
        }
        return teams;
    }

    private List<Participant> sortBySkill(List<Participant> participants) {
        List<Participant> sorted = new ArrayList<>(participants);
        sorted.sort(Comparator.comparingInt(Participant::getSkillLevel));
        return sorted;
    }

    private int countSameGame(Team team, Participant player) {
        return (int) team.getMembers().stream()
                .filter(member -> member.getPreferredGame().equals(player.getPreferredGame()))
                .count();
    }

    private boolean hasSameRole(Team team, Participant player) {
        return team.getMembers().stream()
                .anyMatch(member -> member.getPreferredRole().equals(player.getPreferredRole()));
    }

    private boolean isInAnyTeam(List<Team> teams, Participant player) {
        return teams.stream()
                .anyMatch(team -> team.getMembers().contains(player));
    }

    // REPORTING METHODS
    public void printBalanceReport(List<Team> teams) {
        System.out.println("\n=== TEAM BALANCE REPORT ===");

        for (Team team : teams) {
            System.out.println("\n" + team.getName() + ":");
            System.out.println("  Size: " + team.getCurrentSize() + "/" + team.getMaxSize());
            System.out.println("  Average Skill: " + String.format("%.2f", team.getAverageSkill()));

            int leaders = team.countPersonalityType(PersonalityType.LEADER);
            int balanced = team.countPersonalityType(PersonalityType.BALANCED);
            int thinkers = team.countPersonalityType(PersonalityType.THINKER);

            System.out.println("  Personalities: " + leaders + " Leaders, " + balanced + " Balanced, " + thinkers + " Thinkers");
            System.out.println("  Games: " + team.getUniqueGames());
            System.out.println("  Roles: " + team.getUniqueRoles());
        }
    }

    public void printAdvancedReport(List<Team> teams) {
        System.out.println("\n=== ADVANCED TEAM ANALYSIS ===");
        System.out.println("Constraints Applied: Leader limit, Game variety, Role diversity, Personality mix");

        for (Team team : teams) {
            System.out.println("\n" + team.getName() + ":");
            System.out.println("  Players: " + team.getCurrentSize() + "/" + team.getMaxSize());
            System.out.println("  Avg Skill: " + String.format("%.1f", team.getAverageSkill()));

            int leaders = team.countPersonalityType(PersonalityType.LEADER);
            int balanced = team.countPersonalityType(PersonalityType.BALANCED);
            int thinkers = team.countPersonalityType(PersonalityType.THINKER);
            System.out.println("  Personalities: " + leaders + "L " + balanced + "B " + thinkers + "T");

            System.out.println("  Games: " + team.getUniqueGames());
            System.out.println("  Roles: " + team.getUniqueRoles());

            // Constraint validation
            System.out.print("  Constraints: ");
            System.out.print(leaders <= 1 ? "✓Leader " : "✗Leader ");
            System.out.print(team.getUniqueGames().size() >= Math.min(2, team.getCurrentSize()) ? "✓Games " : "✗Games ");
            System.out.print(team.getUniqueRoles().size() >= Math.min(3, team.getCurrentSize()) ? "✓Roles " : "✗Roles ");
            System.out.print(isPersonalityBalanced(team) ? "✓Personality" : "✗Personality");
            System.out.println();
        }
    }

    private boolean isPersonalityBalanced(Team team) {
        int leaders = team.countPersonalityType(PersonalityType.LEADER);
        int thinkers = team.countPersonalityType(PersonalityType.THINKER);
        int balanced = team.countPersonalityType(PersonalityType.BALANCED);

        return leaders <= 1 && thinkers >= 0 && balanced >= 1;
    }

    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    public int getTeamSize() {   // sequence no 3(run team formation)
        return teamSize;
    }

    public int getParticipantCount() {    // sequence no 4(run team formation)
        return participants.size();
    }

    public TeamFormationConstraint getConstraints() {
        return constraints;
    }
}