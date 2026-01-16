package com.bomberman.game;

import com.bomberman.client.TextureManager;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class Bomb {
    private static int nextId = 0;

    private int id;
    private int gridX, gridY;
    private long placedTime;
    private int ownerId;
    private int explosionRadius = 2;
    private static final long EXPLOSION_DELAY = 3000; // 3 секунды

    public Bomb(int gridX, int gridY, int ownerId) {
        this.id = nextId++;
        this.gridX = gridX;
        this.gridY = gridY;
        this.ownerId = ownerId;
        this.placedTime = System.currentTimeMillis();
    }

    public boolean shouldExplode() {
        return System.currentTimeMillis() - placedTime >= EXPLOSION_DELAY;
    }

    public float getTimeProgress() {
        long elapsed = System.currentTimeMillis() - placedTime;
        return Math.min(1.0f, (float)elapsed / EXPLOSION_DELAY);
    }

    public int getAnimationStage() {
        long elapsed = System.currentTimeMillis() - placedTime;

        if (elapsed >= EXPLOSION_DELAY) {
            return 4; // Последний кадр перед взрывом
        }

        if (elapsed < 1000) {
            return ((int)(elapsed / 500) % 4) + 1;
        } else if (elapsed < 2000) {
            return ((int)((elapsed - 1000) / 300) % 4) + 1;
        } else {
            return ((int)((elapsed - 2000) / 150) % 4) + 1;
        }
    }

    public List<Point> getExplosionTiles(GameMap map) {
        List<Point> tiles = new ArrayList();
        tiles.add(new Point(gridX, gridY)); // Центр

        // 4 направления: up, down, left, right
        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

        for (int[] dir : directions) {
            for (int i = 1; i <= explosionRadius; i++) {
                int tx = gridX + dir[0] * i;
                int ty = gridY + dir[1] * i;

                if (!map.isInBounds(tx, ty)) {
                    break;
                }

                int tileType = map.getTile(tx, ty);

                if (tileType == TextureManager.TILE_WALL_SOLID) {
                    break;
                }

                tiles.add(new Point(tx, ty));

                if (tileType == TextureManager.TILE_WALL_DESTRUCTIBLE) {
                    break;
                }
            }
        }

        return tiles;
    }

    public int getId() { return id; }
    public int getGridX() { return gridX; }
    public int getGridY() { return gridY; }
    public int getOwnerId() { return ownerId; }
    public long getPlacedTime() { return placedTime; }


}
