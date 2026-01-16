package com.bomberman.client;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TextureManager {
    private static TextureManager instance;
    private Map<String, BufferedImage> textures;

    public static final int TILE_FLOOR = 0;
    public static final int TILE_WALL_SOLID = 1;
    public static final int TILE_WALL_DESTRUCTIBLE = 2;

    private TextureManager() {
        textures = new HashMap<>();
    }

    public static TextureManager getInstance() {
        if (instance == null) {
            instance = new TextureManager();
        }
        return instance;
    }

    public void loadTextures() {
        System.out.println("Loading textures...");

        loadTexture("grass", "sprites/block/grass.png");
        loadTexture("wall", "sprites/block/wall.png");
        loadTexture("wall2", "sprites/block/wall2.png");


        for (int i = 1; i <= 8; i++) {
            loadTexture("destroyedblock_" + i,
                    "sprites/block/destroyedblock_" + i + ".png");
        }

        for (int i = 1; i <= 4; i++) {
            loadTexture("bomb_" + i,
                    "sprites/bomb/bomb_" + i + ".png");
        }

        for (int stage = 1; stage <= 4; stage++) {
            loadTexture("explosion_center_" + stage,
                    "sprites/explosion/" + stage + "/explosion_center_" + stage + ".png");
            loadTexture("explosion_up_" + stage,
                    "sprites/explosion/" + stage + "/explosion_up_" + stage + ".png");
            loadTexture("explosion_down_" + stage,
                    "sprites/explosion/" + stage + "/explosion_down_" + stage + ".png");
            loadTexture("explosion_right_" + stage,
                    "sprites/explosion/" + stage + "/explosion_right_" + stage + ".png");
            loadTexture("explosion_left_" + stage,
                    "sprites/explosion/" + stage + "/explosion_left_" + stage + ".png");
            loadTexture("explosion_horizontal_" + stage,
                    "sprites/explosion/" + stage + "/explosion_horizontal_" + stage + ".png");
            loadTexture("explosion_vertical_" + stage,
                    "sprites/explosion/" + stage + "/explosion_vertical_" + stage + ".png");

            String[] colors = {"white", "red", "blue", "black"};
            String[] directions = {"up", "down", "left", "right"};

            for (String color: colors) {
                for (String dir: directions) {
                    loadTexture("player_" + color + "_" + dir + "_1",
                            "sprites/players/" + color + "/" + dir + "_1.png");
                    loadTexture("player_" + color + "_" + dir + "_2",
                            "sprites/players/" + color + "/" + dir + "_2.png");
                    loadTexture("player_" + color + "_" + dir + "_3",
                            "sprites/players/" + color + "/" + dir + "_3.png");
                }
            }

            System.out.println("Loaded " + textures.size() + " textures");
        }
    }

    private void loadTexture(String name, String path) {
        try {
            BufferedImage image = ImageIO.read(
                    Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(path))
            );
            textures.put(name, image);

        } catch (IOException e) {
            System.err.println("Warning: Failed to load texture: " + path);
            BufferedImage placeholder = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            textures.put(name, placeholder);
        }
    }

    public BufferedImage getTexture(String name) {
        BufferedImage texture = textures.get(name);
        if (texture == null) {
            System.err.println("Texture not found: " + name);
            return new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        }
        return texture;
    }

    public BufferedImage getTileTexture(int tileType) {
        return switch (tileType) {
            case TILE_FLOOR -> getTexture("grass");
            case TILE_WALL_SOLID -> getTexture("wall");
            case TILE_WALL_DESTRUCTIBLE -> getTexture("wall2");
            default -> getTexture("grass");
        };
    }

    public BufferedImage getDestructibleTexture(int stage) {
        if (stage <= 0) return getTexture("wall2");
        stage = Math.min(8, stage);
        return getTexture("destroyedblock_" + stage);
    }

    public BufferedImage getBombTexture(int stage) {
        stage = Math.max(1, Math.min(4, stage));
        return getTexture("bomb_" + stage);
    }

    public BufferedImage getExplosionCenterTexture(int stage) {
        stage = Math.max(1, Math.min(4, stage));
        return getTexture("explosion_center_" + stage);
    }

    public BufferedImage getExplosionTexture(String direction, int stage) {
        stage = Math.max(1, Math.min(4, stage));
        return getTexture("explosion_" + direction + "_" + stage);
    }

    public BufferedImage getPlayerTexture(int colorId, String direction, int frame) {
        String[] colors = {"white", "red", "black", "blue"};
        colorId = Math.max(0, Math.min(3, colorId));
        frame = Math.max(1, Math.min(3, frame));

        String color = colors[colorId];
        return getTexture("player_" + color + "_" + direction + "_" + frame);
    }
}
