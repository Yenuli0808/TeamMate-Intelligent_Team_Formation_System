package Service;

import Gaming_Club_Model.Participant;
import Gaming_Club_Model.Team;

import java.util.*;
import java.util.stream.Collectors;

public class ParticipantPortal {
    private Map<String, Team> participantTeams;  // this will map participant ID to Team

    private Map<String, Participant> allParticipants;

    public ParticipantPortal() {
        this.participantTeams = new HashMap<>();
        this.allParticipants = new HashMap<>();
    }

    //Initialize with teams and participants data
    public void initializeData(List<Team> teams, List<Participant> participants){
        if (teams == null || participants == null){
            throw new IllegalArgumentException("Teams and Participants are null");
        }

        for(Participant participant : participants){
            allParticipants.put(participant.getId(), participant);    // In here we will store all participant for look up
        }
        for(Team team : teams){
            for(Participant participant : participants){
                allParticipants.put(participant.getId(), participant);  // build participant to team mapping
            }
        }
        System.out.println("Participant portal initialized with " + teams.size() + " teams and " + participants.size() + " participants");
    }

    public Team viewTeamAssignment(String participantId) {
        if(participantId == null || participantId.trim().isEmpty()){
            throw new IllegalArgumentException("Participant ID cannot be empty");
        }

        Team team = participantTeams.get(participantId.trim());
        if (team == null) {
            throw new IllegalArgumentException("No team assignment found for participant ID: " + participantId);
        }
        return team;
    }

    public String getTeamAssignmentDetails(String participantId) {
        try{
            Team team = viewTeamAssignment(participantId);
            Participant participant = allParticipants.get(participantId);

            if(participant == null){
                throw new IllegalArgumentException("No participant found for participant ID: " + participantId);
            }
            return formatTeamDetailsForParticipant(team,participant);
        }
        catch (Exception e){
            throw new IllegalArgumentException("Cannot get team assignment: " + e.getMessage());
        }
    }

    private String formatTeamDetailsForParticipant(Team team,Participant participant) {
        StringBuilder details = new StringBuilder();

        details.append("=== YOUR TEAM ASSIGNMENT ===\n");
        details.append("Team: ").append(team.getName()).append(" (").append(team.getId()).append(")\n");
        details.append("Team Size: ").append(team.getCurrentSize()).append("/").append(team.getMaxSize()).append("\n");
        details.append("Average Team Skill: ").append(String.format("%.1f", team.getAverageSkill())).append("\n\n");

        //Participant information
        details.append("YOUR INFORMATION:\n");
        details.append("  - ").append(participant.toDisplayString()).append("\n\n");

        //Team members details
        details.append("YOUR TEAMMATES:\n");
        List<Participant> teammates = team.getMembers().stream().filter(member -> !member.getId().equals(participant.getId())).collect(Collectors.toList());

        if (teammates.isEmpty()) {
            details.append("  No other team members assigned yet.\n");
        } else {
            for (Participant teammate : teammates) {
                details.append("  - ").append(teammate.toDisplayString()).append("\n");
            }
        }

        //Team compositions
        details.append("\nTEAM COMPOSITION:\n");
        details.append("  Games: ").append(team.getUniqueGames()).append("\n");
        details.append("  Roles: ").append(team.getUniqueRoles()).append("\n");

        //Personality Distributions
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

    //checking weather participant has team assignments
    public boolean hasTeamAssignment(String participantId) {
        return participantTeams.containsKey(participantId);
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

    public int getTotalAssignments() {
        return participantTeams.size();
    }




}
