package org.example;

public class Maze {
    public int rows, cols;
    public Cell[][] grid;
    private volatile boolean gameOver = false;
    private Bunny bunny;

    public Maze(int size) {
        this.rows = size;
        this.cols = size;
        this.grid = new Cell[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) grid[i][j] = new Cell(i, j);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i < rows - 1) grid[i][j].walls[2] = grid[i+1][j].walls[0] = false;
                if (j < cols - 1) grid[i][j].walls[1] = grid[i][j+1].walls[3] = false;
            }
        }
    }

    public void setBunny(Bunny b) { this.bunny = b; }
    public boolean isBunnyAt(int r, int c) { return bunny.r == r && bunny.c == c; }
    public synchronized boolean isGameOver() { return gameOver; }
    public synchronized void setGameOver(boolean state) { this.gameOver = state; }
}