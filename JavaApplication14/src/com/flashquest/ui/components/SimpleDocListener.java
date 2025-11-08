/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flashquest.ui.components;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * A simple DocumentListener that runs a Runnable on any change.
 * This simplifies the code in PlaceholderTextField and PlaceholderPasswordField.
 */
public class SimpleDocListener implements DocumentListener {
    private final Runnable r;

    SimpleDocListener(Runnable r) {
        this.r = r;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        r.run();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        r.run();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        r.run();
    }
}
