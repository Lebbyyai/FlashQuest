package com.flashquest.ui.components;

import com.flashquest.ui.Theme;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class NavButton extends JButton {

    public NavButton(String text, boolean isSelected) {
        super(text);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setBorder(new EmptyBorder(12, 15, 12, 15));
        setHorizontalAlignment(SwingConstants.LEFT);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        setFocusPainted(false);
        setOpaque(true);

        if (isSelected) {
            setBackground(Theme.COLOR_PRIMARY_LIGHT);
            setForeground(Theme.COLOR_PRIMARY_DARK);
        } else {
            setBackground(Theme.COLOR_SIDEBAR_BG);
            setForeground(Theme.COLOR_TEXT_INVERTED); 
        }

        // Hover effect
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!isSelected) {
                    setBackground(Theme.COLOR_SIDEBAR_HOVER);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!isSelected) {
                    setBackground(Theme.COLOR_SIDEBAR_BG);
                }
            }
        });
    }
}