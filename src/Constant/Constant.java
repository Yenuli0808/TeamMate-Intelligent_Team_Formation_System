package Constant;

public final class Constant {
    private Constant(){}  // this will prevent instantiation

    //Personality Scoring
    public static final int MIN_PERSONALITY_SCORE = 0;
    public static final int MAX_PERSONALITY_SCORE = 100;

    //Skill levels
    public static final int MIN_SKILL_LEVEL = 1;
    public static final int MAX_SKILL_LEVEL = 10;

    //Team Formation Constraints
    public static final int MIN_TEAM_SIZE = 2;
    public static final int MAX_TEAM_SIZE = 10;
    public static final int MAX_SAME_GAME_PER_TEAM = 2;
    public static final int MIN_DIFFERENT_ROLES = 3;

    public static final String DEFAULT_INPUT_CSV = "data/participants_sample.csv";
    public static final String DEFAULT_OUTPUT_CSV = "data/formed_teams.csv";

    public static final String INVALID_SKILL_LEVEL = "Skill level must be between " + MIN_SKILL_LEVEL + " and " + MAX_SKILL_LEVEL;
    public static final String INVALID_PERSONALITY_SCORE = "Personality score must be between " + MIN_PERSONALITY_SCORE + " and " + MAX_PERSONALITY_SCORE;
}
