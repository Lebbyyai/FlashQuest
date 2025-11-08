package com.flashquest.ui.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class CircularImagePanel extends JPanel {

    private Image image;
    private int diameter;

    public CircularImagePanel(Image img, int diameter) {
        this.image = img;
        this.diameter = diameter;
        setPreferredSize(new Dimension(diameter, diameter));
        setOpaque(false);
    }

    /**
     * Public method to update the image after the panel is created.
     */
    public void setImage(Image newImage) { // <-- This correctly accepts an Image
        this.image = newImage;
        repaint(); // Redraw the panel with the new image
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Create a circular clipping mask
        Ellipse2D.Double clip = new Ellipse2D.Double(0, 0, diameter, diameter);
        g2.setClip(clip);

        if (image != null) {
            // Scale the image to fill the circle
            Image scaledImage = getScaledImageToFill(image, diameter, diameter);
            
            // Center the scaled image (in case it's not perfectly square)
            int x = (diameter - scaledImage.getWidth(null)) / 2;
            int y = (diameter - scaledImage.getHeight(null)) / 2;
            
            g2.drawImage(scaledImage, x, y, this);
        } else {
            // Draw a default gray circle if no image
            g2.setColor(getBackground().darker());
            g2.fill(clip);
        }

        g2.dispose();
    }

    private Image getScaledImageToFill(Image srcImg, int w, int h) {
        int srcWidth = srcImg.getWidth(null);
        int srcHeight = srcImg.getHeight(null);

        double wRatio = (double) w / (double) srcWidth;
        double hRatio = (double) h / (double) srcHeight;

        double ratio = Math.max(wRatio, hRatio);

        int newWidth = (int) (srcWidth * ratio);
        int newHeight = (int) (srcHeight * ratio);

        BufferedImage resizedImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, newWidth, newHeight, null);
        g2.dispose();

        return resizedImg;
    }
}