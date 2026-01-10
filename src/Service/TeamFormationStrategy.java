package Service;

import Gaming_Club_Model.Team;

import java.util.List;

public interface TeamFormationStrategy {
    //defines how teams are formed, independent of the algorithm used
    List<Team> formTeams();
    String getStrategyName();
}
