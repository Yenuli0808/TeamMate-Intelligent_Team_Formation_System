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

    public static int calculateScore(int[] surveyResponses){
        if(surveyResponses == null || surveyResponses.length != 5 ){
            throw  new IllegalArgumentException("Need exactly 5 survey responses");
        }

        int total=0;
        for (int response: surveyResponses){
            if(response <1 || response >5){
                throw  new IllegalArgumentException("Survey responses must be between 1-5, got: %d"+response);
            }
            total+=response;
        }
        return total *4;
    }


}
