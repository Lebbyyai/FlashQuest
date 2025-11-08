package com.flashquest.ui.panels;

import com.flashquest.AppController;
import com.flashquest.services.NetworkClient;
import com.flashquest.ui.Theme;
import com.flashquest.ui.components.*; 
import java.awt.Color; 
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel; 
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// --- IMPORTS FOR LAYOUT ---
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.Font;
// -------------------------


public class LoginPanel extends RoundedPanel { 

    private NetworkClient networkClient; 
    
    // --- Use standard fields for the layout ---
    private JTextField emailField;
    private JPasswordField passwordField;
    
    private AnimatedRoundedButton signInButton;
    private LinkButton signUpLink;
    

    public LoginPanel(int radius, NetworkClient client) { 
        super(radius, Theme.COLOR_CARD_BG); 
        this.networkClient = client; 
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60)); 
        setMaximumSize(new Dimension(500, 650)); 
        
        initComponents();
        addListeners();
    }
    
    private void initComponents() {
    
        // --- 1. Title ---
        JLabel titleLabel = new JLabel("FlashQuest");
        titleLabel.setFont(Theme.FONT_HEADER_LARGE); 
        titleLabel.setForeground(Theme.COLOR_TEXT_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- 2. Email Field + Label ---
        JLabel emailLabel = new JLabel("Email or Username");
        emailLabel.setFont(Theme.FONT_BODY);
        emailLabel.setForeground(Theme.COLOR_TEXT_LIGHT);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT); 
        
        emailField = new JTextField();
        emailField.setFont(Theme.FONT_BODY_BOLD);
        Dimension fieldSize = new Dimension(350, 45);
        emailField.setPreferredSize(fieldSize);
        emailField.setMaximumSize(fieldSize);
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.COLOR_BORDER), 
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // --- 3. Password Field + Label ---
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(Theme.FONT_BODY);
        passwordLabel.setForeground(Theme.COLOR_TEXT_LIGHT);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT); 

        passwordField = new JPasswordField();
        passwordField.setFont(Theme.FONT_BODY_BOLD);
        passwordField.setPreferredSize(fieldSize);
        passwordField.setMaximumSize(fieldSize);
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.COLOR_BORDER), 
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // --- 4. Button and Link ---
        signInButton = new AnimatedRoundedButton(
            "Sign In",                     
            Theme.COLOR_TEXT_INVERTED,     
            Theme.COLOR_ACCENT_RED,        
            25                             
        );
        signInButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        Dimension buttonSize = new Dimension(350, 50);
        signInButton.setPreferredSize(buttonSize);
        signInButton.setMaximumSize(buttonSize);
        signInButton.setFont(Theme.FONT_BODY_BOLD);

        signUpLink = new LinkButton("Don't have an account? Sign up here", Theme.COLOR_TEXT_LIGHT, Theme.COLOR_PRIMARY);
        signUpLink.setAlignmentX(Component.CENTER_ALIGNMENT); // This centers the link

        
        // --- 5. Add components to the panel ---
        add(Box.createVerticalGlue());
        
        add(titleLabel);
        add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Panel for left-aligned label
        JPanel emailLabelPanel = new JPanel();
        emailLabelPanel.setLayout(new BoxLayout(emailLabelPanel, BoxLayout.X_AXIS));
        emailLabelPanel.setOpaque(false);
        emailLabelPanel.setMaximumSize(new Dimension(350, 20)); 
        emailLabelPanel.add(emailLabel);
        emailLabelPanel.add(Box.createHorizontalGlue());
        add(emailLabelPanel);
        add(Box.createRigidArea(new Dimension(0, 5)));
        
        add(emailField);
        add(Box.createRigidArea(new Dimension(0, 20)));

        // Panel for left-aligned label
        JPanel passwordLabelPanel = new JPanel();
        passwordLabelPanel.setLayout(new BoxLayout(passwordLabelPanel, BoxLayout.X_AXIS));
        passwordLabelPanel.setOpaque(false);
        passwordLabelPanel.setMaximumSize(new Dimension(350, 20)); 
        passwordLabelPanel.add(passwordLabel);
        passwordLabelPanel.add(Box.createHorizontalGlue());
        add(passwordLabelPanel);
        add(Box.createRigidArea(new Dimension(0, 5)));

        add(passwordField);
        add(Box.createRigidArea(new Dimension(0, 30)));
        
        add(signInButton);
        add(Box.createRigidArea(new Dimension(0, 20))); // Space
        
        add(signUpLink); // Add link *after* the button
        
        add(Box.createVerticalGlue());
    }
    
    private void addListeners() {
        signInButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            networkClient.sendLoginRequest(email, password);
        });
        
        signUpLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                AppController.getInstance().showSignUp();
            }
        });
    }
}