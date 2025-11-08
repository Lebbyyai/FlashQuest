package com.flashquest.ui.panels;

import javax.swing.JOptionPane;
import com.flashquest.AppController;
import com.flashquest.services.NetworkClient;
import com.flashquest.ui.Theme;
import com.flashquest.ui.components.*;
// --- ADD THESE IMPORTS ---
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.nio.file.Files;
// ---
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame; 
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities; 
import com.flashquest.ui.panels.QuizOptionsDialog.QuizOptions;

public class DashboardPanel extends JPanel {

    private NetworkClient networkClient;
    private AppController appController;

    private JLabel welcomeLabel;
    private StatsCard quizzesCard;
    private StatsCard pointsCard;
    private StatsCard rankCard;
    private StatsCard timeCard;
    
    private JTextArea topicArea; 

    public DashboardPanel(NetworkClient client, AppController controller) {
        super(new BorderLayout(0, 0)); 
        this.networkClient = client;
        this.appController = controller;
        setBackground(Theme.COLOR_PRIMARY);

        add(createSidebar(), BorderLayout.WEST);
        add(createMainContentPanel(), BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Theme.COLOR_SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel title = new JLabel("FlashQuest");
        title.setFont(Theme.FONT_HEADER_MEDIUM);
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));
        sidebar.add(title);

        NavButton homeBtn = new NavButton("Home", true); 
        homeBtn.addActionListener(e -> appController.showDashboard());
        
        NavButton quizBtn = new NavButton("Quiz", false);
        quizBtn.addActionListener(e -> appController.showQuiz());

        NavButton leaderboardBtn = new NavButton("Leaderboard", false);
        leaderboardBtn.addActionListener(e -> appController.showLeaderboard());
        
        NavButton shopBtn = new NavButton("Shop", false);
        shopBtn.addActionListener(e -> appController.showPrizeShop());
        
        NavButton aboutBtn = new NavButton("About", false);
        aboutBtn.addActionListener(e -> appController.showAbout());
    
        NavButton accountBtn = new NavButton("Account", false);
        accountBtn.addActionListener(e -> appController.showAccount());
    
        sidebar.add(accountBtn);
        sidebar.add(homeBtn);
        sidebar.add(quizBtn);
        sidebar.add(leaderboardBtn);
        sidebar.add(shopBtn);
        sidebar.add(aboutBtn);

        sidebar.add(Box.createVerticalGlue()); 
        
        NavButton logoutBtn = new NavButton("Logout", false);
        logoutBtn.addActionListener(e -> appController.showLogin());
        sidebar.add(logoutBtn);

        return sidebar;
    }

    private JPanel createMainContentPanel() {
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false); 
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        mainContent.add(createHeader(), BorderLayout.NORTH);
        mainContent.add(createCenterContent(), BorderLayout.CENTER);

        return mainContent;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel dashboardTitle = new JLabel("Dashboard");
        dashboardTitle.setFont(Theme.FONT_HEADER_LARGE);
        dashboardTitle.setForeground(Theme.COLOR_TEXT_DARK);
        header.add(dashboardTitle, BorderLayout.WEST);

        RoundedPanel searchBar = new RoundedPanel(20, Color.WHITE);
        searchBar.setLayout(new BorderLayout(10, 0));
        searchBar.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        JTextField searchField = new JTextField("Search...");
        searchField.setBorder(null);
        searchField.setFont(Theme.FONT_BODY);
        searchField.setForeground(Theme.COLOR_TEXT_LIGHT);
        
        searchBar.add(searchField, BorderLayout.CENTER);
        
        header.add(searchBar, BorderLayout.CENTER);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);
        
        welcomeLabel = new JLabel("Welcome, User!");
        welcomeLabel.setFont(Theme.FONT_BODY_BOLD);
        welcomeLabel.setForeground(Theme.COLOR_TEXT_DARK);
        
        JLabel userIcon = new JLabel(); 
        userIcon.setPreferredSize(new Dimension(32, 32));
        userIcon.setBorder(BorderFactory.createLineBorder(Color.BLACK)); 
        
        userPanel.add(welcomeLabel);
        userPanel.add(userIcon);
        header.add(userPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createCenterContent() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        centerPanel.add(createStatsPanel());
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(createGeneratorPanel());

        return centerPanel;
    }


    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        statsPanel.setOpaque(false);
        
        quizzesCard = new StatsCard(Theme.COLOR_ACCENT_ORANGE, "Quizzes Completed", "0");
        pointsCard = new StatsCard(Theme.COLOR_ACCENT_TEAL, "Total Badges", "0");
        
        // --- FIX: Labels updated to match real data ---
        rankCard = new StatsCard(Theme.COLOR_ACCENT_ORANGE, "Avg. Points", "0.0");
        timeCard = new StatsCard(Theme.COLOR_ACCENT_RED, "All-Time High", "0"); 

        statsPanel.add(quizzesCard);
        statsPanel.add(pointsCard);
        statsPanel.add(rankCard);
        statsPanel.add(timeCard);
        
        return statsPanel;
    }
    
    // --- THIS IS THE FUNCTIONAL FILE UPLOAD METHOD ---
    private void handleFileUpload() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a .txt file to analyze");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files (*.txt)", "txt");
        fileChooser.setFileFilter(filter);
        
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            try {
                String fileContent = Files.readString(selectedFile.toPath());
                
                if (fileContent.isBlank()) {
                    JOptionPane.showMessageDialog(this, "The selected file is empty.", "File Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                QuizOptionsDialog dialog = new QuizOptionsDialog(parentFrame);
                QuizOptions options = dialog.showDialog();
                
                if (options != null) {
                    appController.requestQuizFromText(fileContent, options);
                }

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                    this, 
                    "Could not read file: " + e.getMessage(), 
                    "File Error", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private JPanel createGeneratorPanel() {
        RoundedPanel aiPanel = new RoundedPanel(15, Color.WHITE);
        aiPanel.setLayout(new BorderLayout(0, 10));
        aiPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel aiTitle = new JLabel("AI Quiz Generator");
        aiTitle.setFont(Theme.FONT_HEADER_MEDIUM);
        aiTitle.setForeground(Theme.COLOR_TEXT_DARK);
        aiPanel.add(aiTitle, BorderLayout.NORTH);

        topicArea = new JTextArea("Enter a topic (e.g., 'Solar System', 'Java Basics')...");
        topicArea.setFont(Theme.FONT_BODY);
        topicArea.setForeground(Theme.COLOR_TEXT_LIGHT);
        topicArea.setLineWrap(true);
        topicArea.setWrapStyleWord(true);
        topicArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.COLOR_BORDER),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        aiPanel.add(topicArea, BorderLayout.CENTER);

        AnimatedRoundedButton generateBtn = new AnimatedRoundedButton(
            "Generate Quiz", Color.WHITE, Theme.COLOR_PRIMARY, 20
        );
        generateBtn.setFont(Theme.FONT_BODY_BOLD);
        generateBtn.addActionListener(e -> handleGenerateQuiz());
        
        AnimatedRoundedButton uploadBtn = new AnimatedRoundedButton(
            "Upload File", Color.WHITE, Theme.COLOR_PRIMARY, 20
        );
        uploadBtn.setFont(Theme.FONT_BODY_BOLD);
        uploadBtn.addActionListener(e -> handleFileUpload());
        
        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnWrapper.setOpaque(false);
        btnWrapper.add(generateBtn);
        btnWrapper.add(uploadBtn); 
        aiPanel.add(btnWrapper, BorderLayout.SOUTH);

        return aiPanel;
    }
    
    private void handleGenerateQuiz() {
        String topic = topicArea.getText();
        String placeholder = "Enter a topic (e.g., 'Solar System', 'Java Basics')...";

        if (topic.isEmpty() || topic.equals(placeholder) || topic.trim().length() < 3) {
            JOptionPane.showMessageDialog(
                this, "Please enter a valid topic for your quiz.", "Invalid Topic", JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        QuizOptionsDialog dialog = new QuizOptionsDialog(parentFrame);
        
        QuizOptions options = dialog.showDialog();

        if (options != null) {
            appController.requestQuizGeneration(topic, options);
        }
    }

    public void loadUserData() { 
        welcomeLabel.setText("Welcome, " + appController.getDisplayName() + "!");
        networkClient.requestStats(appController.getCurrentUserId()); 
    }
    
    
    public void updateStats(String quizzes, String totalBadges, String avgScore, String allTimeHigh) {
        quizzesCard.setValue(quizzes);
        pointsCard.setValue(totalBadges); 
        rankCard.setValue(avgScore); 
        timeCard.setValue(allTimeHigh);
        
        revalidate();
        repaint();
    }
}