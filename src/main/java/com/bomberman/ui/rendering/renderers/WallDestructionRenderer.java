package com.bomberman.ui.rendering.renderers;

import com.bomberman.game.WallDestruction;
import com.bomberman.ui.animation.WallDestructionAnimation;
import com.bomberman.ui.rendering.Renderer;
import com.bomberman.ui.rendering.TextureProvider;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class WallDestructionRenderer implements Renderer {

    private final List<WallDestructionAnimation> destructions;
    private final TextureProvider textureProvider;
    private final int tileSize;


    public WallDestructionRenderer(List<WallDestructionAnimation> destructions,
                                   TextureProvider textureProvider,
                                   int tileSize) {

        this.destructions = destructions;
        this.textureProvider = textureProvider;
        this.tileSize = tileSize;
    }

    @Override
    public void render(Graphics2D g2d) {
        for (WallDestructionAnimation destruction : destructions) {
            renderDestruction(g2d, destruction);
        }
    }

    private void renderDestruction(Graphics2D g2d, WallDestructionAnimation destruction) {
        int stage = destruction.getDestructionStage();
        BufferedImage texture = textureProvider.getDestructibleTexture(stage);

        if (texture == null) return;

        int px = destruction.getGridX() * tileSize;
        int py = destruction.getGridY() * tileSize;

        g2d.drawImage(texture, px, py, tileSize, tileSize, null);
    }
}
