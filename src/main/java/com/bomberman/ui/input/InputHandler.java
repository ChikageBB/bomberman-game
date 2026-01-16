package com.bomberman.ui.input;

import com.bomberman.client.GameClient;
import com.bomberman.protocol.PlayerMoveMessage;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InputHandler extends KeyAdapter {
    private final GameClient client;
    private final boolean[] keys;
    private boolean wasMoving;

    public InputHandler(GameClient client) {
        this.client = client;
        this.keys = new boolean[256];
        this.wasMoving = false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        handleKeyPress(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    private void handleKeyPress(int keyCode) {
        if (keyCode == KeyEvent.VK_SPACE) {
            client.sendPlaceBomb();
        }
    }

    public void processMovement() {
        PlayerMoveMessage.Direction direction = getCurrentDirection();
        boolean isMoving = direction != null;

        if (isMoving) {
            client.sendMove(direction);
            wasMoving = true;
        } else if (wasMoving) {
            wasMoving = false;
        }
    }

    private PlayerMoveMessage.Direction getCurrentDirection() {
        if (keys[KeyEvent.VK_W] || keys[KeyEvent.VK_UP]) {
            return PlayerMoveMessage.Direction.UP;
        } else if (keys[KeyEvent.VK_S] || keys[KeyEvent.VK_DOWN]) {
            return PlayerMoveMessage.Direction.DOWN;
        } else if (keys[KeyEvent.VK_A] || keys[KeyEvent.VK_LEFT]) {
            return PlayerMoveMessage.Direction.LEFT;
        } else if (keys[KeyEvent.VK_D] || keys[KeyEvent.VK_RIGHT]) {
            return PlayerMoveMessage.Direction.RIGHT;
        }
        return null;
    }

    private boolean isKeyPressed(int keyCode) {
        return keys[keyCode];
    }
}
