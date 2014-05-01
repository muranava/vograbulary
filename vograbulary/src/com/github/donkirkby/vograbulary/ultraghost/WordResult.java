package com.github.donkirkby.vograbulary.ultraghost;

public enum WordResult {
    //stopJesting
    UNKNOWN,
    NOT_A_WORD, 
    TOO_SHORT,
    VALID, 
    NOT_A_MATCH, 
    SHORTER(1),
    EARLIER(2),
    LONGER(3), 
    LATER(3), 
    NOT_IMPROVED(3), 
    SKIPPED(1),
    WORD_FOUND(-1), // Failed skip
    IMPROVEMENT_NOT_A_WORD(3, "not a word"), 
    IMPROVEMENT_NOT_A_MATCH(3, "not a match"),
    IMPROVEMENT_TOO_SHORT(3, "too short"),
    IMPROVED_SKIP_NOT_A_WORD(1, "not a word"),
    IMPROVED_SKIP_NOT_A_MATCH(1, "not a match"),
    IMPROVED_SKIP_TOO_SHORT(1, "too short");
    //resumeJesting
    
    private int score;
    private String name;
    
    private WordResult() {
        this(0);
    }
    
    private WordResult(int score) {
        this(score, null);
        this.name = name().toLowerCase().replace('_', ' ');
        addScore(score);
    }

    private void addScore(int score) {
        if (score != 0) {
            String sign = score > 0 ? "+" : "";
            this.name += " (" + sign + Integer.toString(score) + ")";
        }
    }
    
    private WordResult(int score, String name) {
        this.score = score;
        this.name = name;
        addScore(score);
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public int getScore() {
        return score;
    }
}
