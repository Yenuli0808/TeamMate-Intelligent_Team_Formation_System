package Service;

import Gaming_Club_Model.Participant;
import Gaming_Club_Model.PersonalityType;
import Gaming_Club_Model.Team;

import java.util.*;

public class TeamBuilder implements TeamFormationStrategy {
    private int teamSize;
    private List<Participant> participants;

    public  TeamBuilder(List<Participant> participants, int teamSize) {
        if(participants==null || participants.isEmpty()){
            throw  new IllegalArgumentException("Participants cannot be empty");
        }
        if(teamSize < 2){
            throw  new IllegalArgumentException("Team size cannot be less than 2");
        }

        this.participants=new ArrayList<>(participants);
        this.teamSize=teamSize;
    }

    @Override
    public List<Team> formTeams(){
        return formBalancedTeams();
    }

    @Override
    public String getStrategyName() {
        return "Skill-Balanced Team Formation";
    }

    public List<Team> formBalancedTeams(){
        int teamCount = (int) Math.ceil((double) participants.size()/teamSize);
        List<Team> teams = createEmptyTeams(teamCount);

        List<Participant> sortedParticipants = sortBySkill(participants);

        distributeParticipants(teams,sortedParticipants);

        return teams;
    }

    private List<Team>  createEmptyTeams(int teamCount) {
        List<Team> teams = new ArrayList<>();
        for(int i=0; i<teamCount; i++){
            String teamId = "T" + (i+1);
            String teamName = "Team" + (i+1);
            List<Participant> emptyMembers = new ArrayList<>();
            teams.add(new Team(teamId,teamName,teamSize));
        }
        return teams;
    }

    private List<Participant> sortBySkill(List<Participant> participants){
        List<Participant> sorted = new ArrayList<>(participants);
        Collections.sort(sorted,(p1,p2) -> Integer.compare(p1.getSkillLevel(),p2.getSkillLevel()));
        return sorted;
    }

    private void distributeParticipants(List<Team> teams, List<Participant> participants){
        int teamCount = teams.size();
        int participantIndex = 0;

        while (participantIndex<participants.size()){
            for(int i =0; i < teamCount && participantIndex < participants.size(); i++){
                Team team = teams.get(i);
                if(!team.isFull()){
                    team.addMember(participants.get(participantIndex));
                    participantIndex++;
                }
            }

            for (int i = teamCount - 1; i >= 0 && participantIndex < participants.size(); i--){
                Team team = teams.get(i);
                if(!team.isFull()){
                    team.addMember(participants.get(participantIndex));
                    participantIndex++;
                }
            }
        }
    }

    public List<Team> formRandomTeams(){
        List<Team> teams = createEmptyTeams((int) Math.ceil ((double) participants.size() /teamSize));
        List<Participant> shuffled = new ArrayList<>(participants);
        Collections.shuffle(shuffled);

        int currentTeam = 0;
        for (Participant participant: shuffled){
            Team team = teams.get(currentTeam);
            team.addMember(participant);
            currentTeam = (currentTeam+1) % teams.size();
        }
        return teams;
    }

    public void printBalanceReport(List<Team> teams){
        System.out.println("\n===Team Balance Report===");

        for(Team team: teams){
            System.out.println("\n" +team.getName() + ":");
            System.out.println("Size: "+team.getCurrentSize() + "/" + team.getMaxSize());
            System.out.println("Average Skill: "+ String.format("%.2f",team.getAverageSkill()));

            int leaders = team.countPersonalityType(PersonalityType.LEADER);
            int balanced =  team.countPersonalityType(PersonalityType.BALANCED);
            int thinkers = team.countPersonalityType(PersonalityType.THINKER);

            System.out.println("Personalities: "+ leaders + "Leaders," + balanced + "Balanced,"+thinkers + "Thinkers");

            System.out.println("Games: "+ team.getUniqueGames());
            System.out.println("Roles: "+ team.getUniqueRoles());
        }

    }

    public List<Team> formAdvancedTeams(){
        int teamCount = (int) Math.ceil((double) participants.size()/teamSize);
        List<Team> teams = createEmptyTeams(teamCount);

        System.out.println("Applying 3 Simple Rules:");
        System.out.println("1. One leader per team");
        System.out.println("2. Max 2 players from same game");
        System.out.println("3. Prefer different roles");

        addLeadersFirst(teams);
        addRemainingPlayers(teams);

        return teams;
    }

    private void addLeadersFirst(List<Team> teams){
        //to find all leaders
        List<Participant> leaders = new ArrayList<>();
        for( Participant p: participants){
            if(p.getPersonalityType() == PersonalityType.LEADER){
                leaders.add(p);
            }
        }

        //This will put one leader in each team if possible
        int teamIndex = 0;
        for (Participant leader: leaders){
            if(teamIndex < teams.size() && ! teams.get(teamIndex).isFull()){
                teams.get(teamIndex).addMember(leader);
                teamIndex++;
            }
        }
    }

    private void addRemainingPlayers(List<Team> teams){
        //to get all non leader participants
        List<Participant> remaining = new ArrayList<>();
        for( Participant p: participants){
            if(!isAnyTeam(teams,p)){
                remaining.add(p);
            }
        }

        //adding them to teams
        for(Participant player: remaining){
            Team bestTeam = findTeamWithConstraints(teams,player);
            if(bestTeam != null){
                bestTeam.addMember(player);
            }
        }
    }

    private Team findTeamWithConstraints(List<Team> teams, Participant player){
        // Rule 1: Find team without this game
        for (Team team : teams) {
            if (!team.isFull() && countSameGame(team, player) < 2) {
                if (countSameGame(team, player) == 0) {
                    return team; // Perfect - no same game players
                }
            }
        }

        // Rule 2: Find team without this role
        for (Team team : teams) {
            if (!team.isFull() && countSameGame(team, player) < 2) {
                if (!hasSameRole(team, player)) {
                    return team; // Good - no same role players
                }
            }
        }

        // Rule 3: Any team that follows basic constraints
        for (Team team : teams) {
            if (!team.isFull() && countSameGame(team, player) < 2) {
                return team;
            }
        }

        // Last resort: Any team with space
        for (Team team : teams) {
            if (!team.isFull()) {
                return team;
            }
        }
        return null;
    }

    private int countSameGame(Team team, Participant player) {
        int count = 0;
        for (Participant member : team.getMembers()) {
            if (member.getPreferredGame().equals(player.getPreferredGame())) {
                count++;
            }
        }
        return count;
    }

    private boolean hasSameRole(Team team, Participant player) {
        for (Participant member : team.getMembers()) {
            if (member.getPreferredRole().equals(player.getPreferredRole())) {
                return true;
            }
        }
        return false;
    }

    private boolean isAnyTeam(List<Team> teams, Participant player) {
        for (Team team : teams) {
            if (team.getMembers().contains(player)) {
                return true;
            }
        }
        return false;
    }

    public void printAdvancedReport(List<Team> teams) {
        System.out.println("\n=== ADVANCED TEAM REPORT ===");
        System.out.println("Applied: 1 Leader max, Game variety, Role diversity");

        for (Team team : teams) {
            System.out.println("\n" + team.getName() + ":");
            System.out.println("  Players: " + team.getCurrentSize() + "/" + team.getMaxSize());
            System.out.println("  Avg Skill: " + String.format("%.1f", team.getAverageSkill()));

            int leaders = team.countPersonalityType(PersonalityType.LEADER);
            int balanced = team.countPersonalityType(PersonalityType.BALANCED);
            int thinkers = team.countPersonalityType(PersonalityType.THINKER);
            System.out.println("  Personalities: " + leaders + "L " + balanced + "B " + thinkers + "T");

            System.out.println("  Games: " + team.getUniqueGames());
            System.out.println("  Roles: " + team.getUniqueRoles());

            System.out.print("  Constraints: ");
            System.out.print(leaders <= 1 ? "✓Leader " : "✗Leader ");
            System.out.print(team.getUniqueGames().size() >= 2 ? "✓Games " : "✗Games ");
            System.out.print(team.getUniqueRoles().size() >= 2 ? "✓Roles" : "✗Roles");
            System.out.println();
        }
    }

}
