package Gaming_Club_Model;

import Service.PersonalityClassifier;

public class Survey {
    private String participantId;
    private String name;
    private String email;
    private String phoneNumber;
    private int[] personalityResponses;
    private String preferredGame;
    private String preferredRole;
    private int skillLevel;

    public  Survey(String participantId, int[] personalityResponses,String preferredGame, String preferredRole, int skillLevel) {
        if (personalityResponses == null || personalityResponses.length != 5) {
            throw new IllegalArgumentException("Need exactly 5 personality responses");
        }

        this.participantId = participantId;
        this.personalityResponses = personalityResponses;
        this.preferredGame = preferredGame;
        this.preferredRole = preferredRole;
        this.skillLevel = skillLevel;
    }

    public Participant processSurvey() {
        // Calculate personality score from responses
        int personalityScore = PersonalityClassifier.calculateFromSurvey(personalityResponses);

        // Create and return participant
        return new Participant(
                participantId, name, email, phoneNumber,
                preferredGame, skillLevel, preferredRole, personalityScore
        );
    }
}
