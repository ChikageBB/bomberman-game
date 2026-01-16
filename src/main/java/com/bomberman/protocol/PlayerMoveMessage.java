package com.bomberman.protocol;

public class PlayerMoveMessage extends Message{

    private final int playerId;
    private final int x, y;
    private final Direction direction;

    public PlayerMoveMessage(int playerId, int x, int y, Direction direction) {
        super(Type.PLAYER_MOVE);
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.direction = direction;
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

    public Direction getDirection() {
        return direction;
    }

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
}
