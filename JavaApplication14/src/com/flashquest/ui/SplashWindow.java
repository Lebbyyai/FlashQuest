/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flashquest.ui;

import com.flashquest.ui.components.RoundedPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

/**
 * The application splash screen.
 */
public class SplashWindow extends JWindow {
    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private final JLabel percentLabel = new JLabel("0%", SwingConstants.CENTER);
    private float opacity = 0f;

    public SplashWindow(Runnable onFinish) {
        setSize(640, 360);
        setLocationRelativeTo(null);
        setBackground(new Color(0, 0, 0, 0)); // Transparent background
        setLayout(new BorderLayout());

        JPanel gradientBg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Use Theme colors
                GradientPaint paint = new GradientPaint(0, 0, Theme.COLOR_PRIMARY, getWidth(), getHeight(), Theme.COLOR_PRIMARY_LIGHT);
                g2.setPaint(paint);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        gradientBg.setLayout(new GridBagLayout());

        RoundedPanel card = new RoundedPanel(20, new Color(255, 255, 255, 240));
        card.setPreferredSize(new Dimension(520, 260));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 28, 20, 28));

        JLabel title = new JLabel("FlashQuest", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 38));
        title.setForeground(Theme.COLOR_PRIMARY_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Learning made fun.", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(90, 90, 110));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setBorder(new EmptyBorder(8, 0, 16, 0));

        progressBar.setValue(0);
        progressBar.setPreferredSize(new Dimension(420, 14));
        progressBar.setBorderPainted(false);
        progressBar.setBackground(new Color(230, 230, 240));
        progressBar.setForeground(Theme.COLOR_PRIMARY); // Use Theme color

        percentLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        percentLabel.setForeground(new Color(70, 60, 90));
        percentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        percentLabel.setBorder(new EmptyBorder(10, 0, 0, 0));

        card.add(Box.createVerticalGlue());
        card.add(title);
        card.add(subtitle);
        card.add(Box.createVerticalGlue());
        JPanel pbWrap = new JPanel();
        pbWrap.setOpaque(false);
        pbWrap.add(progressBar);
        card.add(pbWrap);
        card.add(percentLabel);
        card.add(Box.createVerticalGlue());

        gradientBg.add(card);
        add(gradientBg, BorderLayout.CENTER);

        // Timer for fade-in
        Timer fadeIn = new Timer(40, null);
        fadeIn.addActionListener(e -> {
            opacity += 0.08f;
            if (opacity >= 1f) {
                opacity = 1f;
                fadeIn.stop();
            }
            setOpacity(opacity);
        });
        fadeIn.start();

        // Timer for progress bar
        Timer timer = new Timer(45, null);
        final int[] pct = {0};
        timer.addActionListener(e -> {
            pct[0] += 2; // Speed up loading
            if (pct[0] > 100) pct[0] = 100;
            progressBar.setValue(pct[0]);
            percentLabel.setText(pct[0] + "%");

            if (pct[0] >= 100) {
                ((Timer) e.getSource()).stop();
                // Start fade-out
                new Thread(() -> {
                    try {
                        for (int i = 0; i <= 10; i++) {
                            float op = 1f - (i / 10f);
                            SwingUtilities.invokeLater(() -> setOpacity(op));
                            Thread.sleep(80); // Faster fade-out
                        }
                    } catch (InterruptedException ignored) {}
                    // When finished, dispose this window and run the onFinish task
                    SwingUtilities.invokeLater(() -> {
                        dispose();
                        onFinish.run();
                    });
                }).start();
            }
        });
        timer.setRepeats(true);
        timer.start();
        setOpacity(0f);
        setVisible(true);
    }
}
