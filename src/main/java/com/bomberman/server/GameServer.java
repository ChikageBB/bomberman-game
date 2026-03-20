package com.bomberman.server;

import com.bomberman.client.Direction;
import com.bomberman.client.TextureManager;
import com.bomberman.game.*;
import com.bomberman.protocol.*;
import com.bomberman.storage.GameStorage;

import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class GameServer {
    private static final int PORT = 8888;
    private static final long GAME_DURATION = 180_000;
    private static final int TICK_RATE = 60;
    private static final long RESPAWN_DELAY = 3000;

    private ServerSocket serverSocket;
    private List<ClientHandler> clients;
    private GameMap map;
    private List<Player> players;
    private List<Bomb> bombs;
    private List<Explosion> explosions;
    private List<WallDestruction> wallDestructions;
    private boolean gameRunning;
    private long gameStartTime;
    private int nextPlayerId = 0;

    private Map<Integer, Long> playerDeathTimes;

    private ExecutorService threadPool;
    private ScheduledExecutorService gameLoopExecutor;

    public GameServer() {
        clients = new CopyOnWriteArrayList<>();
        players = new CopyOnWriteArrayList<>();
        bombs = new CopyOnWriteArrayList<>();
        explosions = new CopyOnWriteArrayList<>();
        wallDestructions = new CopyOnWriteArrayList<>();
        playerDeathTimes = new ConcurrentHashMap<>();
        map = new GameMap();
        threadPool = Executors.newCachedThreadPool();
        gameLoopExecutor = Executors.newSingleThreadScheduledExecutor();
        gameRunning = false;
    }

    private void startGame() {
        gameRunning = true;
        gameStartTime = System.currentTimeMillis();
        broadcast(new GameStartMessage((int)GAME_DURATION));
        System.out.println("GAME STARTED!");
    }

    private void endGame() {
        gameRunning = false;

        Player winner = null;
        int maxScore = -1;
        for (Player player : players) {
            if (player.getScore() > maxScore) {
                maxScore = player.getScore();
                winner = player;
            }
        }

        if (winner != null) {

            for (Player player: players) {
                boolean won = player.getId() == winner.getId();
                GameStorage.updatePlayerStats(
                        player.getName(),
                        player.getScore(),
                        won
                );
                System.out.println("Saved stats for " + player.getName() +
                        ": score=" + player.getScore() + ", won=" + won);
            }

            broadcast(new GameOverMessage(winner.getId(), winner.getName(), winner.getScore()));
            System.out.println("Game ended! Winner: " + winner.getName() +
                    " with score " + winner.getScore());
        }
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT); // слушаем порт 8888

            startGameLoop(); // запускаем игровой цикл

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                ClientHandler handler = new ClientHandler(clientSocket, this);
                clients.add(handler);
                threadPool.execute(handler); // запускаем обработчик в отдельном потоке
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startGameLoop() {
        gameLoopExecutor.scheduleAtFixedRate(() -> {
            if (gameRunning) {
                update();
            }
        }, 0, 1000 / TICK_RATE, TimeUnit.MILLISECONDS);
    }

    private void update() {
        List<Bomb> explodedBombs = new ArrayList<>();
        for (Bomb bomb: bombs) {
            if (bomb.shouldExplode()) {
                explodedBombs.add(bomb);
                explodeBomb(bomb);
            }
        }

        bombs.removeAll(explodedBombs);

        for (Bomb bomb: explodedBombs) {
            for (Player player: players) {
                if (player.getId() == bomb.getOwnerId()) {
                    player.incrementBombs();
                    break;
                }
            }
        }

        checkRespawn();

        explosions.removeIf(Explosion::isFinished);
        wallDestructions.removeIf(WallDestruction::isFinished);


        if (System.currentTimeMillis() - gameStartTime >= GAME_DURATION) {
            endGame();
        }

        broadcastGameState();
    }

    private void respawnPlayer(Player player) {
        Point spawnPos = GameMap.getStartPosition(player.getId());
        player.respawn(spawnPos.x, spawnPos.y);

        System.out.println("Player " + player.getName() + " respawned at (" +
                spawnPos.x + ", " + spawnPos.y + ")");
    }

    private void checkRespawn() {
        long currentTime = System.currentTimeMillis();


        for (Player player: players) {
            if (!player.isAlive() && playerDeathTimes.containsKey(player.getId())) {
                long deathTime = playerDeathTimes.get(player.getId());

                if (currentTime - deathTime >= RESPAWN_DELAY) {
                    respawnPlayer(player);
                    playerDeathTimes.remove(player.getId());
                }
            }
        }
    }

    private void explodeBomb(Bomb bomb) {
        List<Point> explosionTiles = bomb.getExplosionTiles(map);
        explosions.add(new Explosion(explosionTiles));

        for (Point tile: explosionTiles) {
            int tileType = map.getTile(tile.x, tile.y);

            if (tileType == TextureManager.TILE_WALL_DESTRUCTIBLE) {
                wallDestructions.add(new WallDestruction(tile.x, tile.y));
                map.destroyTile(tile.x, tile.y);
                addScore(bomb.getOwnerId(), 10, "destroy_block");
            }
        }

        for (Player player: players) {
            if (!player.isAlive()) continue;

            for (Point tile: explosionTiles) {
                if (player.getGridX() == tile.x && player.getGridY() == tile.y) {
                    player.kill();
                    playerDeathTimes.put(player.getId(), System.currentTimeMillis());


                    if (player.getId() != bomb.getOwnerId()) {
                        addScore(bomb.getOwnerId(), 100, "kill");
                    } else {
                        addScore(player.getId(), -100, "suicide");
                    }


                    broadcast(new ScoreUpdateMessage(player.getName(), player.getScore(), "death"));
                    System.out.println("Player " + player.getName() + " killed by bomb");
                    break;
                }
            }
        }
    }

    private void addScore(int playerId, int score, String reason) {
        for (Player player: players) {
            if (player.getId() == playerId) {
                player.addScore(score);

                break;
            }
        }
    }

    public synchronized void handleMessage(Message message, ClientHandler sender) {
        switch (message.getType()) {
            case PLAYER_JOIN -> handlePlayerJoin((PlayerJoinMessage) message, sender);
            case PLAYER_MOVE -> handlePlayerMove((PlayerMoveMessage) message);
            case PLACE_BOMB -> handlePlaceBomb((PlaceBombMessage) message);
            default -> {}
        }
    }


    private void handlePlayerJoin(PlayerJoinMessage message, ClientHandler clientHandler) {
        if (players.size() >= GameMap.MAX_PLAYERS) {
            System.out.println("Server full, rejecting player: " + message.getPlayerName());
            return;
        }

        int playerId = nextPlayerId++;
        int colorId = playerId % GameMap.MAX_PLAYERS;

        Point pos = GameMap.getStartPosition(playerId);

        Player player = new Player(playerId, message.getPlayerName(), pos.x, pos.y, colorId);
        players.add(player);

        PlayerJoinMessage response = new PlayerJoinMessage(message.getPlayerName(), playerId);
        clientHandler.sendMessage(response);

        System.out.println("Player joined: " + message.getPlayerName() +
                " (ID: " + playerId + ", Color: " + colorId + ")");

        if (players.size() >= 2 && !gameRunning) {
            startGame();
        }
    }

    private void handlePlayerMove(PlayerMoveMessage message) {
        for (Player player: players) {
            if (player.getId() == message.getPlayerId() && player.isAlive()) {
                if (
                        message.getDirection() == PlayerMoveMessage.Direction.UP ||
                        message.getDirection() == PlayerMoveMessage.Direction.DOWN ||
                        message.getDirection() == PlayerMoveMessage.Direction.LEFT ||
                        message.getDirection() == PlayerMoveMessage.Direction.RIGHT
                ) {
                    int dx = 0, dy = 0;
                    Direction dir = Direction.DOWN;

                    switch (message.getDirection()) {
                        case UP -> {
                            dy = -1;
                            dir = Direction.UP;
                        }
                        case DOWN -> {
                            dy = 1;
                            dir = Direction.DOWN;
                        }
                        case LEFT -> {
                            dx = -1;
                            dir = Direction.LEFT;
                        }
                        case RIGHT -> {
                            dx = 1;
                            dir = Direction.RIGHT;
                        }
                    }

                    player.setDirection(dir);
                    player.move(dx, dy, map);
                }
                break;
            } else {
                player.stopMoving();
            }
        }
    }

    private void handlePlaceBomb(PlaceBombMessage message) {
        Player player = null;
        for (Player p: players) {
            if (p.getId() == message.getPlayerId()) {
                player = p;
                break;
            }
        }

        if (player != null && player.isAlive() && player.getBombsAvailable() > 0 ) {
            int gridX = player.getGridX();
            int gridY = player.getGridY();


            if (!map.canPlaceBomb(gridX, gridY)){
                System.out.println("Cannot place bomb at (" + gridX + ";" + gridY + ") - not a floor tile");
            }

            boolean bombExists = false;

            for (Bomb bomb: bombs) {
                if (bomb.getGridX() == player.getGridX() && bomb.getGridY() ==  player.getGridY()) {
                    bombExists = true;
                    break;
                }
            }

            if (!bombExists) {
                Bomb bomb = new Bomb(player.getGridX(), player.getGridY(), player.getId());
                bombs.add(bomb);
                player.decrementBombs();
                System.out.println("Bomb placed at (" + bomb.getGridX() + ", " + bomb.getGridY() + ")");
            }
        }
    }

    private void broadcastGameState() {
        PlayerState[] playerStates = players.stream()
                .map(p -> new PlayerState(p.getId(), p.getName(), p.getX(), p.getY(),
                                    p.getScore(), p.isAlive(), p.getCurrentDirection()))
                .toArray(PlayerState[]::new);

        BombState[] bombStates = bombs.stream()
                .map(b -> new BombState(b.getId(), b.getGridX(), b.getGridY(),
                        b.getPlacedTime(), b.getOwnerId()))
                .toArray(BombState[]::new);

        long gameTime = gameRunning ? (System.currentTimeMillis() - gameStartTime) : 0;

        GameState state = new GameState(playerStates, bombStates, map.getTiles(), gameTime);
        broadcast(new GameStateMessage(state));
    }

    public void broadcast(Message message) {
        for (ClientHandler client: clients) {
            client.sendMessage(message);
        }
    }

    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }


}

