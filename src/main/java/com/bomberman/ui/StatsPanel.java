package com.bomberman.ui;

import com.bomberman.game.GameMap;
import com.bomberman.protocol.GameState;
import com.bomberman.protocol.PlayerState;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatsPanel extends JPanel {
    private List<PlayerStatsCard> playerCards;
    private JLabel timerLabel;

    public StatsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(30, 30, 30));
        setPreferredSize(new Dimension(200, 0));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        playerCards = new ArrayList<>();

        JLabel titleLabel = new JLabel("PLAYERS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titleLabel);
        add(Box.createRigidArea(new Dimension(0, 10)));

        // ТАЙМЕР
        timerLabel = new JLabel("03:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        timerLabel.setForeground(new Color(100, 200, 100));
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(timerLabel);
        add(Box.createRigidArea(new Dimension(0, 15)));
    }

    public void updateStats(GameState state) {
        // Обновляем таймер
        long remainingTime = 180000 - state.getGameTime();
        if (remainingTime < 0) remainingTime = 0;

        int seconds = (int) (remainingTime / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;

        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));

        // Меняем цвет таймера
        if (remainingTime < 30000) {
            timerLabel.setForeground(new Color(255, 0, 0));
        } else if (remainingTime < 60000) {
            timerLabel.setForeground(new Color(255, 165, 0));
        } else {
            timerLabel.setForeground(new Color(100, 200, 100));
        }

        // Удаляем старые карточки игроков (оставляем заголовок и таймер)
        while (getComponentCount() > 4) {
            remove(getComponentCount() - 1);
        }
        playerCards.clear();

        PlayerState[] players = state.getPlayerStates();
        Arrays.sort(players, (a, b) -> Integer.compare(b.getScore(), a.getScore()));

        Color[] colors = {Color.WHITE, Color.RED, Color.BLACK, Color.BLUE};

        for (int i = 0; i < players.length; i++) {
            PlayerState player = players[i];
            int colorId = player.getId() % GameMap.MAX_PLAYERS;
            PlayerStatsCard card = new PlayerStatsCard(
                    i + 1,
                    player.getName(),
                    player.getScore(),
                    colors[colorId],
                    player.isAlive()
            );
            playerCards.add(card);
            add(card);
            add(Box.createRigidArea(new Dimension(0, 10)));
        }

        revalidate();
        repaint();
    }
}