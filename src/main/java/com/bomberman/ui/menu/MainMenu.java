package com.bomberman.ui.menu;

import com.bomberman.storage.GameStorage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class MainMenu extends JFrame implements MainMenuView {

    private final MenuController controller;

    private JTextField nameField;
    private JTextField serverField;
    private JTextField portField;
    private JButton playButton;
    private JButton hostButton;
    private JButton statsButton;
    private JButton exitButton;

    public MainMenu() {
        this.controller = new MenuController(this);

        setupWindow();
        buildUI();
        loadSavedSettings();

        pack();
        setLocationRelativeTo(null);
    }

    private void setupWindow() {
        setTitle("Bomberman - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    private void buildUI() {
        JPanel mainPanel = createMainPanel();
        add(mainPanel);
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(40, 40, 40));

        panel.add(createTitleLabel());
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(createSettingsPanel());
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(createButtonsPanel());

        return panel;
    }

    private JLabel createTitleLabel() {
        JLabel label = new JLabel("BOMBERMAN");
        label.setFont(new Font("Arial", Font.BOLD, 36));
        label.setForeground(Color.ORANGE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBackground(new Color(40, 40, 40));
        panel.setMaximumSize(new Dimension(400, 100));

        nameField = new JTextField(20);
        serverField = new JTextField(20);
        portField = new JTextField(20);

        panel.add(createLabel("Player Name:"));
        panel.add(nameField);
        panel.add(createLabel("Server Address:"));
        panel.add(serverField);
        panel.add(createLabel("Port:"));
        panel.add(portField);

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        return label;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(40, 40, 40));

        playButton = createStyledButton("JOIN GAME");
        hostButton = createStyledButton("HOST GAME");
        statsButton = createStyledButton("VIEW STATISTICS");
        exitButton = createStyledButton("EXIT");

        playButton.addActionListener(e -> handleJoinGame());
        hostButton.addActionListener(e -> handleHostGame());
        statsButton.addActionListener(e -> controller.showStatistics());
        exitButton.addActionListener(e -> System.exit(0));

        panel.add(playButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(hostButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(statsButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(exitButton);

        return panel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setSize(new Dimension(300, 40));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(100, 160, 210));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(70, 130, 180));
            }
        });

        return button;
    }


    private void loadSavedSettings() {
        var settings = controller.getSettings();
        nameField.setText(settings.playerName);
        serverField.setText(settings.serverAddress);
        portField.setText(String.valueOf(settings.serverPort));
    }


    private void handleJoinGame() {
        int port = parsePort();
        if (port == -1) return;

        controller.joinGame(
                nameField.getText().trim(),
                serverField.getText().trim(),
                port
        );
    }

    private void handleHostGame() {
        int port = parsePort();
        if (port == -1) return;

        controller.hostGame(
                nameField.getText().trim(),
                serverField.getText().trim(),
                port
        );
    }

    private int parsePort() {
        try {
            return Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Invalid port number");
            return -1;
        }
    }


    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    @Override
    public void showInfo(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Information",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public void showStatistics(List<GameStorage.PlayerStats> stats) {
        JTextArea textArea = new JTextArea(formatStatistics(stats));
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Player Statistics",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private String formatStatistics(List<GameStorage.PlayerStats> stats){
        StringBuilder sb = new StringBuilder();
        sb.append("TOP PLAYERS:\n\n");

        if (stats.isEmpty()) {
            sb.append("No statistics available yet.");
        } else {
            sb.append(String.format("%-20s %10s %10s %10s\n",
                    "Name", "Games", "Wins", "Total Score"));
            sb.append("─".repeat(60)).append("\n");

            for (GameStorage.PlayerStats stat : stats) {
                sb.append(String.format("%-20s %10d %10d %10d\n",
                        stat.name,
                        stat.gamesPlayed,
                        stat.gamesWon,
                        stat.totalScore));
            }
        }

        return sb.toString();
    }

    @Override
    public void close() {
        dispose();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        SwingUtilities.invokeLater(() -> {
            MainMenu menu = new MainMenu();
            menu.setVisible(true);
        });
    }
}
