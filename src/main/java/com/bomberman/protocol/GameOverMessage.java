package com.bomberman.protocol;

public class GameOverMessage extends Message{
    private final int winnerId;
    private final String winnerName;
    private final int finalScore;


    public GameOverMessage(int winnerId, String winnerName, int finalScore) {
        super(Type.GAME_OVER);
        this.winnerId = winnerId;
        this.winnerName = winnerName;
        this.finalScore = finalScore;
    }

    public int getWinnerId() {
        return winnerId;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public int getFinalScore() {
        return finalScore;
    }
}
