package Service;

import Gaming_Club_Model.Participant;
import Gaming_Club_Model.Team;

public interface TeamFormationConstraint {
    boolean satisfiesBasicConstraints(Team team, Participant newMember);
    boolean satisfiesAllConstraints(Team team, Participant newMember);
    int getMaxSameGame();
    int getMinDifferentRoles();
    void setMaxSameGame(int max);
    void setMinDifferentRoles(int min);
}
