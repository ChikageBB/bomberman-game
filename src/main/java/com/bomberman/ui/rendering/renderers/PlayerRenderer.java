package com.bomberman.ui.rendering.renderers;

import com.bomberman.client.Direction;
import com.bomberman.protocol.PlayerState;
import com.bomberman.ui.PlayerAnimationState;
import com.bomberman.ui.rendering.Renderer;
import com.bomberman.ui.rendering.TextureProvider;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

public class PlayerRenderer implements Renderer {

    private final PlayerState[] players;
    private final PlayerState[] previousPlayers;
    private final Map<Integer, PlayerAnimationState> animations;
    private final TextureProvider textureProvider;
    private final int tileSize;
    private final int maxPlayers;


    public PlayerRenderer(PlayerState[] players,
                          PlayerState[] previousPlayers,
                          Map<Integer, PlayerAnimationState> animations,
                          TextureProvider textureProvider,
                          int tileSize,
                          int maxPlayers) {
        this.players = players;
        this.previousPlayers = previousPlayers;
        this.animations = animations;
        this.textureProvider = textureProvider;
        this.tileSize = tileSize;
        this.maxPlayers = maxPlayers;
    }

    @Override
    public void render(Graphics2D g2d) {
        for (PlayerState player: players) {
            if (!player.isAlive()) continue;
            renderPlayer(g2d, player);
        }
    }

    private void renderPlayer(Graphics2D g2d, PlayerState player) {
        int colorId = player.getId() % maxPlayers;

        PlayerAnimationState animState = animations.get(player.getId());
        if (animState == null) return;

        boolean isMoving = detectMovement(player);
        animState.updateMovement(isMoving, player.getDirection());

        String direction = directionToString(animState.getCurrentDirection());
        int frame = animState.getCurrentFrame();

        BufferedImage texture = textureProvider.getPlayerTexture(colorId, direction, frame);
        g2d.drawImage(texture, player.getX(), player.getY(), tileSize, tileSize, null);

        renderPlayerName(g2d, player);
    }

    private void renderPlayerName(Graphics2D g2d, PlayerState player) {
        String name = player.getName();
        Font font = new Font("Arial", Font.BOLD, 10);
        g2d.setFont(font);

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(name);
        int textX = player.getX() + (tileSize - textWidth) / 2;
        int textY = player.getY() - 5;

        // Тень
        g2d.setColor(Color.BLACK);
        g2d.drawString(name, textX + 1, textY + 1);

        // Текст
        g2d.setColor(Color.WHITE);
        g2d.drawString(name, textX, textY);
    }


    private boolean detectMovement(PlayerState state) {
        if (previousPlayers == null) return false;

        for (PlayerState prev: previousPlayers) {
            if (prev.getId() == state.getId()) {
                return prev.getY() != state.getY() || prev.getX() != state.getX();
            }
        }
        return false;
    }

    private String directionToString(Direction direction) {
        return switch (direction) {
            case UP -> "up";
            case DOWN -> "down";
            case LEFT -> "left";
            case RIGHT -> "right";
        };
    }
}
