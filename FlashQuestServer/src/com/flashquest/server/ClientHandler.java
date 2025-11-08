package com.flashquest.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import org.mindrot.jbcrypt.BCrypt; 
import java.util.Base64;

public class ClientHandler implements Runnable {

    private Socket socket;
    private Scanner in;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);

            while (in.hasNextLine()) {
                String command = in.nextLine();
                System.out.println("Received from client: " + command);
                handleCommand(command);
            }
        } catch (Exception e) {
            System.err.println("ClientHandler error: " + e.getMessage());
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleCommand(String command) {
        String[] parts = command.split(" ");
        String action = parts[0];

        try {
            switch (action) {
                case "LOGIN": {
                    String email = parts[1];
                    String rawPassword = parts[2];
                    
                    String result = DatabaseManager.validateUser(email, rawPassword);
                    out.println(result);
                    break;
                }
                case "REGISTER": {
                    String username = parts[1];
                    String email = parts[2];
                    String rawPassword = parts[3];

                    String result = DatabaseManager.registerUser(username, email, rawPassword);
                    out.println(result);
                    break;
                }
                case "GET_STATS": {
                    int userId = Integer.parseInt(parts[1]);
                    String result = DatabaseManager.getUserStats(userId);
                    out.println(result);
                    break;
                }
                
                case "GENERATE_QUIZ": {
    // Client sends: GENERATE_QUIZ [topic] [type] [difficulty] [num]
                    String topic = parts[1];
                    String type = parts[2]; // We will now use this
                    String difficulty = parts[3]; 
                 int numQuestions = Integer.parseInt(parts[4]); 
    
    // --- FIX: Pass 'type' to DatabaseManager ---
                    String result = DatabaseManager.getQuizQuestions(topic, numQuestions, difficulty, type); 
                        out.println(result); 
                break;
        }

// REPLACE the "GENERATE_FROM_TEXT" case with this:
                case "GENERATE_FROM_TEXT": {
    // Client sends: GENERATE_FROM_TEXT [topic] [type] [difficulty] [num] [base64_content]
                     String topic = parts[1]; 
                    String type = parts[2]; // We will now use this
                 String difficulty = parts[3];
                 int numQuestions = Integer.parseInt(parts[4]);
                 String base64Content = parts[5];
    
                byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
                String fileText = new String(decodedBytes);
    
    // --- FIX: Pass 'type' to DatabaseManager ---
                 String result = DatabaseManager.getQuizFromText(fileText, numQuestions, difficulty, type);
                out.println(result);
                break;
}
                
                case "SUBMIT_QUIZ": {
                    int userId = Integer.parseInt(parts[1]);
                    int score = Integer.parseInt(parts[2]);
                    
                    String result = DatabaseManager.submitQuizResults(userId, score);
                    out.println(result); 
                    break;
                }
                
                case "CHECK_FOR_QUIZ": {
                    String result = DatabaseManager.checkForSurpriseQuiz();
                    out.println(result);
                    break;
                }
                
                case "GET_LEADERBOARD": {
                    String result = DatabaseManager.getLeaderboardData();
                    out.println(result);
                    break;
                }
                case "REDEEM_PRIZE": {
                    int userId = Integer.parseInt(parts[1]);
                    int cost = Integer.parseInt(parts[2]);
                    String result = DatabaseManager.redeemBadges(userId, cost);
                    out.println(result);
                    break;
                }
                case "GET_ACCOUNT_DATA": {
                    int userId = Integer.parseInt(parts[1]);
                    String result = DatabaseManager.getAccountData(userId);
                    out.println(result);
                    break;
                }
                case "UPDATE_EMAIL": {
                    int userId = Integer.parseInt(parts[1]);
                    String newEmail = parts[2];
                    String result = DatabaseManager.updateUserEmail(userId, newEmail);
                    out.println(result);
                    break;
                }
                case "UPDATE_PASSWORD": {
                    int userId = Integer.parseInt(parts[1]);
                    String newPassword = parts[2];
                    String result = DatabaseManager.updateUserPassword(userId, newPassword);
                    out.println(result);
                    break;
                }

                default:
                    out.println("UNKNOWN_COMMAND");
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error processing command '" + command + "': " + e.getMessage());
            e.printStackTrace(); 
            out.println("FAIL_SERVER_ERROR");
        }
    }
}