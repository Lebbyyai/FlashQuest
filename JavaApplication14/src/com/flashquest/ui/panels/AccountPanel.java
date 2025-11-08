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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class AccountPanel extends RoundedPanel {

    private AppController appController;
    private NetworkClient networkClient;

    private JLabel titleLabel;
    private JLabel usernameLabel;
    
    // --- FIX: This was missing ---
    private JLabel emailLabel; 
    
    private JLabel totalBadgesLabel;
    
    // --- Components for editing ---
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private AnimatedRoundedButton editButton;
    private AnimatedRoundedButton saveButton;
    private JPanel editFieldsPanel;

    public AccountPanel(NetworkClient client, AppController controller) {
        super(20, Theme.COLOR_CARD_BG);
        this.appController = controller;
        this.networkClient = client;

        setLayout(new BorderLayout(0, 15));
        setMaximumSize(new Dimension(800, 600));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        add(createHeader(), BorderLayout.NORTH);
        add(createFormPanel(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setOpaque(false);

        AnimatedRoundedButton backButton = new AnimatedRoundedButton(
            "Back", Theme.COLOR_PRIMARY_DARK, Color.WHITE, 15
        );
        backButton.setFont(Theme.FONT_BODY_BOLD);
        backButton.addActionListener(e -> appController.showDashboard());
        headerPanel.add(backButton, BorderLayout.WEST);

        titleLabel = new JLabel("Account Settings");
        titleLabel.setFont(Theme.FONT_HEADER_LARGE); 
        titleLabel.setForeground(Theme.COLOR_TEXT_DARK);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(backButton.getPreferredSize());
        headerPanel.add(spacer, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Row 1: Username ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel userTitle = new JLabel("Username:");
        userTitle.setFont(Theme.FONT_BODY_BOLD); 
        userTitle.setForeground(Theme.COLOR_TEXT_LIGHT);
        formPanel.add(userTitle, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        usernameLabel = new JLabel("Loading...");
        usernameLabel.setFont(Theme.FONT_BODY_BOLD); 
        usernameLabel.setForeground(Theme.COLOR_TEXT_DARK);
        formPanel.add(usernameLabel, gbc);

        // --- ROW 2: Email (This was the missing row) ---
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel emailTitle = new JLabel("Email:");
        emailTitle.setFont(Theme.FONT_BODY_BOLD);
        emailTitle.setForeground(Theme.COLOR_TEXT_LIGHT);
        formPanel.add(emailTitle, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        emailLabel = new JLabel("Loading..."); // <-- FIX: Initializing it
        emailLabel.setFont(Theme.FONT_BODY); // Use regular font
        emailLabel.setForeground(Theme.COLOR_TEXT_DARK);
        formPanel.add(emailLabel, gbc);


        // --- Row 3: Total Badges (was Row 2) ---
        gbc.gridy = 2; // <-- Changed to 2
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel badgeTitle = new JLabel("Total Badges:");
        badgeTitle.setFont(Theme.FONT_BODY_BOLD); 
        badgeTitle.setForeground(Theme.COLOR_TEXT_LIGHT);
        formPanel.add(badgeTitle, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        totalBadgesLabel = new JLabel("0");
        totalBadgesLabel.setFont(Theme.FONT_HEADER_MEDIUM); 
        totalBadgesLabel.setForeground(Theme.COLOR_ACCENT_TEAL);
        formPanel.add(totalBadgesLabel, gbc);
        
        // --- Row 4: Edit Button (was Row 3) ---
        gbc.gridy = 3; // <-- Changed to 3
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        editButton = new AnimatedRoundedButton("Edit Email/Password", Theme.COLOR_ACCENT_ORANGE, Color.WHITE, 15);
        editButton.setFont(Theme.FONT_BODY_BOLD);
        editButton.addActionListener(e -> toggleEditMode(true));
        formPanel.add(editButton, gbc);

        // --- Row 5: Collapsible Edit Fields (was Row 4) ---
        gbc.gridy = 4; // <-- Changed to 4
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        editFieldsPanel = createEditFieldsPanel();
        editFieldsPanel.setVisible(false); // Start hidden
        formPanel.add(editFieldsPanel, gbc);

        return formPanel;
    }
    
    private JPanel createEditFieldsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridy = 0;
        gbc.gridx = 0;
        JLabel emailTitle = new JLabel("New Email:");
        emailTitle.setFont(Theme.FONT_BODY_BOLD);
        panel.add(emailTitle, gbc);
        
        gbc.gridx = 1;
        emailField = new JTextField(20);
        emailField.setFont(Theme.FONT_BODY);
        panel.add(emailField, gbc);
        
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel passTitle = new JLabel("New Password:");
        passTitle.setFont(Theme.FONT_BODY_BOLD);
        panel.add(passTitle, gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.setFont(Theme.FONT_BODY);
        panel.add(passwordField, gbc);
        
        gbc.gridy = 2;
        gbc.gridx = 0;
        JLabel confirmTitle = new JLabel("Confirm Pass:");
        confirmTitle.setFont(Theme.FONT_BODY_BOLD);
        panel.add(confirmTitle, gbc);
        
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(Theme.FONT_BODY);
        panel.add(confirmPasswordField, gbc);
        
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        
        saveButton = new AnimatedRoundedButton("Save Changes", Theme.COLOR_ACCENT_GREEN, Color.WHITE, 15);
        saveButton.setFont(Theme.FONT_BODY_BOLD);
        saveButton.addActionListener(e -> handleSaveChanges());
        
        AnimatedRoundedButton cancelButton = new AnimatedRoundedButton("Cancel", Theme.COLOR_ACCENT_RED, Color.WHITE, 15);
        cancelButton.setFont(Theme.FONT_BODY_BOLD);
        cancelButton.addActionListener(e -> toggleEditMode(false));
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private void toggleEditMode(boolean enable) {
        editButton.setVisible(!enable);
        editFieldsPanel.setVisible(enable);
        
        if (enable) {
            emailField.setText(emailLabel.getText()); 
            passwordField.setText("");
            confirmPasswordField.setText("");
        }
    }
    
    private void handleSaveChanges() {
        String newEmail = emailField.getText();
        String newPassword = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Check if email was changed
        if (!newEmail.equals(emailLabel.getText())) {
            networkClient.sendUpdateEmail(appController.getCurrentUserId(), newEmail);
        }

        // Check if password was changed
        if (!newPassword.isEmpty()) {
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Password Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (newPassword.length() < 6) {
                 JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.", "Password Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            networkClient.sendUpdatePassword(appController.getCurrentUserId(), newPassword);
        }

        toggleEditMode(false);
    }

    // --- FIX: This method now correctly updates the new emailLabel ---
    public void loadData(String username, String email, String totalScore, String totalBadges) {
        usernameLabel.setText(username);
        emailLabel.setText(email); // <-- This will now work
        totalBadgesLabel.setText(totalBadges);
        
        emailField.setText(email);
    }
}