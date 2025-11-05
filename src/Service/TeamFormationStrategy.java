package Service;

import Gaming_Club_Model.Team;

import java.util.List;

public interface TeamFormationStrategy {
    List<Team> formTeams();
    String getStrategyName();
}
