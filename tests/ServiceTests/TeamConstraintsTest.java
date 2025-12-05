package ServiceTests;

import Gaming_Club_Model.Participant;
import Gaming_Club_Model.Team;
import Service.DefaultTeamConstraints;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TeamConstraintsTest {

    @Test
    public void testGameConstraints_SameGameLimit(){
        DefaultTeamConstraints constraints = new DefaultTeamConstraints();
        Team team = new Team("T001","Test Team",4);

        Participant p1 = new Participant("P001", "P1", "p1@edu.com", "0711111111",
                "Valorant", 5, "Attacker", 75);
        Participant p2 = new Participant("P002", "P2", "p2@edu.com", "0722222222",
                "Valorant", 6, "Defender", 80);

        team.addMember(p1);
        team.addMember(p2);

        // Try to add 3rd Valorant player - should violate constraint
        Participant p3 = new Participant("P003", "P3", "p3@edu.com", "0733333333",
                "Valorant", 7, "Supporter", 85);
        team.addMember(p3);

        assertFalse("Should violate game constraint (max 2 same game)",constraints.satisfiesBasicConstraints(team,p3));
    }

    @Test
    public void testGameConstraint_DifferentGameAllowed() {
        DefaultTeamConstraints constraints = new DefaultTeamConstraints();
        Team team = new Team("T001", "Test Team", 4);

        // Add 2 Valorant players
        Participant p1 = new Participant("P001", "P1", "p1@edu.com", "0711111111",
                "Valorant", 5, "Attacker", 75);
        Participant p2 = new Participant("P002", "P2", "p2@edu.com", "0722222222",
                "Valorant", 6, "Defender", 80);

        team.addMember(p1);
        team.addMember(p2);

        // Try to add CS:GO player - should be allowed
        Participant p3 = new Participant("P003", "P3", "p3@edu.com", "0733333333",
                "CS:GO", 7, "Supporter", 85);

        assertTrue(constraints.satisfiesBasicConstraints(team, p3), "Should allow different game");
    }

    @Test
    public void testRoleDiversityConstraint() {
        DefaultTeamConstraints constraints = new DefaultTeamConstraints();
        Team team = new Team("T001", "Test Team", 4);

        // Add participants with different roles
        team.addMember(new Participant("P001", "P1", "p1@edu.com", "0711111111",
                "Valorant", 5, "Attacker", 75));
        team.addMember(new Participant("P002", "P2", "p2@edu.com", "0722222222",
                "CS:GO", 6, "Defender", 80));
        team.addMember(new Participant("P003", "P3", "p3@edu.com", "0733333333",
                "DOTA 2", 7, "Supporter", 85));

        // Add Coordinator - should satisfy role diversity (4 different roles)
        Participant p4 = new Participant("P004", "P4", "p4@edu.com", "0744444444",
                "FIFA", 8, "Coordinator", 90);

        assertTrue(constraints.satisfiesAllConstraints(team, p4), "Should satisfy role diversity with 4 different roles");
    }

    @Test
    public void testRoleDiversityConstraint_DuplicateRole() {
        DefaultTeamConstraints constraints = new DefaultTeamConstraints();
        Team team = new Team("T001", "Test Team", 4);

        team.addMember(new Participant("P001", "P1", "p1@edu.com", "0711111111",
                "Valorant", 5, "Attacker", 75));
        team.addMember(new Participant("P002", "P2", "p2@edu.com", "0722222222",
                "CS:GO", 6, "Defender", 80));

        // Try to add another Attacker - it might not satisfy diversity
        Participant p3 = new Participant("P003", "P3", "p3@edu.com", "0733333333",
                "DOTA 2", 7, "Attacker", 85);

        assertTrue(constraints.satisfiesBasicConstraints(team, p3), "Should satisfy basic constraints even with duplicate role");
    }

    @Test
    public void debugLeaderConstraintIssue() {
        DefaultTeamConstraints constraints = new DefaultTeamConstraints();
        Team team = new Team("T001", "Test Team", 4);

        // Add first leader
        Participant leader1 = new Participant("P001", "Leader1", "leader1@edu.com", "0711111111",
                "Valorant", 8, "Strategist", 95);
        team.addMember(leader1);

        // Try to add second leader
        Participant leader2 = new Participant("P002", "Leader2", "leader2@edu.com", "0722222222",
                "CS:GO", 7, "Attacker", 92);

        // Test each constraint individually
        boolean gameConstraint = constraints.satisfiesGameConstraint(team, leader2);
        boolean roleConstraint = constraints.satisfiesRoleConstraint(team, leader2);
        boolean personalityConstraint = constraints.satisfiesPersonalityConstraint(team, leader2);

        System.out.println("=== CONSTRAINT DEBUG ===");
        System.out.println("Game constraint: " + gameConstraint);
        System.out.println("Role constraint: " + roleConstraint);
        System.out.println("Personality constraint: " + personalityConstraint);
        System.out.println("All constraints: " + constraints.satisfiesAllConstraints(team, leader2));

        // Check role details
        System.out.println("Team roles: " + team.getUniqueRoles());
        System.out.println("New player role: " + leader2.getPreferredRole());
        System.out.println("Team size: " + team.getCurrentSize());
    }

    @Test
    public void testPersonalityConstraint_LeaderLimit() {
        DefaultTeamConstraints constraints = new DefaultTeamConstraints();
        Team team = new Team("T001", "Test Team", 4);

        // Add a leader
        Participant leader1 = new Participant("P001", "Leader1", "leader1@edu.com", "0711111111",
                "Valorant", 8, "Strategist", 95);

        team.addMember(leader1);

        // Try to add another leader - should violate constraint
        Participant leader2 = new Participant("P002", "Leader2", "leader2@edu.com", "0722222222",
                "CS:GO", 7, "Attacker", 92);

        assertFalse("Should violate personality constraint (max 1 leader)",constraints.satisfiesAllConstraints(team, leader2));
    }

    @Test
    public void testPersonalityConstraint_NonLeaderAllowed() {
        DefaultTeamConstraints constraints = new DefaultTeamConstraints();

        // Temporarily relax role constraints for this test,This ensures we're only testing the leader limit, not role diversity
        constraints.setMinDifferentRoles(2);

        Team team = new Team("T001", "Test Team", 4);

        // Add a leader
        Participant leader = new Participant("P001", "Leader", "leader@edu.com", "0711111111",
                "Valorant", 8, "Strategist", 95);
        team.addMember(leader);

        // Try to add a balanced player
        Participant balanced = new Participant("P002", "Balanced", "balanced@edu.com", "0722222222",
                "CS:GO", 7, "Attacker", 80);

        assertTrue(constraints.satisfiesAllConstraints(team, balanced),
                "Should allow non-leader players even with existing leader");
    }

    @Test
    public void testConstraintGettersAndSetters() {
        DefaultTeamConstraints constraints = new DefaultTeamConstraints();

        // Test default values
        assertEquals(2, constraints.getMaxSameGame());
        assertEquals(3, constraints.getMinDifferentRoles());

        // Test setters
        constraints.setMaxSameGame(3);
        constraints.setMinDifferentRoles(2);

        assertEquals(3, constraints.getMaxSameGame());
        assertEquals(2, constraints.getMinDifferentRoles());
    }

    @Test
    public void testEmptyTeamConstraints() {
        DefaultTeamConstraints constraints = new DefaultTeamConstraints();
        Team emptyTeam = new Team("T001", "Empty Team", 4);

        // Any player should satisfy constraints for empty team
        Participant player = new Participant("P001", "Player", "player@edu.com", "0711111111",
                "Valorant", 5, "Attacker", 75);

        assertTrue(constraints.satisfiesBasicConstraints(emptyTeam, player),"Should satisfy constraints for empty team");
        assertTrue(constraints.satisfiesAllConstraints(emptyTeam, player),"Should satisfy all constraints for empty team");
    }

    @Test
    public void testPersonalityTypeComparison() {
        // Test that personality type comparison works correctly
        Participant leader = new Participant("P001", "Leader", "leader@edu.com", "0711111111",
                "Valorant", 8, "Strategist", 95);

        assertEquals("Leader", leader.getPersonalityType().toString());
    }
}