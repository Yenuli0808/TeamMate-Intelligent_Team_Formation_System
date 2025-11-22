package TestData;

import Gaming_Club_Model.Participant;

import java.util.Arrays;
import java.util.List;

public class TestDataFactory {

    public static List<Participant> createDiverseTestParticipants(){
        return Arrays.asList(
                new Participant("P001", "Leader_1", "leader1@edu.com", "0710000001", "Valorant", 9, "Strategist", 95),
                new Participant("P002", "Leader_2", "leader2@edu.com", "0710000002", "CS:GO", 8, "Attacker", 92),
                new Participant("P003", "Balanced_1", "balanced1@edu.com", "0710000003", "DOTA 2", 7, "Defender", 85),
                new Participant("P004", "Balanced_2", "balanced2@edu.com", "0710000004", "FIFA", 6, "Supporter", 78),
                new Participant("P005", "Balanced_3", "balanced3@edu.com", "0710000005", "Basketball", 5, "Coordinator", 75),
                new Participant("P006", "Thinker_1", "thinker1@edu.com", "0710000006", "Valorant", 8, "Strategist", 65),
                new Participant("P007", "Thinker_2", "thinker2@edu.com", "0710000007", "CS:GO", 7, "Attacker", 60),
                new Participant("P008", "Thinker_3", "thinker3@edu.com", "0710000008", "DOTA 2", 6, "Defender", 55)
        );
    }
}
