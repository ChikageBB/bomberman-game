package com.bomberman.ui.state;

import com.bomberman.client.TextureManager;
import com.bomberman.protocol.BombState;
import com.bomberman.protocol.GameState;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public class GameStateComparator {
    public void detectChanges(GameState oldState, GameState newState,
                              BiConsumer<Integer, Integer> onExplosion,
                              BiConsumer<Integer, Integer> onWallDestroyed) {
        if (oldState == null || newState == null) return;

        detectExplosions(oldState.getBombStates(), newState.getBombStates(), onExplosion);
        detectWallDestruction(oldState.getMap(), newState.getMap(), onWallDestroyed);
    }

    private void detectExplosions(BombState[] oldBombs, BombState[] newBombs,
                                  BiConsumer<Integer, Integer> onExplosion) {
        Set<Integer> newBombIds = new HashSet<>();
        for (BombState bomb : newBombs) {
            newBombIds.add(bomb.getId());
        }

        for (BombState oldBomb : oldBombs) {
            if (!newBombIds.contains(oldBomb.getId())) {
                onExplosion.accept(oldBomb.getX(), oldBomb.getY());
            }
        }
    }

    private void detectWallDestruction(int[][] oldMap, int[][] newMap,
                                       BiConsumer<Integer, Integer> onWallDestroyed) {
        for (int y = 0; y < oldMap.length; y++) {
            for (int x = 0; x < oldMap[y].length; x++) {
                if (oldMap[y][x] == TextureManager.TILE_WALL_DESTRUCTIBLE &&
                        newMap[y][x] == TextureManager.TILE_FLOOR) {
                    onWallDestroyed.accept(x, y);
                }
            }
        }
    }
}
