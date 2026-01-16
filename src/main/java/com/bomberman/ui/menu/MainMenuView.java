package com.bomberman.ui.menu;

import com.bomberman.storage.GameStorage;

import java.util.List;

public interface MainMenuView {
    void showError(String message);
    void showInfo(String message);
    void showStatistics(List<GameStorage.PlayerStats> stats);
    void close();
}
