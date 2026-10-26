package com.fintrack.ui;

import com.fintrack.app.AppContext;
import com.fintrack.ui.components.AppTheme;
import com.fintrack.ui.components.NavigationPanel;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;

public class MainFrame extends JFrame {
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final NavigationPanel navigation;
    private final DashboardPanel dashboard;
    private final TransactionPanel transactions;
    private final CategoryPanel categories;
    private final AccountPanel accounts;
    private final BudgetPanel budgets;
    private final ReportsPanel reports;

    public MainFrame(AppContext context, String username, Runnable onLogout) {
        super("FinTrack — Personal Finance Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 680));
        setSize(1360, 860);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppTheme.BACKGROUND);

        dashboard = new DashboardPanel(context);
        transactions = new TransactionPanel(context);
        categories = new CategoryPanel(context);
        accounts = new AccountPanel(context);
        budgets = new BudgetPanel(context);
        reports = new ReportsPanel(context);

        content.setBackground(AppTheme.BACKGROUND);
        content.add(dashboard, "dashboard");
        content.add(transactions, "transactions");
        content.add(categories, "categories");
        content.add(accounts, "accounts");
        content.add(budgets, "budgets");
        content.add(reports, "reports");

        setLayout(new BorderLayout());
        navigation = new NavigationPanel(username, this::showPage, () -> {
            dispose();
            onLogout.run();
        });
        add(navigation, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
        showPage("dashboard");
    }

    private void showPage(String page) {
        switch (page) {
            case "dashboard" -> dashboard.refreshData();
            case "transactions" -> transactions.refreshData();
            case "categories" -> categories.refreshData();
            case "accounts" -> accounts.refreshData();
            case "budgets" -> budgets.refreshData();
            case "reports" -> reports.refreshData();
            default -> { }
        }
        cards.show(content, page);
        navigation.setActive(page);
    }
}
