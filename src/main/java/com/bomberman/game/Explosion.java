package com.bomberman.game;

import java.awt.*;
import java.util.List;


public class Explosion {
    private List<Point> tiles;
    private long startTime;
    private Point center;
    private static final long DURATION = 600; // 600ms на всю анимацию
    private static final long STAGE_DURATION = DURATION / 4; // 150ms на кадр

    public Explosion(List<Point> tiles) {
        this.tiles = tiles;
        this.startTime = System.currentTimeMillis();
        if (!tiles.isEmpty()) {
            this.center = tiles.get(0); // Первый тайл - центр
        }
    }

    public boolean isFinished() {
        return System.currentTimeMillis() - startTime >= DURATION;
    }

    public int getAnimationStage() {
        long elapsed = System.currentTimeMillis() - startTime;
        int stage = (int)(elapsed / STAGE_DURATION) + 1;
        return Math.min(4, stage);
    }

    public List<Point> getTiles() {
        return tiles;
    }

    public Point getCenter() {
        return center;
    }


    public String getDirection(Point tile) {
        if (center == null || tile.equals(center)) {
            return "center";
        }

        if (tile.x == center.x) {
            return tile.y < center.y ? "up" : "down";
        } else if (tile.y == center.y) {
            return tile.x < center.x ? "left" : "right";
        }

        return "center";
    }
}
