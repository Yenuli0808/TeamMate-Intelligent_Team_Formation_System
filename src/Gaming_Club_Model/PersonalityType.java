package Gaming_Club_Model;

/**
 * Enum representing different personality types with score ranges.
 * Demonstrates enum usage for fixed set of values.
 */

public enum PersonalityType {
    LEADER("Leader",90,100,"Confident,decision-maker,naturally take charge"),
    BALANCED("Balanced",70,89,"Adaptive, communicative, team-oriented"),
    THINKER("Thinker",50,69,"Observant,analytical,prefer planning before action");

    private String displayName;
    private int minScore;
    private int maxScore;
    private String description;

    PersonalityType(String displayName, int minScore, int maxScore, String description) {
        this.displayName = displayName;
        this.minScore = minScore;
        this.maxScore = maxScore;
        this.description = description;
    }

    public boolean matchesScore(int score) {
        return score >= this.minScore && score <= this.maxScore;
    }

    public String getDisplayName() {
        return displayName;
    }
    public int getMinScore() {
        return minScore;
    }
    public int getMaxScore() {
        return maxScore;
    }
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }

}
