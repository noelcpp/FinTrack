package com.fintrack.ui.components;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class NavigationPanel extends JPanel {
    private final Map<String, RoundedButton> buttons = new LinkedHashMap<>();
    private final Color sidebarText = new Color(198, 209, 228);

    public NavigationPanel(String username, Consumer<String> onNavigate, Runnable onLogout) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(218, 0));
        setBorder(BorderFactory.createEmptyBorder(24, 15, 18, 15));
        setBackground(AppTheme.SIDEBAR);

        JLabel brand = new JLabel("FinTrack");
        brand.setForeground(Color.WHITE);
        brand.setFont(brand.getFont().deriveFont(Font.BOLD, 25f));
        brand.setAlignmentX(LEFT_ALIGNMENT);
        add(brand);

        JLabel subtitle = new JLabel("PERSONAL FINANCE");
        subtitle.setForeground(new Color(127, 145, 173));
        subtitle.setFont(subtitle.getFont().deriveFont(Font.BOLD, 9.5f));
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        add(Box.createVerticalStrut(5));
        add(subtitle);
        add(Box.createVerticalStrut(32));

        addButton("Dashboard", "dashboard", onNavigate);
        addButton("Transactions", "transactions", onNavigate);
        addButton("Categories", "categories", onNavigate);
        addButton("Accounts", "accounts", onNavigate);
        addButton("Budgets", "budgets", onNavigate);
        addButton("Reports & insights", "reports", onNavigate);

        add(Box.createVerticalGlue());
        JPanel userCard = new RoundedPanel(new Color(25, 38, 59), 16);
        userCard.setLayout(new BoxLayout(userCard, BoxLayout.Y_AXIS));
        userCard.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        userCard.setAlignmentX(LEFT_ALIGNMENT);
        userCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        JLabel signedIn = new JLabel("SIGNED IN AS");
        signedIn.setFont(signedIn.getFont().deriveFont(Font.BOLD, 9f));
        signedIn.setForeground(new Color(127, 145, 173));
        JLabel user = new JLabel(username);
        user.setForeground(Color.WHITE);
        user.setFont(user.getFont().deriveFont(Font.BOLD, 12f));
        user.setToolTipText(username);
        userCard.add(signedIn); userCard.add(Box.createVerticalStrut(5)); userCard.add(user);
        add(userCard);
        add(Box.createVerticalStrut(10));
        RoundedButton logout = new RoundedButton("Log out", RoundedButton.Variant.NAV);
        logout.setAlignmentX(LEFT_ALIGNMENT);
        logout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        logout.addActionListener(e -> onLogout.run());
        add(logout);
        add(Box.createVerticalStrut(5));
        JLabel footer = new JLabel("Local data · Private profile");
        footer.setForeground(new Color(127, 145, 173));
        footer.setFont(footer.getFont().deriveFont(9.5f));
        footer.setAlignmentX(LEFT_ALIGNMENT);
        add(footer);
        setActive("dashboard");
    }

    private void addButton(String text, String key, Consumer<String> callback) {
        RoundedButton button = new RoundedButton(text, RoundedButton.Variant.NAV);
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 43));
        button.setPreferredSize(new Dimension(188, 43));
        button.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 10));
        button.addActionListener(e -> {
            setActive(key);
            callback.accept(key);
        });
        buttons.put(key, button);
        add(button);
        add(Box.createVerticalStrut(6));
    }

    public void setActive(String key) {
        for (Map.Entry<String, RoundedButton> entry : buttons.entrySet()) {
            entry.getValue().setActive(entry.getKey().equals(key));
            entry.getValue().setForeground(entry.getKey().equals(key) ? Color.WHITE : sidebarText);
        }
    }
}
