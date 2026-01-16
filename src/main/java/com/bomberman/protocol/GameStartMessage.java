package com.bomberman.protocol;

public class GameStartMessage extends Message{

    private final int gameDuration;

    public GameStartMessage(int gameDuration) {
        super(Type.GAME_START);
        this.gameDuration = gameDuration;
    }

    public int getGameDuration() {
        return gameDuration;
    }
}
