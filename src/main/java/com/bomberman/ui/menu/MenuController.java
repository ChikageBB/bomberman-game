package com.bomberman.ui.menu;

import com.bomberman.client.GameClient;
import com.bomberman.server.GameServer;
import com.bomberman.storage.GameStorage;
import com.bomberman.ui.GameWindow;

public class MenuController {
    private final MainMenuView view;
    private GameStorage.GameSettings settings;


    public MenuController(MainMenuView view) {
        this.view = view;
        this.settings = GameStorage.loadSettings();
    }

    public void saveSettings(String playerName, String serverAddress, int port) {
        settings.playerName = playerName;
        settings.serverAddress = serverAddress;
        settings.serverPort = port;
        GameStorage.saveSettings(settings);
    }

    public GameStorage.GameSettings getSettings() {
        return settings;
    }

    public void hostGame(String playerName, String serverAddress, int port) {
        saveSettings(playerName, serverAddress, port);

        new Thread(() -> {
            GameServer server = new GameServer();
            server.start();
        }).start();

        view.showInfo(
                "Server started on port " + port + "\n" +
                        "Players can now connect to this server."
        );

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        joinGame(playerName, "localhost", port);
    }

    public boolean joinGame(String playerName, String serverAddress, int port) {

        if (playerName == null || playerName.isBlank()) {
            view.showError("Please enter your name");
            return false;
        }

        saveSettings(playerName, serverAddress, port);

        GameClient client = new GameClient(serverAddress, port);
        if (!client.connect(playerName)) {
            view.showError("Failed to connect to server.\nMake sure the server is running.");
            return false;
        }

        GameWindow gameWindow = new GameWindow(client);
        client.setWindow(gameWindow);
        gameWindow.setVisible(true);

        view.close();
        return true;
    }


    public void showStatistics() {
        var topPlayers = GameStorage.getTopPlayers(10);
        view.showStatistics(topPlayers);
    }
}
