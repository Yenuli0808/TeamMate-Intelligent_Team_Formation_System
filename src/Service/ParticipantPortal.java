package Service;

import Gaming_Club_Model.Participant;
import Gaming_Club_Model.Team;

import java.util.*;
import java.util.stream.Collectors;

public class ParticipantPortal {
    private Map<String, Team> participantTeams;  // this will map participant ID to Team

    private Map<String, Participant> allParticipants;  //Stores all participants for lookup

    public ParticipantPortal() {
        this.participantTeams = new HashMap<>();
        this.allParticipants = new HashMap<>();
    }

    //Initialize with teams and participants data
    public void initializeData(List<Team> teams, List<Participant> participants){
        if (teams == null || participants == null){
            throw new IllegalArgumentException("Teams and Participants can not be null");
        }

        System.out.println("Initializing with " + teams.size() + " teams and " + participants.size() + " participants");

        // Clear previous data
        participantTeams.clear();
        allParticipants.clear();

        for(Participant participant : participants){
            allParticipants.put(participant.getId(), participant);    // In here we will store all participant for look up
        }

        int totalMappings = 0;
        for(Team team : teams){
            for (Participant member : team.getMembers()) {
                participantTeams.put(member.getId(), team);
                totalMappings++;
            }
        }
        System.out.println("Created " + totalMappings + " participant-team mappings");
    }

    public Team viewTeamAssignment(String participantId) {
        if(participantId == null || participantId.trim().isEmpty()){
            throw new IllegalArgumentException("Participant ID cannot be empty");
        }

        String searchId = participantId.trim().toUpperCase();

        Team team = participantTeams.get(searchId);
        if (team == null) {
            team = participantTeams.entrySet().stream()
                    .filter(entry -> entry.getKey().equalsIgnoreCase(searchId))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(null);
        }
        if (team == null) {
            // Show available IDs for debugging
            System.out.println("Available participant IDs in teams:");
            participantTeams.keySet().stream()
                    .sorted()
                    .limit(10)
                    .forEach(id -> {
                        Participant p = allParticipants.get(id);
                        String name = (p != null) ? p.getName() : "Unknown";
                        System.out.println("  - " + id + " (" + name + ")");
                    });
            throw new IllegalArgumentException("No team assignment found for participant ID: " + participantId);
        }
        return team;
    }

    public String getTeamAssignmentDetails(String participantId) {   //sequence 3.5.5.2(view team assignment)
        try{
            Team team = viewTeamAssignment(participantId);     //sequence 3.5.5.2.2(view team assignment)
            Participant participant = allParticipants.get(participantId.toUpperCase());

            if (participant == null) {
                // Try to find participant with different case
                //sequence 3.5.5.2.3(view team assignment)
                participant = allParticipants.entrySet().stream()
                        .filter(entry -> entry.getKey().equalsIgnoreCase(participantId))
                        .map(Map.Entry::getValue)
                        .findFirst()
                        .orElse(null);

                if (participant == null) {
                    throw new IllegalArgumentException("Participant not found: " + participantId);
                }
            }
            return formatTeamDetailsForParticipant(team,participant);    //sequence 3.5.5.2.4(view team assignment)
        }
        catch (Exception e){
            throw new IllegalArgumentException("Cannot get team assignment: " + e.getMessage());
        }
    }

    //sequence 3.5.5.2.4(view team assignment)
    private String formatTeamDetailsForParticipant(Team team,Participant participant) {
        StringBuilder details = new StringBuilder();

        details.append("=== YOUR TEAM ASSIGNMENT ===\n");
        details.append("Team: ").append(team.getName()).append(" (").append(team.getId()).append(")\n");
        details.append("Team Size: ").append(team.getCurrentSize()).append("/").append(team.getMaxSize()).append("\n");
        details.append("Average Team Skill: ").append(String.format("%.1f", team.getAverageSkill())).append("\n\n");

        //Participant information
        //sequence 3.5.5.2.4.1(view team assignment)
        details.append("YOUR INFORMATION:\n");
        details.append("  - ").append(participant.toDisplayString()).append("\n\n");

        //Team members details
        details.append("YOUR TEAMMATES:\n");
        List<Participant> teammates = team.getMembers().stream()
                .filter(member -> !member.getId().equalsIgnoreCase(participant.getId()))
                .collect(Collectors.toList());

        //sequence 3.5.5.2.4.2(view team assignment)
        if (teammates.isEmpty()) {
            details.append("  No other team members assigned yet.\n");
        } else {
            for (Participant teammate : teammates) {
                details.append("  - ").append(teammate.toDisplayString()).append("\n");   //sequence 3.5.5.2.4.3(view team assignment)
            }
        }

        //Team compositions
        //sequence 3.5.5.2.4.4, 3.5.5.2.4.5(view team assignment)
        details.append("\nTEAM COMPOSITION:\n");
        details.append("  Games: ").append(team.getUniqueGames()).append("\n");
        details.append("  Roles: ").append(team.getUniqueRoles()).append("\n");

        //Personality Distributions
        //sequence 3.5.5.2.4.6-3.5.5.2.4.8(view team assignment)
        int leaders = team.countPersonalityType(Gaming_Club_Model.PersonalityType.LEADER);
        int balanced = team.countPersonalityType(Gaming_Club_Model.PersonalityType.BALANCED);
        int thinkers = team.countPersonalityType(Gaming_Club_Model.PersonalityType.THINKER);
        details.append("  Personalities: ").append(leaders).append(" Leaders, ").append(balanced).append(" Balanced, ").append(thinkers).append(" Thinkers\n");

        //Contact Info for teammates
        details.append("\n TEAM CONTACTS:\n");
        for(Participant member : team.getMembers()){
            if(!member.getId().equals(participant.getId())){
                details.append("  - ").append(member.getName()).append(": ").append(member.getEmail()).append(" | Game: ").append(member.getPreferredGame()).append("\n");
            }
        }
        return details.toString();
    }

    //Search for participant by name or partial ID
    public List<Participant> findParticipant(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String term = searchTerm.toLowerCase().trim();
        return allParticipants.values().stream()
                .filter(p -> p.getId().toLowerCase().contains(term) ||
                        p.getName().toLowerCase().contains(term) ||
                        p.getEmail().toLowerCase().contains(term))
                .collect(Collectors.toList());
    }

    //checking weather participant has team assignments
    public boolean hasTeamAssignment(String participantId) {
        if (participantId == null) return false;
        return participantTeams.containsKey(participantId.toUpperCase());
    }

    public Map<String, String> getAllTeamAssignments() {
        Map<String, String> assignments = new HashMap<>();
        for (Map.Entry<String, Team> entry : participantTeams.entrySet()) {
            assignments.put(entry.getKey(), entry.getValue().getName() + " (" + entry.getValue().getId() + ")");
        }
        return assignments;
    }

    public String getTeamStatistics(String participantId) {
        Team team = viewTeamAssignment(participantId);

        StringBuilder stats = new StringBuilder();
        stats.append("=== TEAM STATISTICS ===\n");
        stats.append("Team: ").append(team.getName()).append("\n");
        stats.append("Members: ").append(team.getCurrentSize()).append("/").append(team.getMaxSize()).append("\n");
        stats.append("Average Skill: ").append(String.format("%.2f", team.getAverageSkill())).append("\n");
        stats.append("Games: ").append(team.getUniqueGames().size()).append(" unique games\n");
        stats.append("Roles: ").append(team.getUniqueRoles().size()).append(" different roles\n");

        // Skill range
        OptionalInt minSkill = team.getMembers().stream().mapToInt(Participant::getSkillLevel).min();
        OptionalInt maxSkill = team.getMembers().stream().mapToInt(Participant::getSkillLevel).max();

        if (minSkill.isPresent() && maxSkill.isPresent()) {
            stats.append("Skill Range: ").append(minSkill.getAsInt()).append(" - ").append(maxSkill.getAsInt()).append("\n");
        }

        return stats.toString();
    }

    public Map<String, Team> getParticipantTeams() {
        return Collections.unmodifiableMap(participantTeams);
    }

    public Map<String, Participant> getAllParticipants() {
        return Collections.unmodifiableMap(allParticipants);
    }

    public int getTotalAssignments() {
        return participantTeams.size();
    }
}
