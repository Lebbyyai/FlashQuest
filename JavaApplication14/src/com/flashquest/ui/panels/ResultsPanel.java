package com.flashquest.ui.panels;

import com.flashquest.AppController;
import com.flashquest.ui.Theme;
import com.flashquest.ui.components.AnimatedRoundedButton;
import com.flashquest.ui.components.RoundedPanel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Color;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class ResultsPanel extends JPanel {

    private AppController controller;
    private JLabel scoreLabel;
    private JLabel streakLabel;
    private JLabel badgesEarnedLabel;
    private JLabel totalBadgesLabel;

    public ResultsPanel(AppController controller) {
        this.controller = controller;
        super(new GridBagLayout()); 
        setOpaque(false); 

        RoundedPanel card = new RoundedPanel(20, Theme.COLOR_CARD_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(25, 40, 25, 40));
        card.setPreferredSize(new Dimension(500, 350));
        card.setMaximumSize(new Dimension(500, 350));

        JLabel title = new JLabel("Quiz Complete!", SwingConstants.CENTER);
        title.setFont(Theme.FONT_HEADER_LARGE);
        title.setForeground(Theme.COLOR_TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        scoreLabel = new JLabel("You Scored: 0 / 0", SwingConstants.CENTER);
        scoreLabel.setFont(Theme.FONT_HEADER_MEDIUM);
        scoreLabel.setForeground(Theme.COLOR_PRIMARY);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreLabel.setBorder(new EmptyBorder(15, 0, 5, 0));

        streakLabel = new JLabel("Longest Streak: 0", SwingConstants.CENTER);
        streakLabel.setFont(Theme.FONT_BODY_BOLD);
        streakLabel.setForeground(Theme.COLOR_TEXT_LIGHT);
        streakLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        badgesEarnedLabel = new JLabel("Badges Earned: 0", SwingConstants.CENTER);
        badgesEarnedLabel.setFont(Theme.FONT_BODY_BOLD);
        badgesEarnedLabel.setForeground(Theme.COLOR_ACCENT_GREEN);
        badgesEarnedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        badgesEarnedLabel.setBorder(new EmptyBorder(10, 0, 5, 0));

        totalBadgesLabel = new JLabel("Updating total badges...", SwingConstants.CENTER);
        totalBadgesLabel.setFont(Theme.FONT_BODY);
        totalBadgesLabel.setForeground(Theme.COLOR_TEXT_DARK);
        totalBadgesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(scoreLabel);
        card.add(streakLabel);
        card.add(badgesEarnedLabel);
        card.add(totalBadgesLabel);
        card.add(Box.createRigidArea(new Dimension(0, 25)));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnRow.setOpaque(false);

        AnimatedRoundedButton playAgain = new AnimatedRoundedButton(
            "Play Again", Color.WHITE, Theme.COLOR_PRIMARY_DARK, 18
        );
        playAgain.setFont(Theme.FONT_BODY_BOLD);
        playAgain.addActionListener(e -> controller.showDashboard()); 

        AnimatedRoundedButton mainMenu = new AnimatedRoundedButton(
            "Main Menu", Color.WHITE, Theme.COLOR_PRIMARY, 18
        );
        mainMenu.setFont(Theme.FONT_BODY_BOLD);
        mainMenu.addActionListener(e -> controller.showDashboard()); 

        btnRow.add(playAgain);
        btnRow.add(mainMenu);
        
        card.add(btnRow);
        add(card);
    }
    
    /**
     * This method signature MUST match what AppController calls.
     */
    public void setScores(int numCorrect, int totalQuestions, int streak) {
        scoreLabel.setText("You Scored: " + numCorrect + " / " + totalQuestions);
        streakLabel.setText("Longest Streak: " + streak);
        badgesEarnedLabel.setText("Badges Earned: +" + numCorrect);
        totalBadgesLabel.setText("Saving results...");
    }
    
    /**
     * This is called by AppController after the server confirms the new total.
     */
    public void showUpdatedBadges(String newTotalBadges) {
        totalBadgesLabel.setText("New Total Badges: " + newTotalBadges);
    }
}