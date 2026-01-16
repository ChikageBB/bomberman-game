package com.bomberman.game;

public class WallDestruction {
    private int gridX, gridY;
    private long startTime;
    private static final long DURATION = 800; // 800ms
    private static final long STAGE_DURATION = DURATION / 8; // 100ms на кадр

    public WallDestruction(int gridX, int gridY) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.startTime = System.currentTimeMillis();
    }

    public boolean isFinished() {
        return System.currentTimeMillis() - startTime >= DURATION;
    }

    public int getDestructionStage() {
        long elapsed = System.currentTimeMillis() - startTime;
        int stage = (int)(elapsed / STAGE_DURATION) + 1;
        return Math.min(8, stage);
    }

    public int getGridX() { return gridX; }
    public int getGridY() { return gridY; }
}
