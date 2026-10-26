package com.fintrack.ui.components;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Font;

public class SummaryCard extends JPanel {
    private final JLabel valueLabel = new JLabel("₹0.00");
    public SummaryCard(String title) {
        setLayout(new BorderLayout(6, 12));
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new java.awt.Color(220, 224, 230)), BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.PLAIN, 13f));
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 20f));
        add(titleLabel, BorderLayout.NORTH); add(valueLabel, BorderLayout.CENTER);
    }
    public void setValue(String value) { valueLabel.setText(value); }
}
