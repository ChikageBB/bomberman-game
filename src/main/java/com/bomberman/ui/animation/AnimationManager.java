package com.bomberman.ui.animation;

import com.bomberman.ui.PlayerAnimationState;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class AnimationManager {
    private final CopyOnWriteArrayList<ExplosionAnimation> explosions;
    private final CopyOnWriteArrayList<WallDestructionAnimation> wallDestruction;
    private final ConcurrentHashMap<Integer, PlayerAnimationState> playerAnimations;


    public AnimationManager() {
        this.explosions = new CopyOnWriteArrayList<>();
        this.wallDestruction = new CopyOnWriteArrayList<>();
        this.playerAnimations = new ConcurrentHashMap<>();
    }

    public void addExplosion(int x, int y) {
        explosions.add(new ExplosionAnimation(x, y));
    }

    public void addWallDestruction(int gridX, int gridY) {
        wallDestruction.add(new WallDestructionAnimation(gridX, gridY));
    }

    public PlayerAnimationState getPlayerAnimation(int playerId) {
        return playerAnimations.computeIfAbsent(playerId, k -> new PlayerAnimationState());
    }


    public Map<Integer, PlayerAnimationState> getPlayerAnimations() {
        return playerAnimations;
    }

    public void update() {
        explosions.removeIf(ExplosionAnimation::isFinished);
        wallDestruction.removeIf(WallDestructionAnimation::isFinished);
    }

    public List<ExplosionAnimation> getActiveExplosions() {
        return Collections.unmodifiableList(explosions);
    }

    public List<WallDestructionAnimation> getActiveWallDestructions() {
        return Collections.unmodifiableList(wallDestruction);
    }

    public int getExplosionCount() {
        return explosions.size();
    }

    public int getWallDestructionCount() {
        return wallDestruction.size();
    }

    public boolean hasActiveAnimations() {
        return !explosions.isEmpty() || !wallDestruction.isEmpty();
    }

    public void removePlayerAnimation(int playerId) {
        playerAnimations.remove(playerId);
    }

    public void clear() {
        explosions.clear();
        wallDestruction.clear();
        playerAnimations.clear();
    }

    public String getStatistics() {
        return String.format(
                "Animations: Explosions=%d, WallDestructions=%d, Players=%d",
                explosions.size(),
                wallDestruction.size(),
                playerAnimations.size()
        );
    }
}
