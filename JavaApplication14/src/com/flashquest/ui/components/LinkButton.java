package com.flashquest.ui.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;

public class LinkButton extends JLabel {

    private final Color defaultColor;
    private final Color hoverColor;

    public LinkButton(String text, Color defaultColor, Color hoverColor) {
        super("<html><u>" + text + "</u></html>");
        this.defaultColor = defaultColor;
        this.hoverColor = hoverColor;
        
        // Use a smaller, regular font for links
        setFont(new Font("Segoe UI", Font.PLAIN, 12));
        setForeground(defaultColor);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setForeground(LinkButton.this.hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setForeground(LinkButton.this.defaultColor);
            }
        });
    }
}