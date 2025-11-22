package ServiceTests;

import Gaming_Club_Model.PersonalityType;
import Service.PersonalityClassifier;
import org.junit.Test;

import static org.junit.Assert.*;

public class PersonalityClassifierTest {

    @Test
    public void testPersonalityClassification_BoundaryValues(){
        assertEquals(PersonalityType.LEADER, PersonalityClassifier.classify(90));
        assertEquals(PersonalityType.LEADER, PersonalityClassifier.classify(100));
        assertEquals(PersonalityType.BALANCED, PersonalityClassifier.classify(70));
        assertEquals(PersonalityType.BALANCED, PersonalityClassifier.classify(89));
        assertEquals(PersonalityType.THINKER, PersonalityClassifier.classify(50));
        assertEquals(PersonalityType.THINKER, PersonalityClassifier.classify(69));
    }

    @Test
    public void testPersonalityClassification_InvalidScores(){
        assertThrows(IllegalArgumentException.class, () -> PersonalityClassifier.classify(45));
        assertThrows(IllegalArgumentException.class, () -> PersonalityClassifier.classify(101));
    }

    @Test
    public void testSurveyScoreCalculation(){
        int[] responses = {5,4,5,4,5};
        int score = PersonalityClassifier.calculateFromSurvey(responses);

        assertEquals(92, score);
        assertEquals(PersonalityType.LEADER, PersonalityClassifier.classify(score));
    }

    @Test
    public void testInvalidSurveyResponses(){
        int[] invalidResponses = {6,4,5,4,5};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            PersonalityClassifier.calculateFromSurvey(invalidResponses);
        });
        assertTrue(exception.getMessage().contains("Responses must be 1-5"));
    }
}
