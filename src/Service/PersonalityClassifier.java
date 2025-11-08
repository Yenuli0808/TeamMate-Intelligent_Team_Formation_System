package Service;

import Gaming_Club_Model.PersonalityType;

public class PersonalityClassifier {
    public static PersonalityType classify(int score){
        if(score >=90 && score <=100){
            return PersonalityType.LEADER;
        }else if( score >=70 && score <=89){
            return PersonalityType.BALANCED;
        }else if( score >=50 && score <=69){
            return PersonalityType.THINKER;
        }else{
            throw  new IllegalArgumentException("Invalid score: "+score+".Must be 0-100");
        }
    }

    public static int calculateFromSurvey(int[] responses) {
        if(responses.length != 5) {
            throw new IllegalArgumentException("Need 5 responses");
        }
        int sum = 0;
        for(int r : responses) {
            if(r < 1 || r > 5) {
                throw new IllegalArgumentException("Responses must be 1-5");
            }
            sum += r;
        }
        return sum * 4; // Scale to 100
    }


}
