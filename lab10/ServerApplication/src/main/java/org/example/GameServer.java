package org.example;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class GameServer {
    private final int port;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private boolean isRunning = true;
    private ServerSocket serverSocket;
    private final List<Question> questions = new ArrayList<>();

    public GameServer(int port) {
        this.port = port;
        loadQuestions();
    }

    private void loadQuestions() {
        File file = new File("questions.txt");

        if (!file.exists()) {
            System.err.println("Eroare: Fișierul questions.txt nu a fost găsit în directorul: " + file.getAbsolutePath());
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split("\\|");

                if (parts.length == 3) {
                    String prompt = parts[0].trim();
                    String answer = parts[1].trim();
                    int timeLimit = Integer.parseInt(parts[2].trim());

                    questions.add(new Question(prompt, answer, timeLimit));
                }
            }
            System.out.println("S-au încărcat " + questions.size() + " întrebări din questions.txt");
        } catch (IOException e) {
            System.err.println("Eroare la citirea fișierului: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Eroare: Formatul timpului în questions.txt este invalid.");
        }
    }
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Quiz Server started on port " + port);

            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    threadPool.execute(new ClientThread(clientSocket, this, questions));
                } catch (IOException e) {
                    if (isRunning) System.err.println("Server socket closed.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    public synchronized void stop() {
        isRunning = false;
        try {
            if (serverSocket != null) serverSocket.close();
            threadPool.shutdownNow();
            System.out.println("Server shut down gracefully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new GameServer(8081).start();
    }
}