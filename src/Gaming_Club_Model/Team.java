package Gaming_Club_Model;

import java.util.ArrayList;
import Service.Formattable;
import java.util.List;

/**
 * Represents a team with participants
 */

public class Team extends BaseEntity implements Formattable {
    private List<Participant> members;
    private int maxSize;

    public Team(String teamId, String teamName, int maxSize) {
        super(teamId, teamName);
        this.maxSize = maxSize;
        this.members = new ArrayList<>();
    }

    @Override
    public String toFormattedString() {
        return String.format("Team %s: %s (%d/%d members) | Avg Skill: %.1f | Games: %s | Roles: %s",
                id, name, members.size(), maxSize, getAverageSkill(),
                getUniqueGames(), getUniqueRoles());
    }

    public boolean addMember(Participant participant) {
        if(members.size() >= maxSize) {
            throw new IllegalArgumentException("Team is already at maximum capacity");
        }
        if(members.contains(participant)) {
            throw new IllegalArgumentException("Participant already exists");
        }
        return members.add(participant);
    }

    @Override
    public boolean validate() {
        return id != null && !id.isEmpty() &&
                name != null && !name.isEmpty() && maxSize >= 2;
    }

    @Override
    public String toCSVFormat(){
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(",").append(name).append(",");
        for (Participant member : members) {
            sb.append(member.getId()).append(";");
        }
        return sb.toString();
    }

    public double getAverageSkill() {
        if (members.isEmpty())
            return 0.0;
        int total = members.stream().mapToInt(Participant::getSkillLevel).sum();
        return (double) total / members.size();
    }

    public List<String> getUniqueGames(){
        List<String> games = new ArrayList<>();
        for(Participant member: members){
            String game = member.getPreferredGame();
            if(!games.contains(game)){
                games.add(game);
            }
        }
        return games;
    }

    public List<String> getUniqueRoles(){
        List<String> roles = new ArrayList<>();
        for(Participant member: members){
            String role = member.getPreferredRole();
            if(!roles.contains(role)){
                roles.add(role);
            }
        }
        return roles;
    }

    public int countPersonalityType(PersonalityType type){
        int count = 0;
        for(Participant member: members){
            if(member.getPersonalityType()==type){
                count++;
            }
        }
        return count;
    }

    public boolean isFull(){
        return members.size() >= maxSize;
    }


    public List<Participant> getMembers() {
        return new ArrayList<>(members);
    }
    public int getCurrentSize() {
        return members.size();
    }
    public int getMaxSize() {
        return maxSize;
    }

    @Override
    public String toString() {
        return String.format("Team{id='%s', name='%s',size=%d/%d, avgSkill=%.2f}", id, name, members.size(), maxSize,this.getAverageSkill());
    }

    /*Detailed information**/
    public String getTeamDetails(){
        StringBuilder details = new StringBuilder();
        details.append("===").append(name).append("===\n");
        details.append("ID: ").append(id).append("\n");
        details.append("Size:").append(members.size()).append("\n").append(maxSize).append("\n");
        details.append("Average Skill: ").append(String.format("%.2f", this.getAverageSkill())).append("\n");
        details.append("Games: ").append(getUniqueGames()).append("\n");
        details.append("Roles: ").append(getUniqueRoles()).append("\n");
        details.append("Personalities:\n");
        details.append("Leader: ").append(countPersonalityType(PersonalityType.LEADER)).append("\n");
        details.append("Balanced: ").append(countPersonalityType(PersonalityType.BALANCED)).append("\n");
        details.append("Thinkers:").append(countPersonalityType(PersonalityType.THINKER)).append("\n");

        details.append("Members:\n");
        for (Participant member : members) {
            details.append(" - ").append(member.toFormattedString()).append("\n");
        }
        return details.toString();
    }

}
