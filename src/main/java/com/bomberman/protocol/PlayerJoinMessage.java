package com.bomberman.protocol;

public class PlayerJoinMessage extends Message{

    private final String playerName;
    private int assignedId;


    public PlayerJoinMessage(String playerName, int assignedId) {
        super(Type.PLAYER_JOIN);
        this.playerName = playerName;
        this.assignedId = assignedId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getAssignedId() {
        return assignedId;
    }

    public void setAssignedId(int assignedId) {
        this.assignedId = assignedId;
    }
}
