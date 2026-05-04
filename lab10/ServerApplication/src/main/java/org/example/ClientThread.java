package org.example;

import java.io.*;
import java.net.*;
import java.util.List;

public class ClientThread implements Runnable {
    private final Socket socket;
    private final GameServer server;
    private final List<Question> questions;

    public ClientThread(Socket socket, GameServer server, List<Question> questions) {
        this.socket = socket;
        this.server = server;
        this.questions = questions;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println("Welcome! Type 'start' to play or 'stop' to shut down server.");

            String request;
            while ((request = in.readLine()) != null) {
                if (request.equalsIgnoreCase("stop")) {
                    out.println("Server stopped");
                    server.stop();
                    break;
                } else if (request.equalsIgnoreCase("start")) {
                    playGame(in, out);
                } else {
                    out.println("Server received: " + request);
                }
            }
        } catch (IOException e) {
            System.out.println("Player disconnected.");
        }
    }

    private void playGame(BufferedReader in, PrintWriter out) throws IOException {
        int score = 0;
        long totalTime = 0;

        for (Question q : questions) {
            out.println("QUESTION: " + q.text + " (" + q.timeLimit + "s)");
            long startTime = System.currentTimeMillis();

            String answer = in.readLine();
            long endTime = System.currentTimeMillis();
            long taken = (endTime - startTime) / 1000;

            if (taken <= q.timeLimit && q.answer.equalsIgnoreCase(answer)) {
                score++;
                totalTime += (endTime - startTime);
                out.println("CORRECT!");
            } else {
                out.println("WRONG or TOO SLOW! Answer: " + q.answer);
            }
        }
        out.println("GAME OVER. Score: " + score + " | Time: " + (totalTime / 1000.0) + "s");
    }
}