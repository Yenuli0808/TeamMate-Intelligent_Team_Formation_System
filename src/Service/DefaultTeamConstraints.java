package Service;

import Gaming_Club_Model.Team;
import Gaming_Club_Model.Participant;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultTeamConstraints implements TeamFormationConstraint {
    private int maxSameGame = 2;
    private int minDifferentRoles = 3;

    @Override
    public boolean satisfiesBasicConstraints(Team team, Participant newMember) {
        return satisfiesGameConstraint(team, newMember);
    }

    @Override
    public boolean satisfiesAllConstraints(Team team, Participant newMember) {
        return satisfiesGameConstraint(team, newMember) &&
                satisfiesRoleConstraint(team, newMember) &&
                satisfiesPersonalityConstraint(team, newMember);
    }

    private boolean satisfiesGameConstraint(Team team, Participant newMember) {
        long sameGameCount = team.getMembers().stream()
                .filter(p -> p.getPreferredGame().equals(newMember.getPreferredGame()))
                .count();
        return sameGameCount < maxSameGame;
    }

    private boolean satisfiesRoleConstraint(Team team, Participant newMember) {
        Set<String> currentRoles = team.getMembers().stream()
                .map(Participant::getPreferredRole)
                .collect(Collectors.toSet());

        // If adding this role would help reach minimum diversity
        currentRoles.add(newMember.getPreferredRole());
        return currentRoles.size() >= Math.min(minDifferentRoles, team.getMaxSize());
    }

    private boolean satisfiesPersonalityConstraint(Team team, Participant newMember) {
        // Basic constraint: max 1 leader per team
        if (newMember.getPersonalityType().toString().equals("LEADER")) {
            long leaderCount = team.getMembers().stream()
                    .filter(p -> p.getPersonalityType().toString().equals("LEADER"))
                    .count();
            return leaderCount == 0;
        }
        return true;
    }

    // Getters and setters
    @Override
    public int getMaxSameGame() { return maxSameGame; }

    @Override
    public void setMaxSameGame(int max) { this.maxSameGame = max; }

    @Override
    public int getMinDifferentRoles() { return minDifferentRoles; }

    @Override
    public void setMinDifferentRoles(int min) { this.minDifferentRoles = min; }
}