package com.flashquest.ui.components;

import com.flashquest.ui.Theme;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

// Assuming you have an AnimatedRoundedButton and RoundedPanel class
public class ShopItemCard extends RoundedPanel {

    public ShopItemCard(String name, String description, int price, java.awt.Color color, java.awt.GridBagConstraints gbc) {
        super(15, Theme.COLOR_CARD_BG);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setPreferredSize(new Dimension(250, 180));
        
        // Header Panel (Title and Cost Badge)
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(name);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Theme.COLOR_TEXT_DARK);
        
        // Badge showing the cost
        RoundedPanel costBadge = new RoundedPanel(10, color);
        costBadge.setBorder(new EmptyBorder(4, 8, 4, 8));
        JLabel costLabel = new JLabel(price + " Badges");
        costLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        costLabel.setForeground(Color.WHITE);
        costBadge.add(costLabel);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createHorizontalGlue());
        headerPanel.add(costBadge);
        
        // Description
        JLabel descLabel = new JLabel("<html>" + description + "</html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(Theme.COLOR_TEXT_LIGHT);
        descLabel.setAlignmentX(LEFT_ALIGNMENT);
        descLabel.setBorder(new EmptyBorder(5, 0, 15, 0));

        // Buy Button Placeholder (Requires AnimatedRoundedButton.java)
        AnimatedRoundedButton buyButton = new AnimatedRoundedButton(
            "Buy", 
            Color.WHITE, 
            color, 
            25
        );
        buyButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 35));
        buyButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        buyButton.setAlignmentX(LEFT_ALIGNMENT);
        
        add(headerPanel);
        add(descLabel);
        add(Box.createVerticalGlue());
        add(buyButton);
    }
}