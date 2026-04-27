package org.example;

import java.util.Random;

public class Robot extends GameCharacter {
    public Robot(String name, Maze maze, int r, int c) {
        super(name, maze, r, c);
    }

    @Override
    protected void move() {
        Random rand = new Random();
        int dir = rand.nextInt(4);

        if (!maze.grid[r][c].walls[dir]) {
            int nextR = r, nextC = c;
            if (dir == 0) nextR--; else if (dir == 1) nextC++;
            else if (dir == 2) nextR++; else if (dir == 3) nextC--;

            if (nextR >= 0 && nextR < maze.rows && nextC >= 0 && nextC < maze.cols) {
                Cell target = maze.grid[nextR][nextC];
                if (target.occupy()) {
                    maze.grid[r][c].release();
                    this.r = nextR;
                    this.c = nextC;
                    System.out.println("[" + getName() + "] este la (" + r + "," + c + ")");

                    if (maze.isBunnyAt(r, c)) {
                        System.out.println("🤖 " + getName() + " A PRINS IEPURAȘUL!");
                        maze.setGameOver(true);
                    }
                }
            }
        }
    }
}