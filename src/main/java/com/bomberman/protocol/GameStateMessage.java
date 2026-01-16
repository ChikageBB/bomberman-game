package com.bomberman.protocol;

import java.io.Serializable;

public class GameStateMessage extends Message {

    private final GameState gameState;

    public GameStateMessage(GameState gameState) {
        super(Type.GAME_STATE);
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }
}
