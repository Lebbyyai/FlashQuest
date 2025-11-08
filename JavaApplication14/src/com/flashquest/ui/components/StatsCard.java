package com.flashquest.ui.components;

import com.flashquest.ui.Theme;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

// Requires RoundedPanel.java to exist
public class StatsCard extends RoundedPanel {

    private JLabel valueLabel;
    
    public StatsCard(java.awt.Color backgroundColor, String stitle, String iconPath) {
        super(20, backgroundColor);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setPreferredSize(new Dimension(250, 160));
        
        // Icon and Title
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setOpaque(false);
        
        // Placeholder for the icon
        JLabel iconLabel = new JLabel(" "); 
        
        JLabel title = new JLabel(stitle);
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(0, 10, 0, 0));

        headerPanel.add(iconLabel);
        headerPanel.add(title);
        headerPanel.add(Box.createHorizontalGlue());
        
        // Value (Score)
        valueLabel = new JLabel("0");
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(headerPanel);
        add(Box.createVerticalGlue());
        add(valueLabel);
        add(Box.createRigidArea(new Dimension(0, 5)));
    }
    
    public void setValue(String newValue) {
        valueLabel.setText(newValue);
    }
}