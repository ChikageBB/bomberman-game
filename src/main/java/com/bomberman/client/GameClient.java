package com.bomberman.client;

import com.bomberman.protocol.*;
import com.bomberman.game.*;
import com.bomberman.ui.GameWindow;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

/**
 * Игровой клиент
 */
public class GameClient {
    private String serverAddress;
    private int serverPort;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean connected;

    private GameState currentState;
    private int myPlayerId = -1;
    private GameWindow window;

    private ExecutorService networkThread;

    public GameClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.connected = false;
        this.networkThread = Executors.newSingleThreadExecutor();
    }

    public boolean connect(String playerName) {
        try {
            socket = new Socket(serverAddress, serverPort);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            connected = true;

            System.out.println("Connected to server: " + serverAddress + ":" + serverPort);

            // Отправляем подключение (assignedId = -1, сервер назначит)
            sendMessage(new PlayerJoinMessage(playerName, -1));

            // Запускаем поток приёма
            networkThread.execute(this::receiveMessages);

            return true;
        } catch (IOException e) {
            System.err.println("Failed to connect: " + e.getMessage());
            return false;
        }
    }

    private void receiveMessages() {
        try {
            while (connected) {
                Message message = (Message) in.readObject();
                handleMessage(message);
            }
        } catch (IOException | ClassNotFoundException e) {
            if (connected) {
                System.err.println("Connection lost: " + e.getMessage());
                disconnect();
            }
        }
    }

    private void handleMessage(Message message) {
        switch (message.getType()) {
            case PLAYER_JOIN:
                PlayerJoinMessage joinMsg = (PlayerJoinMessage) message;
                myPlayerId = joinMsg.getAssignedId();
                System.out.println("Assigned player ID: " + myPlayerId);
                break;

            case GAME_STATE:
                GameStateMessage stateMsg = (GameStateMessage) message;
                currentState = stateMsg.getGameState();
                if (window != null) {
                    window.updateGameState(currentState);
                }
                break;

            case GAME_START:
                GameStartMessage startMsg = (GameStartMessage) message;
                System.out.println("Game started! Duration: " +
                        startMsg.getGameDuration() / 1000 + " seconds");
                break;

            case GAME_OVER:
                GameOverMessage overMsg = (GameOverMessage) message;
                if (window != null) {
                    window.showGameOver(overMsg.getWinnerName(), overMsg.getFinalScore());
                }
                break;

            case SCORE_UPDATE:
                ScoreUpdateMessage scoreMsg = (ScoreUpdateMessage) message;
                System.out.println("Score update: " + scoreMsg.getName() +
                        " - " + scoreMsg.getScore() + " (" + scoreMsg.getReason() + ")");
                break;

            default:
                break;
        }
    }

    public void sendMessage(Message message) {
        if (!connected) return;

        try {
            out.writeObject(message);
            out.reset();
            out.flush();
        } catch (IOException e) {
            System.err.println("Failed to send message: " + e.getMessage());
        }
    }

    public void sendMove(PlayerMoveMessage.Direction direction) {
        if (myPlayerId == -1) return;

        Player myPlayer = getMyPlayer();
        if (myPlayer != null) {
            sendMessage(new PlayerMoveMessage(myPlayerId, myPlayer.getX(),
                    myPlayer.getY(), direction));
        }
    }

    public void sendPlaceBomb() {
        if (myPlayerId == -1) return;

        Player myPlayer = getMyPlayer();
        if (myPlayer != null) {
            sendMessage(new PlaceBombMessage(myPlayerId, myPlayer.getGridX(),
                    myPlayer.getGridY()));
        }
    }

    public Player getMyPlayer() {
        if (currentState == null) return null;

        for (PlayerState ps : currentState.getPlayerStates()) {
            if (ps.getId() == myPlayerId) {
                int colorId = ps.getId() % GameMap.MAX_PLAYERS;
                Player p = new Player(ps.getId(), ps.getName(),
                        ps.getX() / GameMap.TILE_SIZE,
                        ps.getY() / GameMap.TILE_SIZE,
                        colorId);
                p.setX(ps.getX());
                p.setY(ps.getY());
                p.setScore(ps.getScore());
                p.setAlive(ps.isAlive());
                return p;
            }
        }
        return null;
    }

    public void disconnect() {
        connected = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        networkThread.shutdown();
    }

    public void setWindow(GameWindow window) {
        this.window = window;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public int getMyPlayerId() {
        return myPlayerId;
    }

    public boolean isConnected() {
        return connected;
    }
}