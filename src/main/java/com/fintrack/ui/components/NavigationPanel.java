package com.fintrack.ui.components;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.function.Consumer;

public class NavigationPanel extends JPanel {
    public NavigationPanel(Consumer<String> onNavigate) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(190, 0));
        setBorder(BorderFactory.createEmptyBorder(18, 12, 18, 12));
        setBackground(new Color(29, 39, 54));
        JLabel brand = new JLabel("FinTrack"); brand.setForeground(Color.WHITE); brand.setFont(brand.getFont().deriveFont(Font.BOLD, 23f));
        brand.setAlignmentX(LEFT_ALIGNMENT); add(brand); add(Box.createVerticalStrut(26));
        addButton("Dashboard", "dashboard", onNavigate);
        addButton("Transactions", "transactions", onNavigate);
        addButton("Categories", "categories", onNavigate);
        addButton("Accounts", "accounts", onNavigate);
        addButton("Budgets", "budgets", onNavigate);
        addButton("Reports", "reports", onNavigate);
        add(Box.createVerticalGlue());
        JLabel footer = new JLabel("Personal finance manager"); footer.setForeground(new Color(190, 199, 211)); footer.setFont(footer.getFont().deriveFont(10f)); footer.setAlignmentX(LEFT_ALIGNMENT); add(footer);
    }
    private void addButton(String text, String key, Consumer<String> callback) {
        JButton button = new JButton(text); button.setAlignmentX(LEFT_ALIGNMENT); button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setFocusPainted(false); button.setForeground(Color.WHITE); button.setBackground(new Color(43, 55, 73)); button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        button.addActionListener(e -> callback.accept(key)); add(button); add(Box.createVerticalStrut(7));
    }
}
