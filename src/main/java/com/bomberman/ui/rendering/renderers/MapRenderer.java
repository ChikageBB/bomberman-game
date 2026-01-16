package com.bomberman.ui.rendering.renderers;

import com.bomberman.ui.rendering.Renderer;
import com.bomberman.ui.rendering.TextureProvider;

import java.awt.*;

public class MapRenderer implements Renderer {
    private final int[][] tiles;
    private final TextureProvider textureProvider;
    private final int tileSize;


    public MapRenderer(int[][] tiles, TextureProvider textureProvider, int tileSize) {
        this.tiles = tiles;
        this.textureProvider = textureProvider;
        this.tileSize = tileSize;
    }


    @Override
    public void render(Graphics2D g2d) {
        for (int y = 0; y < tiles.length; y++) {
            for (int x = 0; x < tiles[y].length; x++) {
                int px = x * tileSize;
                int py = y * tileSize;
                var texture = textureProvider.getTileTexture(tiles[y][x]);
                g2d.drawImage(texture, px, py, tileSize, tileSize, null);
            }
        }
    }
}
