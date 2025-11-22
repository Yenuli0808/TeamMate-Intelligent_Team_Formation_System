package TestData;

import Gaming_Club_Model.Participant;
import java.util.Arrays;
import java.util.List;

public class TestDataFactory {

    public static List<Participant> createDiverseTestParticipants() {
        return Arrays.asList(
                // Leaders
                new Participant("P001", "Leader_1", "leader1@edu.com", "0710000001",
                        "Valorant", 9, "Strategist", 95),
                new Participant("P002", "Leader_2", "leader2@edu.com", "0710000002",
                        "CS:GO", 8, "Attacker", 92),

                // Balanced
                new Participant("P003", "Balanced_1", "balanced1@edu.com", "0710000003",
                        "DOTA 2", 7, "Defender", 85),
                new Participant("P004", "Balanced_2", "balanced2@edu.com", "0710000004",
                        "FIFA", 6, "Supporter", 78),
                new Participant("P005", "Balanced_3", "balanced3@edu.com", "0710000005",
                        "Basketball", 5, "Coordinator", 75),
                new Participant("P006", "Balanced_4", "balanced4@edu.com", "0710000006",
                        "Valorant", 8, "Attacker", 82),

                // Thinkers
                new Participant("P007", "Thinker_1", "thinker1@edu.com", "0710000007",
                        "Valorant", 8, "Strategist", 65),
                new Participant("P008", "Thinker_2", "thinker2@edu.com", "0710000008",
                        "CS:GO", 7, "Attacker", 60),
                new Participant("P009", "Thinker_3", "thinker3@edu.com", "0710000009",
                        "DOTA 2", 6, "Defender", 55),
                new Participant("P010", "Thinker_4", "thinker4@edu.com", "0710000010",
                        "FIFA", 5, "Supporter", 58)
        );
    }

    public static List<Participant> createEdgeCaseParticipants() {
        return Arrays.asList(
                // Minimum skill
                new Participant("P101", "Min_Skill", "min@edu.com", "0720000001",
                        "Valorant", 1, "Strategist", 95),
                // Maximum skill
                new Participant("P102", "Max_Skill", "max@edu.com", "0720000002",
                        "CS:GO", 10, "Attacker", 95),
                // Boundary personality scores
                new Participant("P103", "Boundary_90", "b90@edu.com", "0720000003",
                        "DOTA 2", 5, "Defender", 90), // Leader boundary
                new Participant("P104", "Boundary_70", "b70@edu.com", "0720000004",
                        "FIFA", 5, "Supporter", 70), // Balanced boundary
                new Participant("P105", "Boundary_50", "b50@edu.com", "0720000005",
                        "Basketball", 5, "Coordinator", 50) // Thinker boundary
        );
    }

    public static List<Participant> createConcurrencyTestParticipants() {
        return Arrays.asList(
                new Participant("C001", "Concurrent_1", "c1@edu.com", "0730000001",
                        "Valorant", 7, "Strategist", 85),
                new Participant("C002", "Concurrent_2", "c2@edu.com", "0730000002",
                        "CS:GO", 6, "Attacker", 75),
                new Participant("C003", "Concurrent_3", "c3@edu.com", "0730000003",
                        "DOTA 2", 8, "Defender", 88),
                new Participant("C004", "Concurrent_4", "c4@edu.com", "0730000004",
                        "FIFA", 5, "Supporter", 72),
                new Participant("C005", "Concurrent_5", "c5@edu.com", "0730000005",
                        "Basketball", 9, "Coordinator", 92),
                new Participant("C006", "Concurrent_6", "c6@edu.com", "0730000006",
                        "Valorant", 4, "Strategist", 68)
        );
    }
}