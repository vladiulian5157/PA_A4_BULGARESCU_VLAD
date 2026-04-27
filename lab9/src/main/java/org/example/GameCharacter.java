package org.example;

public abstract class GameCharacter extends Thread {
    protected int r, c;
    protected final Maze maze;
    protected volatile boolean running = true;
    protected volatile long speed = 500;

    public GameCharacter(String name, Maze maze, int startR, int startC) {
        super(name);
        this.maze = maze;
        this.r = startR;
        this.c = startC;
    }

    public void setSpeed(long speed) { this.speed = speed; }
    public void stopCharacter() { this.running = false; }

    @Override
    public void run() {
        while (running && !maze.isGameOver()) {
            move();
            try {
                Thread.sleep(speed);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    protected abstract void move();
}