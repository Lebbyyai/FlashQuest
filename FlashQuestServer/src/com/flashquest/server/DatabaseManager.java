package com.flashquest.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Random;

public class DatabaseManager {

    private static Random random = new Random();
    
    private static long lastSurpriseQuizTime = 0L; 
    private static final long QUIZ_COOLDOWN = 2 * 60 * 1000; // 2 minutes

    private static Connection getConnection() throws SQLException {
        String dbUrl = "jdbc:mysql://localhost:3306/flashquest"; 
        String user = "root"; 
        String password = ""; 
        return DriverManager.getConnection(dbUrl, user, password);
    }

    // --- REAL: Checks if user email exists ---
    private static boolean userExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?"; 
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // true if user exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true; // Failsafe
        }
    }

    // --- REAL: Registers a new user ---
    public static String registerUser(String username, String email, String plainTextPassword) {
        if (userExists(email)) {
            System.err.println("Registration failed: Email already in use.");
            return "REGISTER_FAIL";
        }
        String hashedPassword = BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
        
        String sql = "INSERT INTO users (username, email, password_hash, display_name, is_active, total_score, quizzes_completed, badges, high_score) VALUES (?, ?, ?, ?, 1, 0, 0, 0, 0)"; 
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, hashedPassword); 
            pstmt.setString(4, username); // Sets display_name to username
            int rowsAffected = pstmt.executeUpdate();
            
            return (rowsAffected > 0) ? "REGISTER_SUCCESS" : "FAIL_DB_ERROR"; 
        } catch (SQLException e) {
            System.err.println("SQL Error during registration: " + e.getMessage());
            e.printStackTrace();
            return "FAIL_DB_ERROR"; 
        }
    }
    
    // --- REAL: Validates user login ---
    public static String validateUser(String usernameOrEmail, String plainTextPassword) {
        String sql = "SELECT id, display_name, password_hash FROM users WHERE email = ? OR username = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usernameOrEmail);
            pstmt.setString(2, usernameOrEmail);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    
                    if (BCrypt.checkpw(plainTextPassword, storedHash)) {
                        int userId = rs.getInt("id"); 
                        String displayName = rs.getString("display_name"); 
                        
                        if (displayName == null || displayName.isBlank()) {
                            displayName = "User";
                        }
                        
                        return "LOGIN_SUCCESS " + userId + " " + displayName; 
                    } else {
                        return "LOGIN_FAIL_PASSWORD";
                    }
                } else {
                    return "LOGIN_FAIL_EMAIL";
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error during login: " + e.getMessage());
            e.printStackTrace();
            return "LOGIN_FAIL_DB_ERROR";
        }
    }
    
    // --- REAL: Gets user stats for dashboard ---
    public static String getUserStats(int userId) {
        String sql = "SELECT quizzes_completed, total_score, badges, high_score FROM users WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int quizzes = rs.getInt("quizzes_completed");
                    int totalScore = rs.getInt("total_score");
                    int badges = rs.getInt("badges");
                    int highScore = rs.getInt("high_score");
                    
                    String avgScoreStr;
                    if (quizzes > 0) {
                        double avgPoints = (double)totalScore / quizzes;
                        avgScoreStr = String.format("%.1f", avgPoints);
                    } else {
                        avgScoreStr = "0.0";
                    }
                    
                    return "STATS_SUCCESS " + quizzes + " " + badges + " " + avgScoreStr + " " + highScore;
                } else {
                    return "STATS_FAIL_USER_NOT_FOUND";
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error getting stats: " + e.getMessage());
            e.printStackTrace();
            return "STATS_FAIL_DB_ERROR";
        }
    }

    // --- REAL: Gets leaderboard data ---
    public static String getLeaderboardData() {
        String sql = "SELECT username, total_score, badges FROM users ORDER BY total_score DESC, badges DESC LIMIT 10";
        StringBuilder leaderboardString = new StringBuilder("LEADERBOARD_DATA ");
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            int rank = 1;
            while (rs.next()) {
                String username = rs.getString("username");
                int badges = rs.getInt("badges"); 
                leaderboardString.append(rank).append(",")
                                 .append(username).append(",")
                                 .append(badges).append("|");
                rank++;
            }
            if (rank > 1) {
                leaderboardString.setLength(leaderboardString.length() - 1);
            }
            return leaderboardString.toString();
        } catch (SQLException e) {
            System.err.println("SQL Error getting leaderboard: " + e.getMessage());
            e.printStackTrace();
            return "LEADERBOARD_FAIL";
        }
    }
    
    // --- REAL: Gets account data for settings page ---
    public static String getAccountData(int userId) {
        String sql = "SELECT username, email, total_score, badges FROM users WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String username = rs.getString("username");
                    String email = rs.getString("email");
                    int totalScore = rs.getInt("total_score");
                    int badges = rs.getInt("badges");
                    
                    String dataString = username + "," + email + "," + totalScore + "," + badges;
                    return "ACCOUNT_DATA_SUCCESS " + dataString;
                } else {
                    return "ACCOUNT_DATA_FAIL";
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error getting account data: " + e.getMessage());
            e.printStackTrace();
            return "ACCOUNT_DATA_FAIL";
        }
    }

    // --- REAL: Redeems badges from prize shop ---
    public static String redeemBadges(int userId, int cost) {
        String sql = "UPDATE users SET badges = badges - ? WHERE id = ? AND badges >= ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cost);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, cost);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return "REDEEM_SUCCESS";
            } else {
                return "REDEEM_FAIL_FUNDS"; // Either no funds or user not found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "REDEEM_FAIL_DB";
        }
    }

    // --- REAL: Updates user email ---
    public static String updateUserEmail(int userId, String newEmail) {
        if (userExists(newEmail)) {
            return "UPDATE_EMAIL_FAIL_TAKEN";
        }
        String sql = "UPDATE users SET email = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newEmail);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            return "UPDATE_EMAIL_SUCCESS";
        } catch (SQLException e) {
            e.printStackTrace();
            return "FAIL_DB_ERROR";
        }
    }

    // --- REAL: Updates user password ---
    public static String updateUserPassword(int userId, String newPassword) {
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            return "UPDATE_PASSWORD_SUCCESS";
        } catch (SQLException e) {
            e.printStackTrace();
            return "FAIL_DB_ERROR";
        }
    }

    // --- REAL: Submits quiz results and updates stats ---
    public static String submitQuizResults(int userId, int numCorrect) {
        String selectSql = "SELECT high_score, badges FROM users WHERE id = ?";
        String updateSql = "UPDATE users SET quizzes_completed = quizzes_completed + 1, total_score = total_score + ?, badges = badges + ?, high_score = ? WHERE id = ?";
        
        try (Connection conn = getConnection()) {
            int currentHighScore = 0;
            int currentBadges = 0;
            
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, userId);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        currentHighScore = rs.getInt("high_score");
                        currentBadges = rs.getInt("badges");
                    }
                }
            }
            
            int newHighScore = Math.max(currentHighScore, numCorrect);
            int newTotalBadges = currentBadges + numCorrect;
            
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, numCorrect);
                updateStmt.setInt(2, numCorrect);
                updateStmt.setInt(3, newHighScore);
                updateStmt.setInt(4, userId);
                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected == 0) {
                    return "QUIZ_RESULTS_FAIL"; 
                }
            }
            
            return "QUIZ_RESULTS_SUCCESS " + newTotalBadges; 

        } catch (SQLException e) {
            System.err.println("SQL Error submitting quiz results: " + e.getMessage());
            e.printStackTrace();
            return "QUIZ_RESULTS_FAIL";
        }
    }

    // --- REAL: Gets AI quiz from topic ---
    public static String getQuizQuestions(String topic, int numQuestions, String difficulty, String quizType) {
        System.out.println("No questions found in DB. Calling AI Generator...");
        String aiGeneratedData = AIGenerator.generateQuestions(topic, numQuestions, difficulty, quizType);
        
        if (aiGeneratedData != null) {
            saveQuestionsToDB(aiGeneratedData);
            return "QUIZ_DATA_SUCCESS " + aiGeneratedData;
        } else {
            return "QUIZ_GEN_FAIL"; 
        }
    }
    
    // --- REAL: Gets AI quiz from file text ---
    public static String getQuizFromText(String fileText, int numQuestions, String difficulty, String quizType) {
        System.out.println("File text received. Calling AI Generator...");
        String aiGeneratedData = AIGenerator.generateQuestionsFromText(fileText, numQuestions, difficulty, quizType);
        
        if (aiGeneratedData != null) {
            saveQuestionsToDB(aiGeneratedData);
            return "QUIZ_DATA_SUCCESS " + aiGeneratedData;
        } else {
            return "QUIZ_GEN_FAIL";
        }
    }
    
    

public static String checkForSurpriseQuiz() {
    
    
    if (random.nextDouble() < 0.5) { // 50% chance
        
        long currentTime = System.currentTimeMillis();
        
        
        if (currentTime - lastSurpriseQuizTime < QUIZ_COOLDOWN) {
            System.out.println("Surprise quiz triggered, but on 2-minute cooldown.");
            return "NO_SURPRISE_QUIZ";
        }
        
        System.out.println("Surprise quiz triggered! (Cooldown passed)");
        
        // Generate a default quiz
        String quizData = getQuizQuestions("General Knowledge", 5, "Easy", "Multiple-Choice"); 
        
        if (quizData != null && quizData.startsWith("QUIZ_DATA_SUCCESS")) {
            lastSurpriseQuizTime = currentTime; // Start the cooldown
            return quizData.replaceFirst("QUIZ_DATA_SUCCESS", "SURPRISE_QUIZ_DATA");
        } else {
            return "NO_SURPRISE_QUIZ"; // AI failed, don't start cooldown
        }
    } else {
        // 50% chance failed
        return "NO_SURPRISE_QUIZ";
    }
}
    
    // --- REAL: Saves AI questions (with crash protection) ---
    // This method is built to save ALL 3 quiz types, but the SQL is only for MC
    private static void saveQuestionsToDB(String quizData) {
        // We only save Multiple-Choice questions for now
        String sql = "INSERT INTO questions (topic, question_text, option_a, option_b, option_c, option_d, correct_answer) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String[] parts = quizData.split("\\|");
            if (parts.length < 2) {
                System.err.println("AI Generator: Failed to save. No quiz parts found.");
                return;
            }
            
            String topic = parts[0];
            String firstQuestion = parts[1];
            int tildeCount = firstQuestion.length() - firstQuestion.replace("~", "").length();
            
            // --- We only save Multiple-Choice (5 tildes) ---
            if (tildeCount == 5) {
                int questionsSaved = 0;
                for (int i = 1; i < parts.length; i++) {
                    String[] qParts = parts[i].split("~");
                    
                    if (qParts.length == 6) { // Safety check for MC
                        pstmt.setString(1, topic);
                        pstmt.setString(2, qParts[0]); // question_text
                        pstmt.setString(3, qParts[1]); // option_a
                        pstmt.setString(4, qParts[2]); // option_b
                        pstmt.setString(5, qParts[3]); // option_c
                        pstmt.setString(6, qParts[4]); // option_d
                        pstmt.setString(7, qParts[5]); // correct_answer (index)
                        
                        pstmt.addBatch(); 
                        questionsSaved++;
                    } else {
                        System.err.println("AI Generator: Malformed MC question part skipped. Expected 6 parts, got " + qParts.length);
                    }
                }
                
                if (questionsSaved > 0) {
                    pstmt.executeBatch(); 
                    System.out.println("AI Generator: Saved " + questionsSaved + " new MC questions to database.");
                } else {
                    System.err.println("AI Generator: Failed to save any MC questions. AI response was fully malformed.");
                }
            } else {
                // This is a True/False or Identification quiz
                System.out.println("AI Generator: T/F or ID quiz generated. Not saving to DB.");
            }
            
        } catch (Exception e) {
            System.err.println("AI Generator: Failed to save questions to DB.");
            e.printStackTrace();
        }
    }
}