package com.fintrack.ui.components;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

/** Button with a rounded background and a small hover/press response. */
public class RoundedButton extends JButton {
    public enum Variant { PRIMARY, SECONDARY, DANGER, NAV, GHOST }
    private Variant variant;
    private boolean active;

    public RoundedButton(String text) { this(text, Variant.PRIMARY); }

    public RoundedButton(String text, Variant variant) {
        super(text);
        this.variant = variant;
        setFont(getFont().deriveFont(java.awt.Font.BOLD, 12f));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15));
        setForeground(foregroundForVariant());
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
    }

    public void setVariant(Variant variant) {
        this.variant = variant;
        setForeground(foregroundForVariant());
        repaint();
    }

    public void setActive(boolean active) {
        this.active = active;
        repaint();
    }

    private Color baseColor() {
        return switch (variant) {
            case PRIMARY -> AppTheme.PRIMARY;
            case SECONDARY -> new Color(235, 239, 247);
            case DANGER -> new Color(253, 235, 237);
            case NAV -> active ? new Color(62, 86, 151) : AppTheme.SIDEBAR_BUTTON;
            case GHOST -> new Color(0, 0, 0, 0);
        };
    }

    private Color foregroundForVariant() {
        return switch (variant) {
            case PRIMARY -> Color.WHITE;
            case SECONDARY -> AppTheme.TEXT;
            case DANGER -> AppTheme.RED;
            case NAV -> Color.WHITE;
            case GHOST -> variant == Variant.NAV ? Color.WHITE : AppTheme.MUTED;
        };
    }

    @Override protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color fill = baseColor();
        if (variant != Variant.GHOST && getModel().isPressed()) fill = fill.darker();
        else if (variant != Variant.GHOST && getModel().isRollover() && !(variant == Variant.NAV && active)) fill = fill.brighter();
        if (variant != Variant.GHOST) {
            g.setColor(fill);
            g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
        }
        g.dispose();
        super.paintComponent(graphics);
    }
}
