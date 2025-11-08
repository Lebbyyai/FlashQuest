package com.flashquest.services;

import com.flashquest.AppController;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.SwingUtilities;
import java.util.Base64; // <-- ADD THIS IMPORT

public class NetworkClient {

    private Socket socket;
    private PrintWriter out;
    private Scanner in;
    private AppController controller;

    public NetworkClient(AppController controller) {
        this.controller = controller;
        try {
            socket = new Socket("localhost", 59001); 
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new Scanner(socket.getInputStream());
            
            new Thread(this::listenForServerResponses).start(); 
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenForServerResponses() {
        try {
            while (in.hasNextLine()) {
                String response = in.nextLine();
                SwingUtilities.invokeLater(() -> handleResponse(response));
            }
        } catch (Exception e) {
            if (!socket.isClosed()) {
                e.printStackTrace();
            }
        }
    }
    
    // (Your handleResponse method is unchanged)
    private void handleResponse(String response) {
        // Don't log "NO_SURPRISE_QUIZ" to keep the console clean
        if (!response.equals("NO_SURPRISE_QUIZ")) {
            System.out.println("Server response: " + response);
        }
        
        if (response.startsWith("LOGIN_SUCCESS")) {
            controller.handleLoginSuccess(response.substring("LOGIN_SUCCESS ".length()));
        } else if (response.equals("LOGIN_FAIL_PASSWORD")) {
            controller.handleLoginFailure("Incorrect password.");
        } else if (response.equals("LOGIN_FAIL_EMAIL")) {
            controller.handleLoginFailure("Email or username not found.");
        } else if (response.equals("LOGIN_FAIL_DB_ERROR")) {
            controller.handleLoginFailure("Database error.");
        } else if (response.equals("REGISTER_SUCCESS")) {
            controller.handleRegistrationSuccess();
        } else if (response.equals("REGISTER_FAIL")) {
            controller.handleRegistrationFailure("Email already in use.");
        } else if (response.equals("FAIL_DB_ERROR")) {
            controller.handleRegistrationFailure("A database error occurred.");
            
        } else if (response.startsWith("STATS_SUCCESS")) {
            controller.handleStatsResponse(response.substring("STATS_SUCCESS ".length()));
            
        } else if (response.startsWith("LEADERBOARD_DATA")) {
            controller.handleLeaderboardData(response.substring("LEADERBOARD_DATA ".length()));
        } else if (response.equals("LEADERBOARD_FAIL")) {
            controller.handleLeaderboardData(null);
            
        } else if (response.equals("REDEEM_SUCCESS")) {
            controller.handleRedeemResponse("REDEEM_SUCCESS");
        } else if (response.equals("REDEEM_FAIL_FUNDS")) {
            controller.handleRedeemResponse("REDEEM_FAIL_FUNDS");
        } else if (response.equals("REDEEM_FAIL_DB") || response.equals("REDEEM_FAIL_USER")) {
            controller.handleRedeemResponse("REDEEM_FAIL_DB");

        } else if (response.startsWith("ACCOUNT_DATA_SUCCESS")) {
            controller.handleAccountData(response.substring("ACCOUNT_DATA_SUCCESS ".length()));
        } else if (response.startsWith("ACCOUNT_DATA_FAIL")) {
            controller.handleAccountData(null);
            
        } else if (response.equals("UPDATE_EMAIL_SUCCESS")) {
            controller.handleEmailUpdateSuccess();
        } else if (response.equals("UPDATE_EMAIL_FAIL_TAKEN")) {
            controller.handleEmailUpdateFailure("That email is already in use.");
        } else if (response.equals("UPDATE_PASSWORD_SUCCESS")) {
            controller.handlePasswordUpdateSuccess();
            
        } else if (response.startsWith("QUIZ_DATA_SUCCESS")) {
            controller.handleQuizData(response.substring("QUIZ_DATA_SUCCESS ".length()), false);
            
        } else if (response.startsWith("SURPRISE_QUIZ_DATA")) {
            controller.handleQuizData(response.substring("SURPRISE_QUIZ_DATA ".length()), true);
            
        } else if (response.equals("QUIZ_GEN_FAIL")) {
            controller.handleQuizGenFailure("The server failed to generate a quiz for this topic.");
            
        } else if (response.startsWith("QUIZ_RESULTS_SUCCESS")) {
            String newTotal = response.substring("QUIZ_RESULTS_SUCCESS ".length());
            controller.handleQuizResultsSuccess(newTotal);
            
        } else if (response.equals("NO_SURPRISE_QUIZ")) {
            // Do nothing
            
        } else {
            System.err.println("Unknown server response: " + response);
        }
    }
    
    // (All other send methods are unchanged)
    public void sendLoginRequest(String emailOrUsername, String password) {
        if (out != null) out.println("LOGIN " + emailOrUsername + " " + password);
    }
    public void sendRegisterRequest(String username, String email, String password) {
        if (out != null) out.println("REGISTER " + username + " " + email + " " + password);
    }
    public void requestStats(int userId) { 
        if (out != null) out.println("GET_STATS " + userId);
    }
    public void requestLeaderboard() {
        if (out != null) out.println("GET_LEADERBOARD");
    }
    public void sendRedeemRequest(int userId, int cost) {
        if (out != null) out.println("REDEEM_PRIZE " + userId + " " + cost);
    }
    public void requestAccountData(int userId) {
        if (out != null) out.println("GET_ACCOUNT_DATA " + userId);
    }
    public void sendUpdateEmail(int userId, String newEmail) {
        if (out != null) out.println("UPDATE_EMAIL " + userId + " " + newEmail);
    }
    public void sendUpdatePassword(int userId, String newPassword) {
        if (out != null) out.println("UPDATE_PASSWORD " + userId + " " + newPassword);
    }
    public void sendQuizResults(int numCorrect, int userId) {
        if (out != null) {
            out.println("SUBMIT_QUIZ " + userId + " " + numCorrect);
        }
    }
    public void checkForSurpriseQuiz() {
        if (out != null) {
            out.println("CHECK_FOR_QUIZ");
        }
    }

    // --- THIS IS THE NEW OVERLOADED METHOD ---
    public void sendQuizRequest(String topic, String type, String difficulty, int numQuestions, String fileContent) {
        if (out != null) {
            // Encode the file content to Base64 to safely send it
            String encodedContent = Base64.getEncoder().encodeToString(fileContent.getBytes());
            
            String formattedTopic = topic.replace(" ", "-");
            String formattedType = type.replace(" ", "-");
            
            // Send a new command with the Base64 text at the end
            out.println("GENERATE_FROM_TEXT " + formattedTopic + " " + formattedType + " " + difficulty + " " + numQuestions + " " + encodedContent);
        }
    }
    
    // --- THIS IS YOUR ORIGINAL, UPDATED METHOD ---
    public void sendQuizRequest(String topic, String type, String difficulty, int numQuestions) {
        if (out != null) {
            String formattedTopic = topic.replace(" ", "-");
            String formattedType = type.replace(" ", "-");
            // Send the command "GENERATE_QUIZ" (no file content)
            out.println("GENERATE_QUIZ " + formattedTopic + " " + formattedType + " " + difficulty + " " + numQuestions);
        }
    }

    public void disconnect() {
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}