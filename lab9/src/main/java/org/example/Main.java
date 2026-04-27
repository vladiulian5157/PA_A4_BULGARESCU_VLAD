package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Maze maze = new Maze(10);
        Bunny bunny = new Bunny(maze, 0, 0);
        Robot r1 = new Robot("Robot-1", maze, 9, 0);
        Robot r2 = new Robot("Robot-2", maze, 0, 9);

        maze.setBunny(bunny);
        bunny.start();
        r1.start();
        r2.start();

        new TimeKeeper(maze, 30).start();

        Scanner sc = new Scanner(System.in);
        while (!maze.isGameOver()) {
            System.out.println("Comenzi: 'slow', 'fast', 'exit'");
            String cmd = sc.nextLine();
            if (cmd.equals("slow")) {
                bunny.setSpeed(1000);
                r1.setSpeed(1000);
                r2.setSpeed(1000);
            } else if (cmd.equals("fast")) {
                bunny.setSpeed(200);
                r1.setSpeed(200);
                r2.setSpeed(200);
            } else if (cmd.equals("exit")) {
                maze.setGameOver(true);
            }
        }
        System.out.println("Program terminat.");
    }
}