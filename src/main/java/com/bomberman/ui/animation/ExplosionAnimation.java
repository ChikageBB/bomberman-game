package com.bomberman.ui.animation;

public class ExplosionAnimation {
    private int centerX, centerY;
    private long startTime;
    private static final long DURATION = 600;

    public ExplosionAnimation(int centerX, int centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.startTime = System.currentTimeMillis();
    }

    public boolean isFinished() {
        return System.currentTimeMillis() - startTime >= DURATION;
    }

    public int getStage() {
        long elapsed = System.currentTimeMillis() - startTime;
        return Math.min(4, ((int)(elapsed / 150)) + 1);
    }

    public int getCenterX() { return centerX; }
    public int getCenterY() { return centerY; }
}