package com.flashquest.ui.panels;

import com.flashquest.ui.Theme;
import com.flashquest.ui.components.AnimatedRoundedButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox; 
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider; // We no longer use this, but keeping import is fine
import javax.swing.border.EmptyBorder;

public class QuizOptionsDialog extends JDialog {

    public static class QuizOptions {
        public String quizType; 
        public String difficulty;
        public int numQuestions; // This will now be 5, 10, or 20

        public QuizOptions(String type, String diff, int num) {
            this.quizType = type;
            this.difficulty = diff;
            this.numQuestions = num;
        }
    }

    private QuizOptions options;

    private JComboBox<String> typeDropdown;
    private JComboBox<String> difficultyDropdown;
    // --- FIX: Replaced Slider with JComboBox ---
    private JComboBox<Integer> numDropdown; 

    public QuizOptionsDialog(JFrame parent) {
        super(parent, "Quiz Options", true);
        initUI();
    }

    private void initUI() {
        setUndecorated(true);
        setResizable(false);
        
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Theme.COLOR_BORDER, 2));

        // --- Header ---
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(Theme.COLOR_PRIMARY);
        header.setBorder(new EmptyBorder(10, 15, 10, 15));
        JLabel title = new JLabel("Customize Your Quiz");
        title.setFont(Theme.FONT_BODY_BOLD); // Fixed font
        header.add(title);
        panel.add(header, BorderLayout.NORTH);

        // --- Options Grid ---
        JPanel optionsGrid = new JPanel(new GridBagLayout());
        optionsGrid.setBackground(Color.WHITE);
        optionsGrid.setBorder(new EmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5); // Added a bit more vertical spacing
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Row 1: Quiz Type ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setFont(Theme.FONT_BODY_BOLD);
        optionsGrid.add(typeLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        String[] quizTypes = {"Multiple-Choice", "True/False", "Identification"};
        typeDropdown = new JComboBox<>(quizTypes);
        typeDropdown.setFont(Theme.FONT_BODY);
        optionsGrid.add(typeDropdown, gbc);

        // --- Row 2: Difficulty ---
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel difficultyLabel = new JLabel("Difficulty:");
        difficultyLabel.setFont(Theme.FONT_BODY_BOLD);
        optionsGrid.add(difficultyLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        String[] difficulties = {"Easy", "Medium", "Hard"};
        difficultyDropdown = new JComboBox<>(difficulties);
        difficultyDropdown.setFont(Theme.FONT_BODY);
        optionsGrid.add(difficultyDropdown, gbc);

        // --- Row 3: Number of Questions (NEW DESIGN) ---
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel numQuestionsLabel = new JLabel("Questions:");
        numQuestionsLabel.setFont(Theme.FONT_BODY_BOLD);
        optionsGrid.add(numQuestionsLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        Integer[] numOptions = {5, 10, 20}; // The options
        numDropdown = new JComboBox<>(numOptions);
        numDropdown.setFont(Theme.FONT_BODY);
        optionsGrid.add(numDropdown, gbc);
        
        panel.add(optionsGrid, BorderLayout.CENTER);

        // --- Footer Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);
        
        AnimatedRoundedButton startButton = new AnimatedRoundedButton(
            "Start Quiz", Theme.COLOR_PRIMARY_DARK, Color.WHITE, 15
        );
        startButton.setFont(Theme.FONT_BODY_BOLD);
        startButton.addActionListener(this::onStart);

        AnimatedRoundedButton cancelButton = new AnimatedRoundedButton(
            "Cancel", Theme.COLOR_CARD_BG, Theme.COLOR_TEXT_DARK, 15
        );
        cancelButton.setFont(Theme.FONT_BODY_BOLD);
        cancelButton.addActionListener(this::onCancel);
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(startButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(panel);
    }

    private void onStart(ActionEvent e) {
        String type = (String) typeDropdown.getSelectedItem();
        String difficulty = (String) difficultyDropdown.getSelectedItem();
        // --- FIX: Get value from the new dropdown ---
        int num = (Integer) numDropdown.getSelectedItem(); 
        
        this.options = new QuizOptions(type, difficulty, num);
        
        setVisible(false);
        dispose();
    }

    private void onCancel(ActionEvent e) {
        this.options = null;
        setVisible(false);
        dispose();
    }

    public QuizOptions showDialog() {
        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true); 
        return this.options;
    }
}