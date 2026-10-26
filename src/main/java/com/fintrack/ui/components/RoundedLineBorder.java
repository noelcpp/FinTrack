package com.fintrack.ui.components;

import javax.swing.border.AbstractBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

/** Rounded outline for text inputs that remains compatible with standard Swing fields. */
public final class RoundedLineBorder extends AbstractBorder {
    private final Color color;
    private final int arc;

    public RoundedLineBorder(Color color, int arc) {
        this.color = color;
        this.arc = arc;
    }

    @Override public void paintBorder(Component c, Graphics graphics, int x, int y, int width, int height) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.draw(new RoundRectangle2D.Float(x + 0.5f, y + 0.5f, width - 1f, height - 1f, arc, arc));
        g.dispose();
    }

    @Override public Insets getBorderInsets(Component c) { return new Insets(2, 2, 2, 2); }
    @Override public Insets getBorderInsets(Component c, Insets insets) {
        insets.set(2, 2, 2, 2);
        return insets;
    }
}
