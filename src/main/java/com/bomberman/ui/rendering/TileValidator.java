package com.bomberman.ui.rendering;

public interface TileValidator {
    boolean isExplosionPassable(int x, int y);
    boolean isWalkable(int x, int y);
}
