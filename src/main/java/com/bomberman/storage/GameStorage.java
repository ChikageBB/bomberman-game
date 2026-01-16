package com.bomberman.storage;

import org.json.simple.*;
import org.json.simple.parser.*;
import java.io.*;
import java.util.*;


public class GameStorage {
    private static final String SETTINGS_FILE = "settings.json";
    private static final String STATS_FILE = "player_stats.json";

    public static class GameSettings {
        public String serverAddress = "localhost";
        public int serverPort = 8888;
        public String playerName = "Player";

        public GameSettings() {}

        @SuppressWarnings("unchecked")
        public JSONObject toJSON() {
            JSONObject json = new JSONObject();
            json.put("serverAddress", serverAddress);
            json.put("serverPort", serverPort);
            json.put("playerName", playerName);
            return json;
        }

        public static GameSettings fromJSON(JSONObject json) {
            GameSettings settings = new GameSettings();
            if (json.containsKey("serverAddress")) {
                settings.serverAddress = (String) json.get("serverAddress");
            }
            if (json.containsKey("serverPort")) {
                settings.serverPort = ((Long) json.get("serverPort")).intValue();
            }
            if (json.containsKey("playerName")) {
                settings.playerName = (String) json.get("playerName");
            }
            return settings;
        }
    }

    public static class PlayerStats {
        public String name;
        public int gamesPlayed = 0;
        public int gamesWon = 0;
        public int totalScore = 0;
        public int highestScore = 0;

        public PlayerStats(String name) {
            this.name = name;
        }

        @SuppressWarnings("unchecked")
        public JSONObject toJSON() {
            JSONObject json = new JSONObject();
            json.put("name", name);
            json.put("gamesPlayed", gamesPlayed);
            json.put("gamesWon", gamesWon);
            json.put("totalScore", totalScore);
            json.put("highestScore", highestScore);
            return json;
        }

        public static PlayerStats fromJSON(JSONObject json) {
            String name = (String) json.get("name");
            PlayerStats stats = new PlayerStats(name);
            stats.gamesPlayed = ((Long) json.get("gamesPlayed")).intValue();
            stats.gamesWon = ((Long) json.get("gamesWon")).intValue();
            stats.totalScore = ((Long) json.get("totalScore")).intValue();
            stats.highestScore = ((Long) json.get("highestScore")).intValue();
            return stats;
        }
    }

    public static void saveSettings(GameSettings settings) {
        try (FileWriter file = new FileWriter(SETTINGS_FILE)) {
            file.write(settings.toJSON().toJSONString());
            file.flush();
            System.out.println("Settings saved to " + SETTINGS_FILE);
        } catch (IOException e) {
            System.err.println("Failed to save settings: " + e.getMessage());
        }
    }

    public static GameSettings loadSettings() {
        File file = new File(SETTINGS_FILE);
        if (!file.exists()) {
            System.out.println("Settings file not found, using defaults");
            return new GameSettings();
        }

        try (FileReader reader = new FileReader(file)) {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(reader);
            System.out.println("Settings loaded from " + SETTINGS_FILE);
            return GameSettings.fromJSON(json);
        } catch (IOException | ParseException e) {
            System.err.println("Failed to load settings: " + e.getMessage());
            return new GameSettings();
        }
    }

    @SuppressWarnings("unchecked")
    public static void savePlayerStats(Map<String, PlayerStats> statsMap) {
        JSONArray array = new JSONArray();
        for (PlayerStats stats : statsMap.values()) {
            array.add(stats.toJSON());
        }

        try (FileWriter file = new FileWriter(STATS_FILE)) {
            file.write(array.toJSONString());
            file.flush();
            System.out.println("Player stats saved to " + STATS_FILE);
        } catch (IOException e) {
            System.err.println("Failed to save player stats: " + e.getMessage());
        }
    }

    public static Map<String, PlayerStats> loadPlayerStats() {
        Map<String, PlayerStats> statsMap = new HashMap<>();
        File file = new File(STATS_FILE);

        if (!file.exists()) {
            System.out.println("Player stats file not found, starting fresh");
            return statsMap;
        }

        try (FileReader reader = new FileReader(file)) {
            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(reader);

            for (Object obj : array) {
                JSONObject json = (JSONObject) obj;
                PlayerStats stats = PlayerStats.fromJSON(json);
                statsMap.put(stats.name, stats);
            }

            System.out.println("Player stats loaded from " + STATS_FILE);
        } catch (IOException | ParseException e) {
            System.err.println("Failed to load player stats: " + e.getMessage());
        }

        return statsMap;
    }

    public static void updatePlayerStats(String playerName, int score, boolean won) {
        Map<String, PlayerStats> statsMap = loadPlayerStats();

        PlayerStats stats = statsMap.getOrDefault(playerName, new PlayerStats(playerName));
        stats.gamesPlayed++;
        if (won) stats.gamesWon++;
        stats.totalScore += score;
        if (score > stats.highestScore) {
            stats.highestScore = score;
        }

        statsMap.put(playerName, stats);
        savePlayerStats(statsMap);
    }

    public static List<PlayerStats> getTopPlayers(int limit) {
        Map<String, PlayerStats> statsMap = loadPlayerStats();
        List<PlayerStats> statsList = new ArrayList<>(statsMap.values());

        statsList.sort((a, b) -> Integer.compare(b.totalScore, a.totalScore));

        return statsList.subList(0, Math.min(limit, statsList.size()));
    }
}