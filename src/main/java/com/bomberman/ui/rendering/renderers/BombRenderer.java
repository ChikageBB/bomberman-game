package com.bomberman.ui.rendering.renderers;

import com.bomberman.protocol.BombState;
import com.bomberman.ui.rendering.Renderer;
import com.bomberman.ui.rendering.TextureProvider;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BombRenderer implements Renderer {

    private final BombState[] bombs;
    private final TextureProvider textureProvider;
    private final int tileSize;

    public BombRenderer(BombState[] bombs, TextureProvider textureProvider, int tileSize) {
        this.bombs = bombs;
        this.textureProvider = textureProvider;
        this.tileSize = tileSize;
    }

    @Override
    public void render(Graphics2D g2d) {
        for (BombState bomb: bombs) {
            renderBomb(g2d, bomb);
        }
    }

    private void renderBomb(Graphics2D g2d, BombState bomb) {
        int stage = calculateBombStage(bomb);
        BufferedImage texture = textureProvider.getBombTexture(stage);

        int px = bomb.getX() * tileSize;
        int py = bomb.getY() * tileSize;

        g2d.drawImage(texture, px, py, tileSize, tileSize, null);
    }

    private int calculateBombStage(BombState bomb) {
        long elapsed = System.currentTimeMillis() - bomb.getPlacedTime();

        if (elapsed < 1000) {
            // Медленная анимация в начале (500ms на кадр)
            return ((int)(elapsed / 500) % 4) + 1;
        } else if (elapsed < 2000) {
            // Средняя скорость (300ms на кадр)
            return ((int)((elapsed - 1000) / 300) % 4) + 1;
        } else {
            // Быстрая анимация перед взрывом (150ms на кадр)
            return ((int)((elapsed - 2000) / 150) % 4) + 1;
        }
    }
}
