package Gaming_Club_Model;

import Service.PersonalityClassifier;

public class Survey {
    private String participantId;
    private int[] personalityResponses;
    private String preferredGame;
    private String preferredRole;
    private int skillLevel;

    public  Survey(String participantId, int[] personalityResponses,String preferredGame, String preferredRole, int skillLevel) {
        this.participantId = participantId;
        this.personalityResponses = personalityResponses;
        this.preferredGame = preferredGame;
        this.preferredRole = preferredRole;
        this.skillLevel = skillLevel;
    }

    public boolean isValid() {
        return personalityResponses.length == 5 && skillLevel >=1 && skillLevel <=10;
    }

    public int calculatePersonalityScore(){
        return PersonalityClassifier.calculateFromSurvey(personalityResponses);
    }

}
