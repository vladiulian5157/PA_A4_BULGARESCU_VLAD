package org.example;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Random;
@Getter
public class Bunny extends GameCharacter {
    public Bunny(Maze maze, int r, int c) {
        super("Bunny", maze, r, c);
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
                this.r = nextR;
                this.c = nextC;
                System.out.println("[BUNNY] s-a mutat la (" + r + "," + c + ")");

                if (r == maze.rows - 1 && c == maze.cols - 1) {
                    System.out.println("🐰 IEPRUAȘUL A EVADAT!");
                    maze.setGameOver(true);
                }
            }
        }
    }
}