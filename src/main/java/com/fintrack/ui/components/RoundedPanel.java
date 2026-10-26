package com.fintrack.ui.components;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

/** JPanel with a softly rounded surface, used for cards and grouped controls. */
public class RoundedPanel extends JPanel {
    private final int arc;
    private Color outline;

    public RoundedPanel(Color fill, int arc) { this(fill, arc, null); }

    public RoundedPanel(Color fill, int arc, Color outline) {
        this.arc = arc;
        this.outline = outline;
        setBackground(fill);
        setOpaque(false);
    }

    public void setOutline(Color outline) { this.outline = outline; repaint(); }

    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        RoundRectangle2D shape = new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, arc, arc);
        g.setColor(getBackground());
        g.fill(shape);
        if (outline != null) {
            g.setColor(outline);
            g.draw(shape);
        }
        g.dispose();
    }
}
