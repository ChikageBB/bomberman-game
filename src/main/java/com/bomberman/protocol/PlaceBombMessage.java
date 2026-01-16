package com.bomberman.protocol;

public class PlaceBombMessage extends Message{

    private final int playerId;
    private final int x, y;


    public PlaceBombMessage(int playerId, int x, int y) {
        super(Type.PLACE_BOMB);
        this.playerId = playerId;
        this.x = x;
        this.y = y;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
