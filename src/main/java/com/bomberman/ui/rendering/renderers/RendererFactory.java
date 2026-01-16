package com.bomberman.ui.rendering.renderers;

import com.bomberman.protocol.BombState;
import com.bomberman.protocol.PlayerState;
import com.bomberman.ui.PlayerAnimationState;
import com.bomberman.ui.animation.ExplosionAnimation;
import com.bomberman.ui.animation.WallDestructionAnimation;
import com.bomberman.ui.rendering.MapBounds;
import com.bomberman.ui.rendering.TextureProvider;
import com.bomberman.ui.rendering.TileValidator;

import java.util.List;
import java.util.Map;

public class RendererFactory {
    private final TextureProvider textureProvider;
    private final int tileSize;


    public RendererFactory(TextureProvider textureProvider, int tileSize) {
        this.textureProvider = textureProvider;
        this.tileSize = tileSize;
    }

    public MapRenderer createMapRenderer(int[][] map) {
        return new MapRenderer(map, textureProvider, tileSize);
    }

    public BombRenderer createBombRenderer(BombState[] bombs) {
        return new BombRenderer(bombs, textureProvider, tileSize);
    }

    public WallDestructionRenderer createWallDestructionRenderer(
            List<WallDestructionAnimation> destructions) {
        return new WallDestructionRenderer(destructions, textureProvider, tileSize);
    }

    public ExplosionRenderer createExplosionRenderer(
            List<ExplosionAnimation> explosions,
            MapBounds mapBounds,
            TileValidator validator) {
        return new ExplosionRenderer(
                explosions,
                textureProvider,
                mapBounds,
                validator,
                tileSize
        );
    }

    public PlayerRenderer createPlayerRenderer(
            PlayerState[] players,
            PlayerState[] previousPlayers,
            Map<Integer, PlayerAnimationState> animations,
            int maxPlayers) {
        return new PlayerRenderer(
                players,
                previousPlayers,
                animations,
                textureProvider,
                tileSize,
                maxPlayers
        );
    }

}
