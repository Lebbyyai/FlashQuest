package com.flashquest.ui.components;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

// Component to handle password field placeholders
public class PlaceholderPasswordField extends JPasswordField {

    private final String placeholder;
    private boolean isPlaceholderVisible;

    public PlaceholderPasswordField(String placeholder) {
        this.placeholder = placeholder;
        this.isPlaceholderVisible = true;
        
        // Initial setup
        setText(placeholder);
        setForeground(Color.GRAY);
        setEchoChar((char) 0); // Display placeholder text, not dots
        setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding
        
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (isPlaceholderVisible) {
                    setText("");
                    setForeground(Color.BLACK); 
                    setEchoChar('*'); // Start displaying dots for real password
                    isPlaceholderVisible = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getPassword().length == 0) {
                    setText(placeholder);
                    setForeground(Color.GRAY);
                    setEchoChar((char) 0); // Show placeholder text again
                    isPlaceholderVisible = true;
                }
            }
        });
    }

    @Override
    public char[] getPassword() {
        if (isPlaceholderVisible) {
            return new char[0]; // Return empty array if placeholder is visible
        }
        return super.getPassword();
    }
    
    // Override getText() for compatibility with other components
    @Override
    public String getText() {
        return new String(getPassword());
    }
}