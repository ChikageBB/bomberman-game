package com.bomberman.game;

import com.bomberman.client.Direction;

public class Player {
    private int id;
    private String name;
    private int x, y; // пиксели
    private int gridX, gridY; // сетка
    private int score;
    private boolean alive;
    private int colorId; // 0=white, 1=red, 2=black, 3=blue
    private int speed = 2;
    private int bombsAvailable = 1;

    private Direction currentDirection = Direction.DOWN;
    private long lastStepTime = 0;
    private int walkFrame = 1; // 1, 2, 3
    private boolean isMoving = false;
    private static final long WALK_ANIMATION_SPEED = 150; // мс между кадрами

    private static final int ALIGNMENT_THRESHOLD = 8;

    public Player(int id, String name, int gridX, int gridY, int colorId) {
        this.id = id;
        this.name = name;
        this.gridX = gridX;
        this.gridY = gridY;
        this.x = gridX * GameMap.TILE_SIZE;
        this.y = gridY * GameMap.TILE_SIZE;
        this.score = 0;
        this.alive = true;
        this.colorId = colorId;
    }

    public void move(int dx, int dy, GameMap map) {
        isMoving = true;

        if (dx < 0) currentDirection = Direction.LEFT;
        else if (dx > 0) currentDirection = Direction.RIGHT;
        else if (dy < 0) currentDirection = Direction.UP;
        else if (dy > 0) currentDirection = Direction.DOWN;

        int newX = x;
        int newY = y;

        if (dx != 0) {
            newX = x + dx * speed;

            int centerY = y + GameMap.TILE_SIZE / 2;
            int targetGridY = centerY / GameMap.TILE_SIZE;
            int alignedY = targetGridY * GameMap.TILE_SIZE;
            int offsetY = y - alignedY;

            if (Math.abs(offsetY) <= ALIGNMENT_THRESHOLD) {
                newY = alignedY;
            }
        }

        if (dy != 0) {
            newY = y + dy * speed;

            // Автовыравнивание по горизонтали при вертикальном движении
            int centerX = x + GameMap.TILE_SIZE / 2;
            int targetGridX = centerX / GameMap.TILE_SIZE;
            int alignedX = targetGridX * GameMap.TILE_SIZE;
            int offsetX = x - alignedX;

            if (Math.abs(offsetX) <= ALIGNMENT_THRESHOLD) {
                newX = alignedX;
            }
        }

        if (!map.isCollision(newX, newY, GameMap.TILE_SIZE, GameMap.TILE_SIZE)) {
            x = newX;
            y = newY;


            int centerX = x + GameMap.TILE_SIZE / 2;
            int centerY = y + GameMap.TILE_SIZE / 2;
            gridX = centerX / GameMap.TILE_SIZE;
            gridY = centerY / GameMap.TILE_SIZE;

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastStepTime > WALK_ANIMATION_SPEED) {
                if (walkFrame == 1) walkFrame = 2;
                else if (walkFrame == 2) walkFrame = 3;
                else walkFrame = 2;
                lastStepTime = currentTime;
            }
        }
    }

    public void stopMoving() {
        isMoving = false;
        walkFrame = 1; // Idle
    }

    public int getAnimationFrame() {
        return isMoving ? walkFrame : 1;
    }

    public void addScore(int points) {
        score += points;
    }

    public void kill() {
        alive = false;
    }

    public void respawn(int gridX, int gridY) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.x = gridX * GameMap.TILE_SIZE;
        this.y = gridY * GameMap.TILE_SIZE;
        this.alive = true;
        this.walkFrame = 1;
        this.isMoving = false;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getGridX() { return gridX; }
    public int getGridY() { return gridY; }
    public int getScore() { return score; }
    public boolean isAlive() { return alive; }
    public int getColorId() { return colorId; }
    public int getBombsAvailable() { return bombsAvailable; }
    public Direction getCurrentDirection() { return currentDirection; }
    public boolean isMoving() { return isMoving; }

    public void setX(int x) {
        this.x = x;
        this.gridX = x / GameMap.TILE_SIZE;
    }

    public void setY(int y) {
        this.y = y;
        this.gridY = y / GameMap.TILE_SIZE;
    }

    public void setScore(int score) { this.score = score; }
    public void setAlive(boolean alive) { this.alive = alive; }
    public void setDirection(Direction direction) { this.currentDirection = direction; }
    public void setMoving(boolean moving) { this.isMoving = moving; }
    public void decrementBombs() { if (bombsAvailable > 0) bombsAvailable--; }
    public void incrementBombs() { bombsAvailable++; }

}
