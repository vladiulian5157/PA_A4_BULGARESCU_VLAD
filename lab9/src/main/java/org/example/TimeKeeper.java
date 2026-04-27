package org.example;

public class TimeKeeper extends Thread {
    private final Maze maze;
    private final long timeLimitMs;
    private final long startTime;

    public TimeKeeper(Maze maze, int seconds) {
        this.maze = maze;
        this.timeLimitMs = seconds * 1000L;
        this.startTime = System.currentTimeMillis();
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (!maze.isGameOver()) {
            long elapsed = System.currentTimeMillis() - startTime;
            System.out.println("[TIME] " + (elapsed / 1000) + " secunde trecute...");

            if (elapsed >= timeLimitMs) {
                System.out.println("⌛ TIMP EXPIRAT! Jocul s-a oprit.");
                maze.setGameOver(true);
                break;
            }
            try { Thread.sleep(2000); } catch (InterruptedException e) {}
        }
    }
}