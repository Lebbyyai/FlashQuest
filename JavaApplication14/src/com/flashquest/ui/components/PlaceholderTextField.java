package com.flashquest.ui.components;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

// Used for Email, Username, etc.
public class PlaceholderTextField extends JTextField {

    private final String placeholder;
    private boolean isPlaceholderVisible;

    public PlaceholderTextField(String placeholder) {
        this.placeholder = placeholder;
        this.isPlaceholderVisible = true;
        
        // Initial setup
        setText(placeholder);
        setForeground(Color.GRAY);
        setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding
        
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (isPlaceholderVisible) {
                    setText("");
                    setForeground(Color.BLACK); // Use a darker color when typing
                    isPlaceholderVisible = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().trim().isEmpty()) {
                    setText(placeholder);
                    setForeground(Color.GRAY);
                    isPlaceholderVisible = true;
                }
            }
        });
    }

    @Override
    public String getText() {
        if (isPlaceholderVisible) {
            return "";
        }
        return super.getText();
    }
}