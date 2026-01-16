package com.bomberman.protocol;

import java.io.Serial;
import java.io.Serializable;

public class BombState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int id;
    private final int x, y;
    private final long placedTime;
    private final int ownerId;


    public BombState(int id, int x, int y, long placedTime, int ownerId) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.placedTime = placedTime;
        this.ownerId = ownerId;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public long getPlacedTime() {
        return placedTime;
    }

    public int getOwnerId() {
        return ownerId;
    }
}
