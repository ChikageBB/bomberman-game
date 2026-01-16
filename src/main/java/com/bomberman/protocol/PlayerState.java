package com.bomberman.protocol;

import com.bomberman.client.Direction;

import java.io.Serial;
import java.io.Serializable;

public class PlayerState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


    private final int id;
    private final String name;
    private final int x, y;
    private final int score;
    private final boolean alive;
    private final Direction direction;


    public PlayerState(int id, String name, int x, int y, int score, boolean alive, Direction direction) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.score = score;
        this.alive = alive;
        this.direction = direction;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getScore() {
        return score;
    }

    public boolean isAlive() {
        return alive;
    }

    public Direction getDirection() {
        return direction;
    }
}
