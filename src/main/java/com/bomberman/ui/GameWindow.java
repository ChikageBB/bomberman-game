package com.bomberman.ui;

import com.bomberman.client.*;
import com.bomberman.game.*;
import com.bomberman.protocol.*;
import com.bomberman.ui.input.InputHandler;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GameWindow extends JFrame {
    private final GameClient client;
    private final GamePanel gamePanel;
    private final StatsPanel statsPanel;
    private final InputHandler inputHandler;
    private final Timer gameLoopTimer;


    public GameWindow(GameClient client) {
        this.client = client;
        this.gamePanel = new GamePanel(client);
        this.statsPanel = new StatsPanel();
        this.inputHandler = new InputHandler(client);
        this.gameLoopTimer = createGameLoop();

        setupWindow();
        setupLayout();
        setupInput();
        startGameLoop();
    }
    private void setupWindow() {
        setTitle("Bomberman - Multiplayer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(gamePanel, BorderLayout.CENTER);
        add(statsPanel, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
    }

    private void setupInput() {
        addKeyListener(inputHandler);
    }

    private Timer createGameLoop() {
        return new Timer(16, e -> inputHandler.processMovement());
    }

    private void startGameLoop() {
        gameLoopTimer.start();
    }

    public void updateGameState(GameState state) {
        gamePanel.setGameState(state);
        statsPanel.updateStats(state);
        gamePanel.repaint();
    }

    public void showGameOver(String winnerName, int finalScore) {
        gameLoopTimer.stop();

        JOptionPane.showMessageDialog(
                this,
                "Winner: " + winnerName + "\nScore: " + finalScore,
                "Game Over",
                JOptionPane.INFORMATION_MESSAGE
        );

        cleanUp();

        if (client != null) {
            client.disconnect();
        }

        System.exit(0);
    }


    public void cleanUp() {
        gameLoopTimer.stop();
        dispose();
    }

}


