package org.example;

import java.io.*;
import java.net.*;

public class GameClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8081);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to Server. Type 'exit' to quit.");

            Thread listener = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        System.out.println("\n[SERVER]: " + msg);
                        System.out.print("> ");
                    }
                } catch (IOException e) {
                    System.out.println("Connection lost.");
                }
            });
            listener.start();

            String input;
            while ((input = console.readLine()) != null) {
                if (input.equalsIgnoreCase("exit")) break;
                out.println(input);
            }

        } catch (IOException e) {
            System.err.println("Could not connect to server.");
        }
    }
}