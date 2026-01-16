package com.bomberman.ui.animation;

public class WallDestructionAnimation {

    private final int gridX;
    private final int gridY;
    private final long startTime;
    private static final long DURATION = 600;
    private static final int MAX_STAGES = 8;

    public WallDestructionAnimation(int gridX, int gridY) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.startTime = System.currentTimeMillis();
    }

    public boolean isFinished() {
        return System.currentTimeMillis() - startTime >= DURATION;
    }

    public int getDestructionStage() {
        long elapsed = System.currentTimeMillis() - startTime;
        return Math.min(MAX_STAGES, (int)(elapsed / 150) + 1);
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }
}
