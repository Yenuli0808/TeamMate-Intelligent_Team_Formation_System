package Service;

import Gaming_Club_Model.Participant;
import Gaming_Club_Model.PersonalityType;
import Gaming_Club_Model.Team;

import java.util.*;

public class TeamBuilder {
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
            teams.add(new Team(teamId,teamName,emptyMembers,teamSize));
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
            System.out.println("\n" +team.getTeamName() + ":");
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
}
