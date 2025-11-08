package com.flashquest;

import com.flashquest.services.NetworkClient;
import com.flashquest.ui.panels.*;
import com.flashquest.ui.Theme; 
import com.flashquest.ui.SplashWindow;
import com.flashquest.ui.panels.QuizOptionsDialog.QuizOptions;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout; 
import java.awt.BorderLayout; 
import java.awt.Color; 
import javax.swing.JDialog; 
import javax.swing.JFrame;
import javax.swing.JLabel; 
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar; 
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder; 

public class AppController {

    private static AppController instance;
    private JFrame mainFrame;
    private JPanel mainPanel; 
    private CardLayout cardLayout;
    
    private LoginPanel loginPanel;
    private SignUpPanel signUpPanel;
    private DashboardPanel dashboardPanel;
    private ResultsPanel resultsPanel;
    private LeaderboardPanel leaderboardPanel;
    private PrizeShopPanel prizeShopPanel;
    private AccountPanel accountPanel;
    
    // --- NEW: All 3 Quiz Panels ---
    private QuizPanel multipleChoicePanel; // Renamed for clarity
    private TrueFalsePanel trueFalsePanel;
    private IdentificationPanel identificationPanel;
    
    private JDialog loadingDialog;
    private Timer surpriseQuizTimer;
    
    private int currentUserId;
    private String currentUserDisplayName;
    private int currentUserPoints; 
    
    // --- NEW: Quiz state flags ---
    private boolean isQuizActive = false;
    private String activeQuizPanelName = ""; // "Quiz-MC", "Quiz-TF", "Quiz-ID"

    private NetworkClient networkClient; 

    private AppController() {
        networkClient = new NetworkClient(this); 
        
        mainFrame = new JFrame("FlashQuest");
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        // --- Create all panels ---
        loginPanel = new LoginPanel(25, networkClient); 
        signUpPanel = new SignUpPanel(25, networkClient);
        dashboardPanel = new DashboardPanel(networkClient, this); 
        resultsPanel = new ResultsPanel(this); 
        leaderboardPanel = new LeaderboardPanel(networkClient, this);
        prizeShopPanel = new PrizeShopPanel(networkClient, this);
        accountPanel = new AccountPanel(networkClient, this);
        
        // --- NEW: Initialize all 3 quiz panels ---
        multipleChoicePanel = new QuizPanel(networkClient, this);
        trueFalsePanel = new TrueFalsePanel(networkClient, this);
        identificationPanel = new IdentificationPanel(networkClient, this);
        
        
        // --- Create Wrappers for ALL card-based panels ---
        JPanel loginWrapper = createWrapperPanel(loginPanel);
        JPanel signUpWrapper = createWrapperPanel(signUpPanel);
        JPanel resultsWrapper = createWrapperPanel(resultsPanel);
        JPanel leaderboardWrapper = createWrapperPanel(leaderboardPanel);
        JPanel prizeShopWrapper = createWrapperPanel(prizeShopPanel); 
        JPanel accountWrapper = createWrapperPanel(accountPanel);
        
        // --- NEW: Create wrappers for all 3 quiz panels ---
        JPanel mcQuizWrapper = createWrapperPanel(multipleChoicePanel);
        JPanel tfQuizWrapper = createWrapperPanel(trueFalsePanel);
        JPanel idQuizWrapper = createWrapperPanel(identificationPanel);
        
        
        // --- Add panels to the CardLayout ---
        mainPanel.add(loginWrapper, "Login");     
        mainPanel.add(signUpWrapper, "SignUp");   
        mainPanel.add(resultsWrapper, "Results");
        mainPanel.add(leaderboardWrapper, "Leaderboard");
        mainPanel.add(prizeShopWrapper, "PrizeShop");
        mainPanel.add(accountWrapper, "Account"); 
        
        // --- NEW: Add all 3 quiz panels with unique names ---
        mainPanel.add(mcQuizWrapper, "Quiz-MC"); // Multiple-Choice
        mainPanel.add(tfQuizWrapper, "Quiz-TF"); // True/False
        mainPanel.add(idQuizWrapper, "Quiz-ID"); // Identification

        mainPanel.add(dashboardPanel, "Dashboard"); // This is the only one not in a wrapper
        
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.add(mainPanel);
        mainFrame.setMinimumSize(new Dimension(1024, 768));
        mainFrame.setLocationRelativeTo(null); 
    }
    
    // (createWrapperPanel and getInstance are unchanged)
    private JPanel createWrapperPanel(JPanel content) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Theme.COLOR_PRIMARY);
        wrapper.add(content); 
        return wrapper;
    }

    public static AppController getInstance() {
        if (instance == null) {
            instance = new AppController();
        }
        return instance;
    }
    
    // --- UPDATED: Panel Switching Methods ---

    public void showLogin() {
        stopSurpriseQuizTimer(); 
        isQuizActive = false; // Reset flag
        cardLayout.show(mainPanel, "Login");
        mainFrame.setTitle("FlashQuest - Login");
    }
    
    public void showSignUp() {
        stopSurpriseQuizTimer(); 
        isQuizActive = false; // Reset flag
        cardLayout.show(mainPanel, "SignUp");
        mainFrame.setTitle("FlashQuest - Sign Up");
    }
    
    public void showDashboard() { 
        isQuizActive = false; // Reset flag
        dashboardPanel.loadUserData(); 
        cardLayout.show(mainPanel, "Dashboard"); 
        mainFrame.setTitle("FlashQuest - Dashboard"); 
    }
    
    // --- UPDATED: This method now checks the flag ---
    public void showQuiz() { 
        if (isQuizActive) {
            // A quiz is active, show the correct panel (e.g., "Quiz-TF")
            cardLayout.show(mainPanel, activeQuizPanelName); 
            mainFrame.setTitle("FlashQuest - Quiz");
        } else {
            // No quiz active, show the message
            JOptionPane.showMessageDialog(
                mainFrame, 
                "To start a new quiz, please go to the 'Home' (Dashboard) panel and:\n\n" +
                "1. Generate a quiz from a topic.\n" +
                "2. Upload a .txt file to generate a quiz.\n\n" +
                "This 'Quiz' tab is only for a quiz that is already in progress.",
                "How to Start a Quiz",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    public void showResults(int numCorrect, int totalQuestions, int longestStreak) { 
        isQuizActive = false; // Reset flag
        resultsPanel.setScores(numCorrect, totalQuestions, longestStreak); 
        cardLayout.show(mainPanel, "Results"); 
        mainFrame.setTitle("FlashQuest - Results");
        networkClient.sendQuizResults(numCorrect, currentUserId);
    }
    
    // (showLeaderboard, showPrizeShop, showAbout, showAccount are unchanged)
    public void showLeaderboard() { 
        leaderboardPanel.loadLeaderboardData(); 
        cardLayout.show(mainPanel, "Leaderboard"); 
        mainFrame.setTitle("FlashQuest - Leaderboard"); 
    }
    public void showPrizeShop() { 
        prizeShopPanel.loadShopData(this.currentUserPoints); 
        cardLayout.show(mainPanel, "PrizeShop"); 
        mainFrame.setTitle("FlashQuest - Prize Shop"); 
    }
    public void showAbout() { 
        JOptionPane.showMessageDialog(mainFrame, "FlashQuest App Version 1.0", "About", JOptionPane.INFORMATION_MESSAGE); 
    } 
    
    public void showAccount() { 
        networkClient.requestAccountData(currentUserId); 
        cardLayout.show(mainPanel, "Account"); 
        mainFrame.setTitle("FlashQuest - Account"); 
    }
    
    public String getDisplayName() { return this.currentUserDisplayName; }
    public int getCurrentUserId() { return this.currentUserId; }
    
    public void start() { mainFrame.setVisible(true); } 
    public void shutdown() {
        stopSurpriseQuizTimer(); 
        networkClient.disconnect();
        mainFrame.dispose();
        System.exit(0);
    }

    public static void main(String[] args) { 
        SwingUtilities.invokeLater(() -> {
            Runnable onSplashFinish = () -> {
                AppController.getInstance().start();
                AppController.getInstance().showLogin();
            };
            new SplashWindow(onSplashFinish);
        }); 
    }

    // (All handle... methods are unchanged *except* handleQuizData)
    // ...
    public void handleLoginSuccess(String userData) {
    try {
        // We expect data like: "1 lab"
        String[] parts = userData.split(" ", 2);
        this.currentUserId = Integer.parseInt(parts[0]);
        this.currentUserDisplayName = parts[1];
    } catch (Exception e) {
        // --- THIS IS THE BUG ---
        // If the server sends bad data (e.g., just "1"),
        // the code crashes and runs this:
        this.currentUserId = -1;
        this.currentUserDisplayName = "User"; // <-- The name changes to "User"
    }
    // ...
    showDashboard(); 
}
    public void handleLoginFailure(String message) { JOptionPane.showMessageDialog(mainFrame, "Login Failed: " + message, "Error", JOptionPane.ERROR_MESSAGE); }
    public void handleRegistrationSuccess() { JOptionPane.showMessageDialog(mainFrame, "Registration successful! Please log in.", "Success", JOptionPane.INFORMATION_MESSAGE); showLogin(); }
    public void handleRegistrationFailure(String message) { JOptionPane.showMessageDialog(mainFrame, "Registration Failed: " + message, "Error", JOptionPane.ERROR_MESSAGE); }
    // REPLACE this method in AppController.java
public void handleStatsResponse(String statsData) {
    try {
        // Server sends: "quizzes badges avgScore allTimeHigh"
        String[] parts = statsData.split(" "); 
        
        // --- THIS IS THE FIX ---
        // We now correctly read all 4 parts
        this.currentUserPoints = Integer.parseInt(parts[1]); // parts[1] is badges
        
        // Pass all 4 parts to the Dashboard
        dashboardPanel.updateStats(parts[0], parts[1], parts[2], parts[3]);
        
        if (resultsPanel.isShowing()) {
            resultsPanel.showUpdatedBadges(parts[1]); // parts[1] is badges
        }
    } catch (Exception e) {
        this.currentUserPoints = 0; 
        dashboardPanel.updateStats("N/A", "N/A", "N/A", "N/A");
    }
}
    public void handleLeaderboardData(String data) {
        if (leaderboardPanel != null) leaderboardPanel.updateTable(data);
    }
    public void handleRedeemResponse(String response) {
        if (response.equals("REDEEM_SUCCESS")) {
            JOptionPane.showMessageDialog(mainFrame, "Prize Redeemed!", "Success", JOptionPane.INFORMATION_MESSAGE);
            showDashboard();
        } else if (response.equals("REDEEM_FAIL_FUNDS")) {
            JOptionPane.showMessageDialog(mainFrame, "You do not have enough badges.", "Redeem Failed", JOptionPane.ERROR_MESSAGE);
            prizeShopPanel.loadShopData(this.currentUserPoints);
        } else {
            JOptionPane.showMessageDialog(mainFrame, "An error occurred.", "Redeem Failed", JOptionPane.ERROR_MESSAGE);
            prizeShopPanel.loadShopData(this.currentUserPoints);
        }
    }
    // REPLACE this method in AppController.java
public void handleAccountData(String data) {
    if (data == null) {
        // Send 4 default values on error
        accountPanel.loadData("Error", "Error", "0", "0");
        return;
    }
    try {
        // Server sends: "username,email,totalScore,badges"
        String[] parts = data.split(",");
        
        // --- THIS IS THE FIX ---
        // Pass all 4 parts to the new AccountPanel
        accountPanel.loadData(parts[0], parts[1], parts[2], parts[3]);
        
    } catch (Exception e) {
        // Send 4 default values on error
        accountPanel.loadData("Error", "Error", "0", "0");
    }
}
    public void handleEmailUpdateSuccess() {
        JOptionPane.showMessageDialog(mainFrame, "Email updated!", "Update Success", JOptionPane.INFORMATION_MESSAGE);
        showAccount(); 
    }
    public void handlePasswordUpdateSuccess() {
        JOptionPane.showMessageDialog(mainFrame, "Password updated!", "Update Success", JOptionPane.INFORMATION_MESSAGE);
        showAccount(); 
    }
    public void handleEmailUpdateFailure(String message) {
        JOptionPane.showMessageDialog(mainFrame, "Email update failed: " + message, "Update Error", JOptionPane.ERROR_MESSAGE);
        showAccount(); 
    }
    public void handlePasswordUpdateFailure(String message) {
        JOptionPane.showMessageDialog(mainFrame, "Password update failed: " + message, "Update Error", JOptionPane.ERROR_MESSAGE);
        showAccount();
    }
    public void handleUpdateFailure(String message) {
        JOptionPane.showMessageDialog(mainFrame, "An update failed: " + message, "Update Error", JOptionPane.ERROR_MESSAGE);
        showAccount();
    }

    // (requestQuizGeneration is unchanged)
    public void requestQuizGeneration(String topic, QuizOptions options) {
        showLoadingDialog("Generating your quiz...\nThis may take a moment.");
        networkClient.sendQuizRequest(topic, options.quizType, options.difficulty, options.numQuestions);
    }
    
    // (requestQuizFromText is unchanged)
    public void requestQuizFromText(String fileContent, QuizOptions options) {
        showLoadingDialog("Analyzing text and building quiz...");
        networkClient.sendQuizRequest("File-Upload", options.quizType, options.difficulty, options.numQuestions, fileContent);
    }
    
    // --- THIS IS THE FINAL, UPDATED 'handleQuizData' METHOD ---
    public void handleQuizData(String data, boolean isSurpriseQuiz) {
        hideLoadingDialog();
        
        String topic = "Quiz";
        String quizType = "Multiple-Choice"; // Default
        
        try {
             topic = data.substring(0, data.indexOf("|")).replace("-", " ");
             // Try to find the quiz type from the AI response
             // This is a bit of a guess, but we check for common formats
             String firstQuestion = data.split("\\|")[1];
             int tildeCount = firstQuestion.length() - firstQuestion.replace("~", "").length();

             if (tildeCount == 5) {
                 quizType = "Multiple-Choice";
             } else if (tildeCount == 1) {
                 // Could be T/F or Identification
                 String answer = firstQuestion.split("~")[1];
                 if (answer.equalsIgnoreCase("True") || answer.equalsIgnoreCase("False")) {
                     quizType = "True/False";
                 } else {
                     quizType = "Identification";
                 }
             }
        } catch (Exception e) {
            System.err.println("Could not parse topic or type from quiz data: " + data);
        }
        
        // Show the surprise quiz popup
        if (isSurpriseQuiz) {
            int choice = JOptionPane.showConfirmDialog(
                mainFrame,
                "A surprise " + quizType + " quiz on '" + topic + "' is available!\nWould you like to take it now?",
                "Surprise Quiz!",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            
            if (choice == JOptionPane.NO_OPTION) {
                return;
            }
        }
        
        // --- THIS IS THE NEW LOGIC ---
        // Set the flags for the active quiz
        this.isQuizActive = true;
        
        // Load the data into the correct panel and show it
        if (quizType.equals("True/False")) {
            this.activeQuizPanelName = "Quiz-TF";
            trueFalsePanel.loadQuizData(data, quizType);
            
        } else if (quizType.equals("Identification")) {
            this.activeQuizPanelName = "Quiz-ID";
            identificationPanel.loadQuizData(data, quizType);
            
        } else { // Default to Multiple-Choice
            this.activeQuizPanelName = "Quiz-MC";
            multipleChoicePanel.loadQuizData(data, quizType);
        }
        
        // Show the panel
        cardLayout.show(mainPanel, activeQuizPanelName);
        mainFrame.setTitle("FlashQuest - " + topic);
    }

    public void handleQuizGenFailure(String message) {
        hideLoadingDialog();
        JOptionPane.showMessageDialog(mainFrame, message, "Quiz Generation Failed", JOptionPane.ERROR_MESSAGE);
    }
    
    public void handleQuizResultsSuccess(String newTotalBadges) {
        System.out.println("Server has saved the quiz results. New total: " + newTotalBadges);
        if (resultsPanel != null) {
            resultsPanel.showUpdatedBadges(newTotalBadges);
        }
        try {
            this.currentUserPoints = Integer.parseInt(newTotalBadges);
        } catch (Exception e) {
            e.printStackTrace();
        }
        networkClient.requestStats(currentUserId);
    }
    
    // (start/stop surprise quiz timer and loading dialogs are unchanged)
    private void startSurpriseQuizTimer() {
        if (surpriseQuizTimer != null && surpriseQuizTimer.isRunning()) {
            surpriseQuizTimer.stop();
        }
        int threeMinutes = 180000; // 3 minutes
        surpriseQuizTimer = new Timer(threeMinutes, e -> {
            if (dashboardPanel.isShowing()) {
                System.out.println("Timer fired: Checking for surprise quiz...");
                networkClient.checkForSurpriseQuiz();
            }
        });
        surpriseQuizTimer.setRepeats(true);
        surpriseQuizTimer.start();
        System.out.println("Surprise quiz timer started (3-minute interval).");
    }
    
    private void stopSurpriseQuizTimer() {
        if (surpriseQuizTimer != null && surpriseQuizTimer.isRunning()) {
            surpriseQuizTimer.stop();
            System.out.println("Surprise quiz timer stopped.");
        }
    }
    
    private void showLoadingDialog(String message) {
        if (loadingDialog != null && loadingDialog.isVisible()) {
            loadingDialog.dispose();
        }
        loadingDialog = new JDialog(mainFrame, "Please Wait", true);
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel(message);
        label.setFont(Theme.FONT_BODY_BOLD);
        label.setHorizontalAlignment(JLabel.CENTER);
        panel.add(label, BorderLayout.NORTH);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        panel.add(progressBar, BorderLayout.CENTER);
        loadingDialog.setUndecorated(true);
        loadingDialog.add(panel);
        loadingDialog.pack();
        loadingDialog.setLocationRelativeTo(mainFrame);
        new Thread(() -> loadingDialog.setVisible(true)).start();
    }
    
    private void hideLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dispose();
        }
    }
}