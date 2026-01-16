package com.bomberman.protocol;

import java.io.Serial;
import java.io.Serializable;

public abstract class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


    protected Type type;
    protected long timestamp;


    public Message(Type type) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();

    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public enum Type {
        PLAYER_MOVE,
        PLACE_BOMB,
        GAME_STATE,
        PLAYER_JOIN,
        PLAYER_LEAVE,
        SCORE_UPDATE,
        GAME_START,
        GAME_OVER
    }
}
