package com.bomberman.ui.rendering.renderers;

import com.bomberman.ui.animation.ExplosionAnimation;
import com.bomberman.ui.rendering.MapBounds;
import com.bomberman.ui.rendering.Renderer;
import com.bomberman.ui.rendering.TextureProvider;
import com.bomberman.ui.rendering.TileValidator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class ExplosionRenderer implements Renderer {

    private final List<ExplosionAnimation> explosions;
    private final TextureProvider textureProvider;
    private final MapBounds mapBounds;
    private final TileValidator tileValidator;
    private final int tileSize;


    public ExplosionRenderer(List<ExplosionAnimation> explosions,
                             TextureProvider textureProvider,
                             MapBounds mapBounds,
                             TileValidator tileValidator,
                             int tileSize) {
        this.explosions = explosions;
        this.textureProvider = textureProvider;
        this.mapBounds = mapBounds;
        this.tileValidator = tileValidator;
        this.tileSize = tileSize;
    }


    @Override
    public void render(Graphics2D g2d) {
        for (ExplosionAnimation explosion : explosions) {
            renderExplosion(g2d, explosion);
        }
    }

    private void renderExplosion(Graphics2D g2d, ExplosionAnimation explosion) {
        int stage = explosion.getStage();
        int centerX = explosion.getCenterX();
        int centerY = explosion.getCenterY();

        renderExplosionTile(g2d, centerX, centerY, textureProvider.getExplosionCenterTexture(stage));

        renderExplosionRayLine(g2d, centerX, centerY, 0, -1, "up", "vertical", stage);    // UP
        renderExplosionRayLine(g2d, centerX, centerY, 0, 1, "down", "vertical", stage);   // DOWN
        renderExplosionRayLine(g2d, centerX, centerY, -1, 0, "left", "horizontal", stage); // LEFT
        renderExplosionRayLine(g2d, centerX, centerY, 1, 0, "right", "horizontal", stage); // RIGHT
    }

    private void renderExplosionRayLine(Graphics2D g2d, int startX, int startY,
                                        int dx, int dy, String endType, String middleType,  int stage) {

        final int MAX_RANGE = 2;

        for (int distance = 1; distance <= MAX_RANGE; distance++) {
            int tx = startX + (dx * distance);
            int ty = startY + (dy * distance);

            if (!mapBounds.isInBounds(tx, ty)) break;

            if (!tileValidator.isExplosionPassable(tx, ty)) break;


            String textureType = (distance == MAX_RANGE) ? endType : middleType;

            BufferedImage texture = textureProvider.getExplosionTexture(textureType, stage);
            renderExplosionTile(g2d, tx, ty, texture);
        }
    }

    private void renderExplosionTile(Graphics2D g2d, int gridX, int gridY, BufferedImage image) {
        if (image == null) return;
        int px = gridX * tileSize;
        int py = gridY * tileSize;
        g2d.drawImage(image, px, py, tileSize, tileSize, null);
    }
}
