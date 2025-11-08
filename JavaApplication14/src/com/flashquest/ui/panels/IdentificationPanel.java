package com.flashquest.ui.panels;

import com.flashquest.AppController;
import com.flashquest.services.NetworkClient;
import com.flashquest.ui.Theme;
import com.flashquest.ui.components.AnimatedRoundedButton;
import com.flashquest.ui.components.RoundedPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component; // <-- NEW IMPORT
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box; // <-- NEW IMPORT
import javax.swing.BoxLayout; // <-- NEW IMPORT
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField; 
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class IdentificationPanel extends RoundedPanel {

    private class IDQuestion {
        String questionText;
        String correctAnswer; 

        IDQuestion(String questionText, String correctAnswer) {
            this.questionText = questionText;
            this.correctAnswer = correctAnswer;
        }
    }

    private NetworkClient networkClient;
    private AppController appController;

    private JLabel topicLabel;
    private JLabel streakLabel;
    private JLabel timerLabel;
    private JLabel questionLabel;
    private JTextField answerField; 
    private AnimatedRoundedButton submitButton; 

    private List<IDQuestion> questions = new ArrayList<>();
    private int currentQuestionIndex;
    private int numCorrectAnswers;
    private int currentStreak;
    private int longestStreak;
    private Timer gameTimer;
    private int timeRemaining = 60; 

    public IdentificationPanel(NetworkClient client, AppController controller) {
        super(20, Theme.COLOR_CARD_BG); 
        
        this.networkClient = client;
        this.appController = controller;
        
        setLayout(new BorderLayout(0, 15));
        setMaximumSize(new Dimension(800, 600)); 
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        add(createHeader(), BorderLayout.NORTH);
        add(createQuestionPanel(), BorderLayout.CENTER);
        
        gameTimer = new Timer(1000, e -> updateTimer());
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setOpaque(false);
        AnimatedRoundedButton backButton = new AnimatedRoundedButton(
            "Back", Theme.COLOR_PRIMARY_DARK, Color.WHITE, 15
        );
        backButton.setFont(Theme.FONT_BODY_BOLD);
        backButton.addActionListener(e -> onBackButtonPressed());
        headerPanel.add(backButton, BorderLayout.WEST);
        topicLabel = new JLabel("Loading Identification Quiz..."); 
        topicLabel.setFont(Theme.FONT_HEADER_MEDIUM);
        topicLabel.setForeground(Theme.COLOR_TEXT_DARK);
        topicLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(topicLabel, BorderLayout.CENTER);
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        statsPanel.setOpaque(false);
        streakLabel = new JLabel("Streak: 0");
        streakLabel.setFont(Theme.FONT_BODY_BOLD);
        streakLabel.setForeground(Theme.COLOR_ACCENT_ORANGE);
        statsPanel.add(streakLabel);
        timerLabel = new JLabel("Time: 60");
        timerLabel.setFont(Theme.FONT_BODY_BOLD);
        timerLabel.setForeground(Theme.COLOR_ACCENT_RED);
        statsPanel.add(timerLabel);
        headerPanel.add(statsPanel, BorderLayout.EAST);
        return headerPanel;
    }

    // --- THIS METHOD IS FIXED ---
    private JPanel createQuestionPanel() {
        // Use BoxLayout for more reliable layout
        JPanel questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        questionPanel.setOpaque(false);
        questionPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

        questionLabel = new JLabel("Waiting for question data...");
        questionLabel.setFont(Theme.FONT_HEADER_MEDIUM);
        questionLabel.setForeground(Theme.COLOR_TEXT_DARK);
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Needed for BoxLayout
        questionLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
        
        // Panel to hold the text field and submit button
        JPanel answersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        answersPanel.setOpaque(false);
        answersPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100)); // Give it a max height

        answerField = new JTextField(25); 
        answerField.setFont(Theme.FONT_BODY_BOLD);
        answerField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.COLOR_BORDER, 2),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        submitButton = new AnimatedRoundedButton(
            "Submit", Theme.COLOR_PRIMARY, Color.WHITE, 20
        );
        submitButton.setFont(Theme.FONT_BODY_BOLD);
        submitButton.setPreferredSize(new Dimension(150, 45));
        submitButton.addActionListener(e -> handleAnswer());

        answersPanel.add(answerField);
        answersPanel.add(submitButton);

        // Add both to the BoxLayout panel
        questionPanel.add(questionLabel);
        questionPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        questionPanel.add(answersPanel);
        
        return questionPanel;
    }

    // (The rest of the file is identical to what you have)
    
    public void loadQuizData(String data, String quizType) {
        questions.clear();
        
        try {
            String[] parts = data.split("\\|"); 
            
            String topic = parts[0].replace("-", " ");
            topicLabel.setText(topic + " (Identification)"); 
            
            for (int i = 1; i < parts.length; i++) {
                String[] qParts = parts[i].split("~");
                
                if (qParts.length == 2) { 
                    String qText = qParts[0];
                    String answer = qParts[1];
                    questions.add(new IDQuestion(qText, answer));
                } else {
                    System.err.println("IdentificationPanel: Skipping malformed question part. Expected 2, got " + qParts.length);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to parse Identification quiz data. Returning to dashboard.", "Quiz Error", JOptionPane.ERROR_MESSAGE);
            appController.showDashboard();
            return;
        }

        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "AI failed to generate valid questions. Please try again.", "Quiz Error", JOptionPane.ERROR_MESSAGE);
            appController.showDashboard();
            return;
        }

        currentQuestionIndex = 0;
        numCorrectAnswers = 0;
        currentStreak = 0;
        longestStreak = 0;
        timeRemaining = 60; 
        streakLabel.setText("Streak: 0");
        timerLabel.setText("Time: " + timeRemaining);
        setControlsEnabled(true);
        displayCurrentQuestion();
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }
        gameTimer.start();
    }
    
    private void displayCurrentQuestion() {
        if (currentQuestionIndex < questions.size()) {
            IDQuestion q = questions.get(currentQuestionIndex);
            questionLabel.setText("<html><center>" + q.questionText + "</center></html>");
            answerField.setText(""); 
            answerField.setBackground(Color.WHITE); 
        }
    }
    
    private void setControlsEnabled(boolean enabled) {
        answerField.setEnabled(enabled);
        submitButton.setEnabled(enabled);
    }

    private void updateTimer() {
        timeRemaining--;
        timerLabel.setText("Time: " + timeRemaining);
        if (timeRemaining <= 10) {
            timerLabel.setForeground(Theme.COLOR_ACCENT_RED);
        }
        
        if (timeRemaining <= 0) {
            endQuiz("Time's Up!");
        }
    }
    
    private void handleAnswer() {
        String userAnswer = answerField.getText().trim();
        if (userAnswer.isEmpty()) {
            return; 
        }
        
        setControlsEnabled(false);
        
        IDQuestion q = questions.get(currentQuestionIndex);
        
        boolean isCorrect = userAnswer.equalsIgnoreCase(q.correctAnswer.trim());
        
        if (isCorrect) {
            numCorrectAnswers++;
            currentStreak++;
            if (currentStreak > longestStreak) {
                longestStreak = currentStreak;
            }
            streakLabel.setText("Streak: " + currentStreak);
            answerField.setBackground(Theme.COLOR_ACCENT_GREEN);
        } else {
            currentStreak = 0;
            streakLabel.setText("Streak: 0");
            answerField.setBackground(Theme.COLOR_ACCENT_RED);
            questionLabel.setText("<html><center>" + q.questionText + "<br/>Correct Answer: " + q.correctAnswer + "</center></html>");
        }
        
        Timer nextQuestionTimer = new Timer(1500, e -> { 
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                displayCurrentQuestion();
                setControlsEnabled(true);
            } else {
                endQuiz("Quiz Complete!");
            }
        });
        nextQuestionTimer.setRepeats(false);
        nextQuestionTimer.start();
    }

    private void endQuiz(String title) {
        gameTimer.stop();
        JOptionPane.showMessageDialog(this, title + "\nYou scored " + numCorrectAnswers + " out of " + questions.size(), "Quiz Over", JOptionPane.INFORMATION_MESSAGE);
        
        appController.showResults(numCorrectAnswers, questions.size(), longestStreak);
    }
    
    private void onBackButtonPressed() {
        gameTimer.stop(); 
        int choice = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to go back? All progress will be lost.",
            "Leave Quiz?",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            appController.showDashboard(); 
        } else {
            gameTimer.start(); 
        }
    }
}