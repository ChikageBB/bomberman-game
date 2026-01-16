package com.bomberman.protocol;

public class ScoreUpdateMessage extends Message{

    private final String name;
    private final int score;
    private final String reason;


    public ScoreUpdateMessage(String name, int score, String reason) {
        super(Type.SCORE_UPDATE);
        this.name = name;
        this.score = score;
        this.reason = reason;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public String getReason() {
        return reason;
    }
}
