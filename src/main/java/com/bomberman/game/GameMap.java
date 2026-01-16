package com.bomberman.game;

import com.bomberman.client.TextureManager;

import java.awt.*;

public class GameMap {
    public static final int TILE_SIZE = 32;
    public static final int MAP_WIDTH = 15;
    public static final int MAP_HEIGHT = 13;
    public static final int MAX_PLAYERS = 4;

    private int[][] tiles;

    public GameMap() {
        tiles = new int[MAP_HEIGHT][MAP_WIDTH];
        initializeMap();
    }

    private void initializeMap() {
        int[][] staticMap = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 1},
                {1, 0, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 0, 1},
                {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
                {1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1},
                {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
                {1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1},
                {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
                {1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1},
                {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
                {1, 0, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 0, 1},
                {1, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };

        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                tiles[y][x] = staticMap[y][x];
            }
        }
    }

    public int getTile(int gridX, int gridY) {
        if (gridX < 0 || gridX >= MAP_WIDTH || gridY < 0 || gridY >= MAP_HEIGHT) {
            return TextureManager.TILE_WALL_SOLID;
        }
        return tiles[gridY][gridX];
    }

    public void setTile(int gridX, int gridY, int tileType) {
        if (isInBounds(gridX, gridY)) {
            tiles[gridY][gridX] = tileType;
        }
    }

    public boolean isInBounds(int gridX, int gridY) {
        return gridX >= 0 && gridX < MAP_WIDTH && gridY >= 0 && gridY < MAP_HEIGHT;
    }

    public boolean isWalkable(int gridX, int gridY) {
        int tile = getTile(gridX, gridY);
        return tile == TextureManager.TILE_FLOOR;
    }

    public boolean canPlaceBomb(int gridX, int gridY) {
        if (!isInBounds(gridX, gridY)) {
            return false;
        }

        int tile = getTile(gridX, gridY);
        return tile == TextureManager.TILE_FLOOR;
    }


    public boolean isCollision(int pixelX, int pixelY, int width, int height) {

        int hitboxMargin = 6;
        int checkX = pixelX + hitboxMargin;
        int checkY = pixelY + hitboxMargin;
        int checkWidth = width - hitboxMargin * 2;
        int checkHeight = height - hitboxMargin * 2;

        // Проверяем 4 угла хитбокса
        int x1 = checkX / TILE_SIZE;
        int y1 = checkY / TILE_SIZE;
        int x2 = (checkX + checkWidth - 1) / TILE_SIZE;
        int y2 = (checkY + checkHeight - 1) / TILE_SIZE;

        return !isWalkable(x1, y1) || !isWalkable(x2, y1) ||
                !isWalkable(x1, y2) || !isWalkable(x2, y2);
    }

    public void destroyTile(int gridX, int gridY) {
        if (getTile(gridX, gridY) == TextureManager.TILE_WALL_DESTRUCTIBLE) {
            setTile(gridX, gridY, TextureManager.TILE_FLOOR);
        }
    }

    public int[][] getTiles() {
        return tiles;
    }

    public int getWidth() { return MAP_WIDTH; }
    public int getHeight() { return MAP_HEIGHT; }

    public static Point getStartPosition(int playerId) {
        switch (playerId % MAX_PLAYERS) {
            case 0: return new Point(1, 1);
            case 1: return new Point(MAP_WIDTH - 2, 1);
            case 2: return new Point(1, MAP_HEIGHT - 2);
            case 3: return new Point(MAP_WIDTH - 2, MAP_HEIGHT - 2);
            default: return new Point(1, 1);
        }
    }
}
