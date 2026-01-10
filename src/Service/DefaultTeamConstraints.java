package Service;

import Gaming_Club_Model.PersonalityType;
import Gaming_Club_Model.Team;
import Gaming_Club_Model.Participant;
import java.util.Set;
import java.util.stream.Collectors;

//defines the rules that must be satisfied when adding a participant to a team during team formation

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

    public boolean satisfiesGameConstraint(Team team, Participant newMember) {
        long sameGameCount = team.getMembers().stream()
                .filter(p -> p.getPreferredGame().equals(newMember.getPreferredGame()))
                .count();
        return sameGameCount < maxSameGame;
    }

    public boolean satisfiesRoleConstraint(Team team, Participant newMember) {
        Set<String> currentRoles = team.getMembers().stream()
                .map(Participant::getPreferredRole)
                .collect(Collectors.toSet());

        // If adding this role would help reach minimum diversity
        currentRoles.add(newMember.getPreferredRole());
        int requiredRoles = Math.min(minDifferentRoles,team.getMaxSize());

        if(team.getCurrentSize()==0){
            return true;    //Empty team - any player should be allowed
        }else if(team.getCurrentSize()<3){
            return currentRoles.size() >= Math.min(2, requiredRoles);       // Small teams - require at least 2 different roles
        }
        return currentRoles.size()>=requiredRoles;
    }

    public boolean satisfiesPersonalityConstraint(Team team, Participant newMember) {
        if (newMember.getPersonalityType() == PersonalityType.LEADER) {
            long leaderCount = team.getMembers().stream()
                    .filter(p -> p.getPersonalityType() == PersonalityType.LEADER)
                    .count();
            System.out.println("Existing leaders: " + leaderCount + ", Adding leader: " + (leaderCount == 0));
            return leaderCount == 0;  // Can only add leader if no existing leaders
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