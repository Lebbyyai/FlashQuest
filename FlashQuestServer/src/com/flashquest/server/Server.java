package com.flashquest.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class Server {

    private static final int PORT = 59001;
    private static ExecutorService threadPool = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        System.out.println("Starting FlashQuest Server...");
        
       AIGenerator.loadApiKey();
        
        try (ServerSocket listener = new ServerSocket(PORT)) {
            System.out.println("✅ Server is running. Waiting for clients to connect on port " + PORT + "...");

            while (true) {
                Socket clientSocket = listener.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                
                ClientHandler handler = new ClientHandler(clientSocket);
                threadPool.submit(handler);
            }
            
        } catch (IOException e) {
            System.err.println("Server error: Could not listen on port " + PORT);
            e.printStackTrace();
        }
    }
}