package com.flashquest.ui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

public class AnimatedRoundedButton extends JButton {

    private int radius;
    private Color defaultColor;
    private Color hoverColor;
    
    // --- ADD THESE FIELDS ---
    private Color enabledForeground;
    private Color disabledBackground;
    private Color disabledForeground;
    // ------------------------

    public AnimatedRoundedButton(String text, Color foreground, Color background, int radius) {
        super(text);
        this.radius = radius;
        this.defaultColor = background;
        this.hoverColor = background.darker();
        this.enabledForeground = foreground; // --- ADD THIS ---
        
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorder(new EmptyBorder(10, 20, 10, 20));
        setForeground(foreground);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(hoverColor);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(defaultColor);
                }
            }
        });
        
        setBackground(defaultColor);
    }
    
    // --- ADD THESE TWO SETTER METHODS ---
    public void setDisabledBackground(Color disabledBackground) {
        this.disabledBackground = disabledBackground;
    }
    
    public void setDisabledForeground(Color disabledForeground) {
        this.disabledForeground = disabledForeground;
    }
    // ------------------------------------

    // --- ADD THIS OVERRIDDEN METHOD ---
    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        if (b) {
            // Button is ENABLED
            setForeground(enabledForeground);
            setBackground(defaultColor);
        } else {
            // Button is DISABLED
            setForeground(disabledForeground != null ? disabledForeground : Color.DARK_GRAY);
            setBackground(disabledBackground != null ? disabledBackground : new Color(230, 230, 230));
        }
    }
    // ----------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- FIX: This now uses the correct color whether enabled or disabled ---
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        
        g2.dispose();
        
        // Draw the text (this will now use the correct foreground color)
        super.paintComponent(g);
    }
}