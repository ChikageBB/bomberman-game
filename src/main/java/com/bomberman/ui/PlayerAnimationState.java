package com.bomberman.ui;

import com.bomberman.client.Direction;

public class PlayerAnimationState {
    private Direction currentDirection = Direction.DOWN;
    private int currentFrame = 1;
    private long lastFrameTime = 0;
    private static final long FRAME_DURATION = 150;

    public void updateMovement(boolean isMoving, Direction direction) {
        currentDirection = direction;

        if (isMoving) {
            long now = System.currentTimeMillis();
            if (now - lastFrameTime > FRAME_DURATION) {
                if (currentFrame == 1) currentFrame = 2;
                else if (currentFrame == 2) currentFrame = 3;
                else currentFrame = 2;
                lastFrameTime = now;
            }
        } else {
            currentFrame = 1;
        }
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }
}