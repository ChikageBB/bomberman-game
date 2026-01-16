package com.bomberman.protocol;

import java.io.Serial;
import java.io.Serializable;

public class GameState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final PlayerState[] playerStates;
    private final BombState[] bombStates;
    private final int[][] map;
    private final long gameTime;


    public GameState(PlayerState[] playerStates, BombState[] bombStates, int[][] map, long gameTime) {
        this.playerStates = playerStates;
        this.bombStates = bombStates;
        this.map = map;
        this.gameTime = gameTime;
    }

    public PlayerState[] getPlayerStates() {
        return playerStates;
    }

    public BombState[] getBombStates() {
        return bombStates;
    }

    public int[][] getMap() {
        return map;
    }

    public long getGameTime() {
        return gameTime;
    }

}
