package Service;

import Gaming_Club_Model.Participant;
import Gaming_Club_Model.Team;

public interface TeamFormationConstraint {
    //defines the rule set that determines whether a participant can be added to a team
    boolean satisfiesBasicConstraints(Team team, Participant newMember);
    boolean satisfiesAllConstraints(Team team, Participant newMember);
    int getMaxSameGame();
    int getMinDifferentRoles();
    void setMaxSameGame(int max);
    void setMinDifferentRoles(int min);
}
