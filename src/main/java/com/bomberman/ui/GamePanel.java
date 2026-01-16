package com.bomberman.ui;

import com.bomberman.client.GameClient;
import com.bomberman.client.TextureManager;
import com.bomberman.game.GameMap;
import com.bomberman.protocol.GameState;
import com.bomberman.protocol.PlayerState;
import com.bomberman.ui.animation.AnimationManager;
import com.bomberman.ui.rendering.MapBounds;
import com.bomberman.ui.rendering.TextureProvider;
import com.bomberman.ui.rendering.TileValidator;
import com.bomberman.ui.rendering.renderers.*;
import com.bomberman.ui.state.GameStateComparator;
import com.bomberman.ui.rendering.Renderer;


import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GamePanel extends JPanel {
    private final GameClient client;
    private GameState currentState;
    private GameState previousState;

    private final TextureManager textureManager;
    private final AnimationManager animationManager;
    private final GameStateComparator stateComparator;
    private final CopyOnWriteArrayList<Renderer> renderers;


    private final int width;
    private final int height;

    public GamePanel(GameClient gameClient) {
        this.client = gameClient;
        this.textureManager = TextureManager.getInstance();
        this.animationManager = new AnimationManager();
        this.stateComparator = new GameStateComparator();
        this.renderers = new CopyOnWriteArrayList<>();

        textureManager.loadTextures();

        this.width = GameMap.MAP_WIDTH * GameMap.TILE_SIZE;
        this.height = GameMap.MAP_HEIGHT * GameMap.TILE_SIZE;

        setupUI();
        startRenderLoop();
    }

    private void setupUI() {
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
    }

    private void startRenderLoop() {
        Timer timer = new Timer(33, e -> repaint());
        timer.start();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (currentState == null) {
            renderWaitingScreen(g2d);
            return;
        }

        for (Renderer renderer: renderers) {
            renderer.render(g2d);
        }
    }

    public void setGameState(GameState state) {
        if (state == null) return;

        // Инициализируем анимации для новых игроков
        for (var player : state.getPlayerStates()) {
            animationManager.getPlayerAnimation(player.getId());
        }

        // Обнаруживаем изменения и создаём анимации
        if (currentState != null) {
            stateComparator.detectChanges(
                    currentState,
                    state,
                    animationManager::addExplosion,
                    animationManager::addWallDestruction
            );
        }

        this.previousState = this.currentState;
        this.currentState = state;

        updateRenderers();
    }

    private void updateRenderers() {
        if (currentState == null) return;

        renderers.clear();
        animationManager.update();

        TextureProviderAdapter textureProvider = new TextureProviderAdapter(textureManager);
        MapBoundsImpl mapBounds = new MapBoundsImpl();
        TileValidatorImpl tileValidator = new TileValidatorImpl(currentState.getMap());


        renderers.add(new MapRenderer(
                currentState.getMap(),
                textureProvider,
                GameMap.TILE_SIZE
        ));

        // 2. Разрушение стен
        renderers.add(new WallDestructionRenderer(
                animationManager.getActiveWallDestructions(),
                textureProvider,
                GameMap.TILE_SIZE
        ));

        // 3. Взрывы
        renderers.add(new ExplosionRenderer(
                animationManager.getActiveExplosions(),
                textureProvider,
                mapBounds,
                tileValidator,
                GameMap.TILE_SIZE
        ));

        // 4. Бомбы
        renderers.add(new BombRenderer(
                currentState.getBombStates(),
                textureProvider,
                GameMap.TILE_SIZE
        ));

        // 5. Игроки
        PlayerState[] prevPlayers = previousState != null ?
                previousState.getPlayerStates() : null;

        renderers.add(new PlayerRenderer(
                currentState.getPlayerStates(),
                prevPlayers,
                animationManager.getPlayerAnimations(),
                textureProvider,
                GameMap.TILE_SIZE,
                GameMap.MAX_PLAYERS
        ));
    }

    private void renderWaitingScreen(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        String text = "Waiting for players...";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = getHeight() / 2;
        g2d.drawString(text, x, y);
    }


    private static class MapBoundsImpl implements MapBounds {

        @Override
        public boolean isInBounds(int x, int y) {
            return x >= 0 && x < GameMap.MAP_WIDTH
                    && y >= 0 && y < GameMap.MAP_HEIGHT;
        }
    }

    private static class TileValidatorImpl implements TileValidator {

        private final int[][] map;

        public TileValidatorImpl(int[][] map) {
            this.map = map;
        }

        @Override
        public boolean isExplosionPassable(int x, int y) {
            int tileType = map[y][x];
            return tileType != TextureManager.TILE_WALL_SOLID &&
                    tileType != TextureManager.TILE_WALL_DESTRUCTIBLE;
        }

        @Override
        public boolean isWalkable(int x, int y) {
            return map[y][x] == TextureManager.TILE_FLOOR;
        }
    }

    private static class TextureProviderAdapter implements TextureProvider {


        private final TextureManager textureManager;

        public TextureProviderAdapter(TextureManager textureManager) {
            this.textureManager = textureManager;
        }

        @Override
        public BufferedImage getTileTexture(int tileType) {
            return textureManager.getTileTexture(tileType);
        }

        @Override
        public BufferedImage getPlayerTexture(int colorId, String direction, int frame) {
            return textureManager.getPlayerTexture(colorId, direction, frame);
        }

        @Override
        public BufferedImage getBombTexture(int stage) {
            return textureManager.getBombTexture(stage);
        }

        @Override
        public BufferedImage getExplosionCenterTexture(int stage) {
            return textureManager.getExplosionCenterTexture(stage);
        }

        @Override
        public BufferedImage getExplosionTexture(String type, int stage) {
            return textureManager.getExplosionTexture(type, stage);
        }

        @Override
        public BufferedImage getDestructibleTexture(int stage) {
            return textureManager.getDestructibleTexture(stage);
        }
    }
}