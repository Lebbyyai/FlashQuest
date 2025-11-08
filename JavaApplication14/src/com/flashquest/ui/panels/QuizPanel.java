package com.flashquest.ui.panels;

import com.flashquest.AppController;
import com.flashquest.services.NetworkClient;
import com.flashquest.ui.Theme;
import com.flashquest.ui.components.AnimatedRoundedButton;
import com.flashquest.ui.components.RoundedPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities; 
import javax.swing.Timer;

public class QuizPanel extends RoundedPanel {
    
    // --- Inner class to hold question data ---
    private class Question {
        String questionText;
        String[] options;
        int correctAnswerIndex;

        Question(String questionText, String[] options, int correctAnswerIndex) {
            this.questionText = questionText;
            this.options = options;
            this.correctAnswerIndex = correctAnswerIndex;
        }
    }

    private NetworkClient networkClient;
    private AppController appController;

    private JLabel topicLabel;
    private JLabel streakLabel;
    private JLabel timerLabel;
    private JLabel questionLabel;
    private AnimatedRoundedButton[] answerButtons = new AnimatedRoundedButton[4];
    private JCheckBox powerUp1;
    private JCheckBox powerUp2;
    private JCheckBox powerUp3;

    private List<Question> questions = new ArrayList<>();
    private int currentQuestionIndex;
    private int numCorrectAnswers;
    private int currentStreak;
    private int longestStreak;
    private Timer gameTimer;
    private int timeRemaining = 60; 

    public QuizPanel(NetworkClient client, AppController controller) {
        super(20, Theme.COLOR_CARD_BG); 
        
        this.networkClient = client;
        this.appController = controller;
        
        setLayout(new BorderLayout(0, 15));
        setMaximumSize(new Dimension(800, 600)); 
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        add(createHeader(), BorderLayout.NORTH);
        add(createQuestionPanel(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
        
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

        topicLabel = new JLabel("Loading Quiz..."); 
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

    private JPanel createQuestionPanel() {
        JPanel questionPanel = new JPanel(new BorderLayout(0, 20));
        questionPanel.setOpaque(false);

        questionLabel = new JLabel("Waiting for question data...");
        questionLabel.setFont(Theme.FONT_HEADER_MEDIUM);
        questionLabel.setForeground(Theme.COLOR_TEXT_DARK);
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        questionLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        questionPanel.add(questionLabel, BorderLayout.NORTH);

        JPanel answersGrid = new JPanel(new GridLayout(2, 2, 15, 15));
        answersGrid.setOpaque(false);

        for (int i = 0; i < 4; i++) {
            answerButtons[i] = new AnimatedRoundedButton(
                "...", Color.WHITE, Theme.COLOR_PRIMARY, 20
            );
            answerButtons[i].setFont(Theme.FONT_BODY_BOLD);
            answerButtons[i].setPreferredSize(new Dimension(0, 80)); 
            
            final int answerIndex = i;
            answerButtons[i].addActionListener(e -> handleAnswer(answerIndex));
            answersGrid.add(answerButtons[i]);
        }

        questionPanel.add(answersGrid, BorderLayout.CENTER);
        return questionPanel;
    }

    private JPanel createFooter() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        footerPanel.setOpaque(false); 
        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        JLabel powerUpTitle = new JLabel("Power-ups:");
        powerUpTitle.setFont(Theme.FONT_BODY_BOLD);
        powerUpTitle.setForeground(Theme.COLOR_TEXT_DARK);
        powerUp1 = new JCheckBox("50/50");
        powerUp1.setFont(Theme.FONT_BODY);
        powerUp1.setOpaque(false);
        powerUp1.setForeground(Theme.COLOR_TEXT_DARK);
        powerUp2 = new JCheckBox("Skip Question");
        powerUp2.setFont(Theme.FONT_BODY);
        powerUp2.setOpaque(false);
        powerUp2.setForeground(Theme.COLOR_TEXT_DARK);
        powerUp3 = new JCheckBox("Extra Time");
        powerUp3.setFont(Theme.FONT_BODY);
        powerUp3.setOpaque(false);
        powerUp3.setForeground(Theme.COLOR_TEXT_DARK);
        footerPanel.add(powerUpTitle);
        footerPanel.add(powerUp1);
        footerPanel.add(powerUp2);
        footerPanel.add(powerUp3);
        return footerPanel;
    }
    
    // --- THIS IS THE NEW, FIXED METHOD ---
    // REPLACE this method in QuizPanel.java
// REPLACE this method in QuizPanel.java
public void loadQuizData(String data, String quizType) {
    questions.clear();
    
    try {
        String[] parts = data.split("\\|"); 
        String topic = parts[0].replace("-", " ");
        topicLabel.setText(topic + " (Multiple Choice)"); 
        
        for (int i = 1; i < parts.length; i++) {
            String[] qParts = parts[i].split("~");
            
            // --- THIS IS THE FIX ---
            if (qParts.length == 6) { // Check for 6 parts
                String qText = qParts[0];
                String[] options = { qParts[1], qParts[2], qParts[3], qParts[4] };
                int correctIndex = Integer.parseInt(qParts[5]);
                questions.add(new Question(qText, options, correctIndex));
            } else {
                System.err.println("QuizPanel: Skipping malformed question part. Expected 6, got " + qParts.length);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to parse quiz data. Returning to dashboard.", "Quiz Error", JOptionPane.ERROR_MESSAGE);
        appController.showDashboard();
        return;
    }
    
    if (questions.isEmpty()) {
        JOptionPane.showMessageDialog(this, "AI failed to generate valid questions. Please try a different topic.", "Quiz Error", JOptionPane.ERROR_MESSAGE);
        appController.showDashboard();
        return;
    }

    // Reset stats
    currentQuestionIndex = 0;
    numCorrectAnswers = 0;
    currentStreak = 0;
    longestStreak = 0;
    timeRemaining = 60; 
    streakLabel.setText("Streak: 0");
    timerLabel.setText("Time: " + timeRemaining);
    for (AnimatedRoundedButton btn : answerButtons) {
        btn.setEnabled(true);
    }
    displayCurrentQuestion();
    if (gameTimer != null && gameTimer.isRunning()) {
        gameTimer.stop();
    }
    gameTimer.start();
}
    
    private void displayCurrentQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question q = questions.get(currentQuestionIndex);
            questionLabel.setText("<html><center>" + q.questionText + "</center></html>");
            for (int i = 0; i < 4; i++) {
                answerButtons[i].setText("<html><center>" + q.options[i] + "</center></html>");
                answerButtons[i].setBackground(Theme.COLOR_PRIMARY); 
                answerButtons[i].setForeground(Color.WHITE);
            }
        }
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
    
    private void handleAnswer(int index) {
        for (AnimatedRoundedButton btn : answerButtons) {
            btn.setEnabled(false);
        }
        
        Question q = questions.get(currentQuestionIndex);
        boolean isCorrect = (index == q.correctAnswerIndex);
        
        if (isCorrect) {
            numCorrectAnswers++;
            currentStreak++;
            if (currentStreak > longestStreak) {
                longestStreak = currentStreak;
            }
            streakLabel.setText("Streak: " + currentStreak);
            answerButtons[index].setBackground(Theme.COLOR_ACCENT_GREEN);
        } else {
            currentStreak = 0;
            streakLabel.setText("Streak: 0");
            answerButtons[index].setBackground(Theme.COLOR_ACCENT_RED);
            answerButtons[q.correctAnswerIndex].setBackground(Theme.COLOR_ACCENT_GREEN); 
        }
        
        Timer nextQuestionTimer = new Timer(1000, e -> {
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                displayCurrentQuestion();
                for (AnimatedRoundedButton btn : answerButtons) {
                    btn.setEnabled(true);
                }
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