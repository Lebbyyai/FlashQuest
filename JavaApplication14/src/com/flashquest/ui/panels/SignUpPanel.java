package com.flashquest.ui.panels;

import com.flashquest.AppController;
import com.flashquest.services.NetworkClient;
import com.flashquest.ui.Theme;
import com.flashquest.ui.components.*;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane; 
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


public class SignUpPanel extends RoundedPanel { 

    private NetworkClient networkClient; 

    // --- Use standard fields for the layout ---
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    
    private AnimatedRoundedButton createAccountButton;
    private LinkButton signInLink;
    

    public SignUpPanel(int radius, NetworkClient client) { 
        super(radius, Theme.COLOR_CARD_BG);
        this.networkClient = client; 

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60)); 
        setMaximumSize(new Dimension(500, 750)); 

        initComponents();
        addListeners();
    }
    
    private void initComponents() {
    
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(Theme.FONT_HEADER_LARGE); 
        titleLabel.setForeground(Theme.COLOR_TEXT_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(Theme.FONT_BODY);
        userLabel.setForeground(Theme.COLOR_TEXT_LIGHT);
        
        usernameField = new JTextField();
        usernameField.setFont(Theme.FONT_BODY_BOLD);
        Dimension fieldSize = new Dimension(350, 45); 
        usernameField.setPreferredSize(fieldSize);
        usernameField.setMaximumSize(fieldSize);
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.COLOR_BORDER),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(Theme.FONT_BODY);
        emailLabel.setForeground(Theme.COLOR_TEXT_LIGHT);
        
        emailField = new JTextField();
        emailField.setFont(Theme.FONT_BODY_BOLD);
        emailField.setPreferredSize(fieldSize);
        emailField.setMaximumSize(fieldSize);
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.COLOR_BORDER),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(Theme.FONT_BODY);
        passwordLabel.setForeground(Theme.COLOR_TEXT_LIGHT);

        passwordField = new JPasswordField();
        passwordField.setFont(Theme.FONT_BODY_BOLD);
        passwordField.setPreferredSize(fieldSize);
        passwordField.setMaximumSize(fieldSize);
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.COLOR_BORDER),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        JLabel confirmLabel = new JLabel("Confirm Password");
        confirmLabel.setFont(Theme.FONT_BODY);
        confirmLabel.setForeground(Theme.COLOR_TEXT_LIGHT);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(Theme.FONT_BODY_BOLD);
        confirmPasswordField.setPreferredSize(fieldSize);
        confirmPasswordField.setMaximumSize(fieldSize);
        confirmPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.COLOR_BORDER),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        createAccountButton = new AnimatedRoundedButton(
            "Create Account",              
            Theme.COLOR_TEXT_INVERTED,     
            Theme.COLOR_ACCENT_TEAL,       
            25                             
        );
        createAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        Dimension buttonSize = new Dimension(350, 50);
        createAccountButton.setPreferredSize(buttonSize);
        createAccountButton.setMaximumSize(buttonSize);
        createAccountButton.setFont(Theme.FONT_BODY_BOLD);

        signInLink = new LinkButton("Already have an account? Sign in here", Theme.COLOR_TEXT_LIGHT, Theme.COLOR_PRIMARY);
        signInLink.setAlignmentX(Component.CENTER_ALIGNMENT);

        
        // --- Add components to the panel ---
        add(Box.createVerticalGlue());
        add(titleLabel);
        add(Box.createRigidArea(new Dimension(0, 30)));
        
        add(createLabelPanel(userLabel)); 
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(usernameField);
        add(Box.createRigidArea(new Dimension(0, 20)));

        add(createLabelPanel(emailLabel)); 
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(emailField);
        add(Box.createRigidArea(new Dimension(0, 20)));

        add(createLabelPanel(passwordLabel)); 
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(passwordField);
        add(Box.createRigidArea(new Dimension(0, 20)));
        
        add(createLabelPanel(confirmLabel)); 
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(confirmPasswordField);
        add(Box.createRigidArea(new Dimension(0, 30)));

        add(createAccountButton);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(signInLink);
        
        add(Box.createVerticalGlue());
    }
    
    // Helper method for labels
    private JPanel createLabelPanel(JLabel label) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(350, 20)); 
        panel.add(label);
        panel.add(Box.createHorizontalGlue());
        return panel;
    }

    private void addListeners() {
        createAccountButton.addActionListener(e -> {
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            if (password.equals(confirmPassword)) {
                networkClient.sendRegisterRequest(username, email, password);
            } else {
                JOptionPane.showMessageDialog(SignUpPanel.this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        signInLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                AppController.getInstance().showLogin();
            }
        });
    }
}