/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flashquest.ui.components;

import com.flashquest.ui.Theme; // Import the Theme file
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * A custom JPanel that paints a purple diagonal gradient.
 * This will be the main background for the application frame.
 */
public class GradientBackground extends JPanel {

    public GradientBackground() {
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Use the colors from our new Theme class
        g2.setPaint(new GradientPaint(0, 0, Theme.COLOR_PRIMARY, w, h, Theme.COLOR_PRIMARY_LIGHT));
        g2.fillRect(0, 0, w, h);
        g2.dispose();
    }
}