package com.bomberman.ui;

import javax.swing.*;
import java.awt.*;

public class PlayerStatsCard extends JPanel {
    public PlayerStatsCard(int rank, String name, int score, Color playerColor, boolean alive) {
        setLayout(new BorderLayout(5, 5));
        setBackground(new Color(50, 50, 50));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(alive ? playerColor : java.awt.Color.GRAY, 2),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        setMaximumSize(new Dimension(180, 80));

        JPanel rankPanel = new JPanel();
        rankPanel.setBackground(new Color(50, 50, 50));
        JLabel rankLabel = new JLabel("#" + rank);
        rankLabel.setFont(new Font("Arial", Font.BOLD, 24));
        rankLabel.setForeground(getRankColor(rank));
        rankPanel.add(rankLabel);
        add(rankPanel, BorderLayout.WEST);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(50, 50, 50));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(alive ? java.awt.Color.WHITE : java.awt.Color.GRAY);

        JLabel scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        scoreLabel.setForeground(alive ? java.awt.Color.LIGHT_GRAY : java.awt.Color.DARK_GRAY);

        JLabel statusLabel = new JLabel(alive ? "ALIVE" : "DEAD");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 10));
        statusLabel.setForeground(alive ? new Color(0, 255, 0) : java.awt.Color.RED);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        infoPanel.add(scoreLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        infoPanel.add(statusLabel);

        add(infoPanel, BorderLayout.CENTER);

        JPanel colorPanel = new JPanel();
        colorPanel.setBackground(playerColor);
        colorPanel.setPreferredSize(new Dimension(10, 60));
        add(colorPanel, BorderLayout.EAST);
    }

    private Color getRankColor(int rank) {
        switch (rank) {
            case 1: return new Color(255, 215, 0);
            case 2: return new Color(192, 192, 192);
            case 3: return new Color(205, 127, 50);
            default: return java.awt.Color.WHITE;
        }
    }
}
