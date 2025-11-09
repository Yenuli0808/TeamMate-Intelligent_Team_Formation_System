package Gaming_Club_Model;

import Service.Formattable;
import Service.PersonalityClassifier;

/**
 * Represents a gaming club participant with personal attributes,
 * gaming preferences, and personality traits.
 * Demonstrates encapsulation through private fields and public getters.
 */

public class Participant extends BaseEntity implements Formattable {
    private String email;
    private String preferredGame;
    private int skillLevel;
    private String preferredRole;
    private int personalityScore;
    private PersonalityType personalityType;

    public Participant(String id,String name,String email,String preferredGame,int skillLevel,String preferredRole,int personalityScore) {

        super(id,name);

        if(skillLevel<1 || skillLevel>10){
            throw new IllegalArgumentException("Skill Level must be 1-10");
        }
        if(personalityScore <0 ||personalityScore>100){
            throw new IllegalArgumentException("Personal Score must be between 0 and 100");
        }

        this.email = email;
        this.preferredGame = preferredGame;
        this.skillLevel = skillLevel;
        this.preferredRole = preferredRole;
        this.personalityScore = personalityScore;
        this.personalityType= PersonalityClassifier.classify(personalityScore);
    }

    @Override
    public boolean validate() {
        return id !=null && !id.isEmpty() && name != null && !name.isEmpty() && skillLevel >=1 && skillLevel<=10;
    }


    @Override
    public String toDisplayString() {
        return String.format("%s - %s (%s) | Skill: %d | Role: %s | Type: %s",
                id, name, preferredGame, skillLevel, preferredRole, personalityType);
    }

    @Override
    public String toCSVString() {
        return String.format("%s,%s,%s,%s,%d,%s,%d,%s",
                id, name, email, preferredGame, skillLevel, preferredRole, personalityScore, personalityType);
    }

    @Override
    public String toDetailedString() {
        return String.format("Participant Details:\n" +
                        "ID: %s\nName: %s\nEmail: %s\nGame: %s\nSkill: %d\nRole: %s\nPersonality: %s (Score: %d)",
                id, name, email, preferredGame, skillLevel, preferredRole, personalityType, personalityScore);
    }


    public String getEmail() {
        return email;
    }
    public String getPreferredGame() {
        return preferredGame;
    }
    public int getSkillLevel() {
        return skillLevel;
    }
    public String getPreferredRole() {
        return preferredRole;
    }
    public int getPersonalityScore() {
        return personalityScore;
    }

    public PersonalityType getPersonalityType() {
        return personalityType;
    }

    public void setSkillLevel(int skillLevel) {
        this.skillLevel = skillLevel;
    }

    @Override
    public String toString() {
        return toDisplayString();
    }


    //equals will check weather 2 objects are the same
    // hashcode() will be used for quick lookup in the code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(!(o instanceof Participant)) return false;
        Participant that = (Participant) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
