package ModelTests;

import Gaming_Club_Model.Participant;
import Gaming_Club_Model.PersonalityType;
import org.junit.Test;

import static org.junit.Assert.*;

public class ParticipantTest {

    @Test
    public void testParticipantCreation_ValidData(){
        Participant participant = new Participant("P101", "Participant_101",
                "user101@university.edu", "0712345678", "Valorant", 8, "Strategist", 85);

        assertNotNull(participant);
        assertEquals("P101",participant.getId());
        assertEquals(PersonalityType.BALANCED, participant.getPersonalityType());
    }

    @Test
    public void testParticipantCreation_InvalidSkillLevel(){
        Exception exception = assertThrows(IllegalArgumentException.class,()->{
            new Participant("P102","Participant_102","user102@university.edu","0712345679","CS:GO",15,"Attacker",85);
        });
        assertTrue(exception.getMessage().contains("Skill Level must be 1-10"));

    }

    @Test
    public void testParticipantValidation(){
        Participant validParticipant = new Participant("P103", "Participant_103",
                "user103@university.edu", "0712345670", "FIFA", 5, "Defender", 75);

        assertTrue(validParticipant.validate());
    }

    @Test
    public void testPersonalityTypeClassification(){
        assertEquals(PersonalityType.LEADER, PersonalityType.LEADER);
        assertEquals(PersonalityType.BALANCED, PersonalityType.BALANCED);
        assertEquals(PersonalityType.THINKER, PersonalityType.THINKER);

        assertTrue(PersonalityType.LEADER.matchesScore(95));
        assertTrue(PersonalityType.BALANCED.matchesScore(80));
        assertTrue(PersonalityType.THINKER.matchesScore(60));
    }

}
