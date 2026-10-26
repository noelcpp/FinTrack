package com.fintrack.ui.components;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

public class SummaryCard extends RoundedPanel {
    private final JLabel valueLabel = new JLabel("₹0.00");
    private final Color accent;

    public SummaryCard(String title) { this(title, AppTheme.PRIMARY); }

    public SummaryCard(String title, Color accent) {
        super(AppTheme.CARD, 20, AppTheme.BORDER);
        this.accent = accent;
        setLayout(new BorderLayout(6, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 19, 17, 18));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(AppTheme.MUTED);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.PLAIN, 12.5f));
        valueLabel.setForeground(AppTheme.TEXT);
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 23f));
        add(titleLabel, BorderLayout.NORTH);
        add(valueLabel, BorderLayout.CENTER);
    }

    public void setValue(String value) { valueLabel.setText(value); }

    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(accent);
        g.fill(new RoundRectangle2D.Float(0, 14, 4, Math.max(20, getHeight() - 28), 4, 4));
        g.dispose();
    }
}
