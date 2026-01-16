package com.bomberman.ui.rendering;


import java.awt.image.BufferedImage;

public interface TextureProvider {
    BufferedImage getTileTexture(int tileType);

    BufferedImage getPlayerTexture(int colorId, String direction, int frame);

    BufferedImage getBombTexture(int stage);

    BufferedImage getExplosionCenterTexture(int stage);

    BufferedImage getExplosionTexture(String type, int stage);

    BufferedImage getDestructibleTexture(int stage);
}
