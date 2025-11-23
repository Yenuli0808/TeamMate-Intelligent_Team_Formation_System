package ModelTests;

import Gaming_Club_Model.Participant;
import Gaming_Club_Model.Team;
import org.junit.Test;

import static org.junit.Assert.*;

public class TeamTest {

    @Test
    public void testTeamCreationAndMemberManagement(){
        Team team = new Team("T001","Alpha Team",4);
        Participant member1 = new Participant("P001", "Alice", "alice@edu.com",
                "0711111111", "Valorant", 8, "Strategist", 95);

        assertTrue(team.addMember(member1));
        assertEquals(1,team.getCurrentSize());
        assertFalse(team.isFull());
    }

    @Test
    public void testTeamFullCapacity(){
        Team team = new Team("T002","Beta Team",2);
        Participant p1 = new Participant("P001", "P1", "p1@edu.com", "0711111111",
                "CS:GO", 5, "Attacker", 75);
        Participant p2 = new Participant("P002", "P2", "p2@edu.com", "0722222222",
                "DOTA 2", 6, "Defender", 65);
        Participant p3 = new Participant("P003", "P3", "p3@edu.com", "0733333333",
                "FIFA", 7, "Supporter", 85);

        team.addMember(p1);
        team.addMember(p2);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            team.addMember(p3);
        });
        assertTrue(exception.getMessage().contains("Team is already at maximum capacity"));
    }

    @Test
    public void testTeamAverageSkillCalculation() {
        Team team = new Team("T003", "Gamma Team", 3);

        team.addMember(new Participant("P001", "P1", "p1@edu.com", "0711111111",
                "Valorant", 8, "Strategist", 85));
        team.addMember(new Participant("P002", "P2", "p2@edu.com", "0722222222",
                "CS:GO", 6, "Attacker", 75));

        assertEquals(7.0, team.getAverageSkill(), 0.01);
    }

}
