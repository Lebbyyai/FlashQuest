package com.flashquest.ui.panels;

import com.flashquest.AppController;
import com.flashquest.services.NetworkClient;
import com.flashquest.ui.Theme;
import com.flashquest.ui.components.AnimatedRoundedButton;
import com.flashquest.ui.components.RoundedPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane; // <-- ADD THIS IMPORT
import javax.swing.JPanel;
import javax.swing.SwingConstants; 

public class PrizeShopPanel extends RoundedPanel {

    private NetworkClient networkClient;
    private AppController appController;
    
    private JLabel userBadgesLabel;
    private AnimatedRoundedButton redeem50;
    private AnimatedRoundedButton redeem100;
    private AnimatedRoundedButton redeem500;

    public PrizeShopPanel(NetworkClient client, AppController controller) {
        super(20, Theme.COLOR_CARD_BG); 
        
        this.networkClient = client;
        this.appController = controller;
        
        setLayout(new BorderLayout(0, 15));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        setMaximumSize(new Dimension(800, 400));
        setPreferredSize(new Dimension(800, 400));

        // 1. Header (Back Button, Title + Badge Count)
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setOpaque(false);
        
        AnimatedRoundedButton backButton = new AnimatedRoundedButton(
            "Back", Theme.COLOR_PRIMARY_DARK, Theme.COLOR_BACKGROUND, 15
        );
        backButton.setFont(Theme.FONT_BODY_BOLD);
        backButton.addActionListener(e -> appController.showDashboard());
        headerPanel.add(backButton, BorderLayout.WEST);
        
        JLabel title = new JLabel("Prize Shop");
        title.setFont(Theme.FONT_HEADER_LARGE);
        title.setForeground(Theme.COLOR_TEXT_DARK);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(title, BorderLayout.CENTER);
        
        userBadgesLabel = new JLabel("Your Badges: 0");
        userBadgesLabel.setFont(Theme.FONT_HEADER_MEDIUM);
        userBadgesLabel.setForeground(Theme.COLOR_PRIMARY);
        headerPanel.add(userBadgesLabel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);

        // 2. Prize Cards
        JPanel prizesPanel = new JPanel(new GridLayout(1, 3, 20, 20)); 
        prizesPanel.setOpaque(false);
        prizesPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Prize 1: 50 Badges
        redeem50 = new AnimatedRoundedButton("Redeem", Color.WHITE, Theme.COLOR_ACCENT_TEAL, 20);
        redeem50.addActionListener(e -> redeemPrize(50, "3 Chocolate Bars"));
        prizesPanel.add(createPrizeCard(
            "3 Chocolate Bars", "50 Badges", redeem50, Theme.COLOR_PRIMARY
        ));

        // Prize 2: 100 Badges
        redeem100 = new AnimatedRoundedButton("Redeem", Color.WHITE, Theme.COLOR_ACCENT_TEAL, 20);
        redeem100.addActionListener(e -> redeemPrize(100, "200 Pesos"));
        prizesPanel.add(createPrizeCard(
            "200 Pesos", "100 Badges", redeem100, Theme.COLOR_ACCENT_TEAL // <-- FIX: Changed from Orange
        ));
        
        // Prize 3: 500 Badges
        redeem500 = new AnimatedRoundedButton("Redeem", Color.WHITE, Theme.COLOR_ACCENT_TEAL, 20);
        redeem500.addActionListener(e -> redeemPrize(500, "A New Bag"));
        prizesPanel.add(createPrizeCard(
            "A New Bag", "500 Badges", redeem500, Theme.COLOR_ACCENT_RED
        ));
        
        add(prizesPanel, BorderLayout.CENTER);
    }
    
    /**
     * Helper method to create a single prize card UI.
     */
    private RoundedPanel createPrizeCard(String title, String cost, AnimatedRoundedButton button, Color bgColor) {
        RoundedPanel card = new RoundedPanel(20, bgColor);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.FONT_HEADER_MEDIUM);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel costLabel = new JLabel(cost);
        costLabel.setFont(Theme.FONT_BODY_BOLD);
        costLabel.setForeground(Color.WHITE);
        costLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(Theme.FONT_BODY_BOLD);
        button.setMaximumSize(new Dimension(150, 40));
        
        // --- FIX: Set the disabled colors ---
        button.setDisabledBackground(Theme.COLOR_BORDER); // Light grey
        button.setDisabledForeground(Color.DARK_GRAY);    // Dark grey text
        // ------------------------------------

        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(costLabel);
        card.add(Box.createVerticalGlue());
        card.add(button);
        
        return card;
    }

    /**
     * Called by AppController when the panel is shown.
     */
    public void loadShopData(int currentUserBadges) {
        userBadgesLabel.setText("Your Badges: " + currentUserBadges);
        
        // This will now correctly show the disabled styles
        redeem50.setEnabled(currentUserBadges >= 50);
        redeem100.setEnabled(currentUserBadges >= 100);
        redeem500.setEnabled(currentUserBadges >= 500);
    }

    /**
     * Called when a redeem button is clicked.
     */
    private void redeemPrize(int cost, String itemName) {
        // --- FIX: Show confirmation popup ---
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to redeem " + cost + " badges for " + itemName + "?",
            "Confirm Purchase",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Disable buttons to prevent double-click
            redeem50.setEnabled(false);
            redeem100.setEnabled(false);
            redeem500.setEnabled(false);
            // Send request
            networkClient.sendRedeemRequest(appController.getCurrentUserId(), cost);
        }
    }
}