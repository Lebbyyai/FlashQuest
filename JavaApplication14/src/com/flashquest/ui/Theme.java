package com.flashquest.ui;

import java.awt.Color;
import java.awt.Font;

/**
 * Defines all color and font constants used by the UI.
 * This file MUST be complete and correct to resolve all "cannot find symbol: variable COLOR_..." errors.
 */
public class Theme {

    // --- Primary Colors (Fixes COLOR_PRIMARY, COLOR_PRIMARY_LIGHT) ---
    public static final Color COLOR_PRIMARY = new Color(138, 118, 240);       // Main Purple 
    public static final Color COLOR_PRIMARY_DARK = new Color(75, 59, 149);    
    public static final Color COLOR_PRIMARY_LIGHT = new Color(200, 190, 255); 
    
    // --- Accent/Action Colors (Fixes COLOR_ACCENT_GREEN, COLOR_ACCENT_ORANGE) ---
    public static final Color COLOR_ACCENT_TEAL = new Color(52, 201, 172);    
    public static final Color COLOR_ACCENT_RED = new Color(240, 95, 95);      
    public static final Color COLOR_ACCENT = new Color(255, 179, 0);          
    public static final Color COLOR_ACCENT_GREEN = new Color(40, 180, 99);    
    public static final Color COLOR_ACCENT_ORANGE = new Color(243, 156, 18);  

    // --- Backgrounds & Borders (Fixes COLOR_BORDER, COLOR_SIDEBAR_BG, COLOR_SIDEBAR_HOVER) ---
    public static final Color COLOR_BACKGROUND = new Color(245, 245, 250);   
    public static final Color COLOR_CARD_BG = new Color(255, 255, 255);      
    public static final Color COLOR_BORDER = new Color(220, 220, 220);       
    public static final Color COLOR_SIDEBAR_BG = new Color(51, 51, 76);       // Used by DashboardPanel
    public static final Color COLOR_SIDEBAR_HOVER = new Color(70, 70, 100);   // Used by NavButton logic

    // --- Text Colors ---
    public static final Color COLOR_TEXT_DARK = new Color(34, 34, 34);        
    public static final Color COLOR_TEXT_LIGHT = new Color(102, 102, 102);    
    public static final Color COLOR_TEXT_INVERTED = new Color(255, 255, 255); 

    // --- Fonts (Fixes FONT_HEADER_LARGE, FONT_BODY, FONT_HEADER_MEDIUM, FONT_BODY_BOLD) ---
    public static final Font FONT_HEADER_LARGE = new Font("Segoe UI", Font.BOLD, 36); 
    public static final Font FONT_HEADER_MEDIUM = new Font("Segoe UI", Font.BOLD, 24); 
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);         
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 16);     
}