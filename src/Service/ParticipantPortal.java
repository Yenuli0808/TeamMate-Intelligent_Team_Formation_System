package Service;

import Gaming_Club_Model.Team;

import java.util.Map;

public class ParticipantPortal {
    private Map<String, Team> participantTeams;

    public Team viewTeamAssignment(String participantId) {
        Team team = participantTeams.get(participantId);
        if (team == null) {
            throw new IllegalArgumentException("No team assignment found for participant");
        }
        return team;
    }

    public String getTeamAssignmentDetails(String participantId) {
        Team team = viewTeamAssignment(participantId);
        return formatTeamDetailsForParticipant(team, participantId);
    }

    private String formatTeamDetailsForParticipant(Team team, String participantId) {

        return participantId;
    }


}
