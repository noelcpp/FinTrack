package com.fintrack.ui;

import com.fintrack.auth.UserStore;
import com.fintrack.ui.components.AppTheme;
import com.fintrack.ui.components.RoundedButton;
import com.fintrack.ui.components.RoundedPanel;
import com.fintrack.ui.components.RoundedLineBorder;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.function.Consumer;

/** Local username/password sign-in and account creation. */
public final class LoginFrame extends JFrame {
    private final UserStore userStore;
    private final Consumer<String> onAuthenticated;
    private boolean createMode;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel heading;
    private JLabel description;
    private RoundedButton submitButton;
    private JButton switchModeButton;
    private JLabel hint;

    public LoginFrame(UserStore userStore, Consumer<String> onAuthenticated) {
        super("FinTrack — Sign in");
        this.userStore = userStore;
        this.onAuthenticated = onAuthenticated;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(520, 590));
        setSize(560, 650);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppTheme.BACKGROUND);
        setContentPane(buildContent());
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new GridLayout(1, 1));
        root.setBackground(AppTheme.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(26, 30, 26, 30));

        RoundedPanel card = new RoundedPanel(AppTheme.CARD, 26, AppTheme.BORDER);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(32, 38, 30, 38));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel brand = new JLabel("FinTrack");
        brand.setForeground(AppTheme.PRIMARY);
        brand.setFont(brand.getFont().deriveFont(Font.BOLD, 31f));
        brand.setAlignmentX(LEFT_ALIGNMENT);
        content.add(brand);
        content.add(Box.createVerticalStrut(22));

        heading = new JLabel();
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 22f));
        heading.setForeground(AppTheme.TEXT);
        heading.setAlignmentX(LEFT_ALIGNMENT);
        content.add(heading);
        content.add(Box.createVerticalStrut(7));

        description = new JLabel();
        description.setForeground(AppTheme.MUTED);
        description.setAlignmentX(LEFT_ALIGNMENT);
        content.add(description);
        content.add(Box.createVerticalStrut(24));

        JLabel usernameLabel = fieldLabel("Username");
        content.add(usernameLabel);
        content.add(Box.createVerticalStrut(7));
        usernameField = new JTextField();
        styleField(usernameField);
        content.add(usernameField);
        content.add(Box.createVerticalStrut(17));

        JLabel passwordLabel = fieldLabel("Password");
        content.add(passwordLabel);
        content.add(Box.createVerticalStrut(7));
        passwordField = new JPasswordField();
        styleField(passwordField);
        content.add(passwordField);
        content.add(Box.createVerticalStrut(19));

        hint = new JLabel(" ");
        hint.setForeground(AppTheme.MUTED);
        hint.setFont(hint.getFont().deriveFont(11f));
        hint.setAlignmentX(LEFT_ALIGNMENT);
        content.add(hint);
        content.add(Box.createVerticalStrut(12));

        submitButton = new RoundedButton("Sign in");
        submitButton.setAlignmentX(LEFT_ALIGNMENT);
        submitButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        submitButton.setPreferredSize(new Dimension(380, 46));
        submitButton.addActionListener(e -> submit());
        content.add(submitButton);
        content.add(Box.createVerticalStrut(17));

        switchModeButton = new JButton();
        switchModeButton.setContentAreaFilled(false);
        switchModeButton.setBorderPainted(false);
        switchModeButton.setFocusPainted(false);
        switchModeButton.setForeground(AppTheme.PRIMARY);
        switchModeButton.setFont(switchModeButton.getFont().deriveFont(Font.BOLD, 12f));
        switchModeButton.setAlignmentX(LEFT_ALIGNMENT);
        switchModeButton.addActionListener(e -> { createMode = !createMode; updateMode(); });
        content.add(switchModeButton);
        content.add(Box.createVerticalGlue());

        JLabel localNote = new JLabel("Local-only accounts · each user has a separate data file");
        localNote.setFont(localNote.getFont().deriveFont(10.5f));
        localNote.setForeground(AppTheme.MUTED);
        localNote.setAlignmentX(LEFT_ALIGNMENT);
        content.add(Box.createVerticalStrut(20));
        content.add(localNote);

        card.add(content, BorderLayout.CENTER);
        root.add(card);
        updateMode();
        getRootPane().setDefaultButton(submitButton);
        return root;
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(AppTheme.TEXT);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 12f));
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    private void styleField(JTextField field) {
        field.setFont(field.getFont().deriveFont(14f));
        field.setForeground(AppTheme.TEXT);
        field.setBackground(AppTheme.CARD);
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(AppTheme.BORDER, 16),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        field.setAlignmentX(LEFT_ALIGNMENT);
    }

    private void updateMode() {
        if (heading == null) return;
        heading.setText(createMode ? "Create your account" : "Welcome back");
        description.setText(createMode ? "Start with a separate local finance profile." : "Sign in to open your personal finance workspace.");
        submitButton.setText(createMode ? "Create account" : "Sign in");
        switchModeButton.setText(createMode ? "Already have an account? Sign in" : "New to FinTrack? Create an account");
        hint.setText(createMode ? "Username: 3–32 characters. Password: at least 8 characters." : " ");
        setTitle(createMode ? "FinTrack — Create account" : "FinTrack — Sign in");
        revalidate(); repaint();
    }

    private void submit() {
        String username = usernameField.getText().trim();
        char[] password = passwordField.getPassword();
        try {
            if (createMode) {
                userStore.register(username, password);
                username = username.trim().toLowerCase(java.util.Locale.ROOT);
                JOptionPane.showMessageDialog(this, "Account created. You are now signed in.", "FinTrack", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                onAuthenticated.accept(username);
            } else {
                boolean valid = userStore.authenticate(username, password);
                if (!valid) {
                    JOptionPane.showMessageDialog(this, "Username or password is incorrect.", "Sign-in failed", JOptionPane.ERROR_MESSAGE);
                    passwordField.setText("");
                    passwordField.requestFocusInWindow();
                    return;
                }
                dispose();
                onAuthenticated.accept(username.trim().toLowerCase(java.util.Locale.ROOT));
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), createMode ? "Could not create account" : "Sign-in failed", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "FinTrack could not access its local user store.\n" + ex.getMessage(), "Storage error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Arrays.fill(password, '\0');
        }
    }
}
