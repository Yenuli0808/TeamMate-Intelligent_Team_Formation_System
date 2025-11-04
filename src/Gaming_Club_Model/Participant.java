package Gaming_Club_Model;

/**
 * Represents a gaming club participant with personal attributes,
 * gaming preferences, and personality traits.
 * Demonstrates encapsulation through private fields and public getters.
 */

public class Participant {
    private String id;
    private String name;
    private String email;
    private String preferredGame;
    private int skillLevel;
    private String preferredRole;
    private int personalityScore;
    private PersonalityType personalityType;

    public  Participant(String id,String name,String email,String preferredGame,int skillLevel,String preferredRole,int personalityScore) {
        if (id==null || id.isEmpty()){
            throw new IllegalArgumentException("ID cannot be empty");
        }
        if(skillLevel<1 || skillLevel>10){
            throw new IllegalArgumentException("Skill Level must be 1-10");
        }
        if(personalityScore <0 ||personalityScore>100){
            throw new IllegalArgumentException("Personal Score must be between 0 and 100");
        }

        this.id = id;
        this.name = name;
        this.email = email;
        this.preferredGame = preferredGame;
        this.skillLevel = skillLevel;
        this.preferredRole = preferredRole;
        this.personalityScore = personalityScore;
        this.personalityType=PersonalityClassifier.classify(personalityScore);
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
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
        return String.format("Participant{id='%s',game='%s',role='%s', skill=%d, type=%s} ",id,name,preferredGame,preferredRole,skillLevel,personalityType);
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
