package com.fintrack.ui;

import com.fintrack.app.AppContext;
import com.fintrack.ui.components.NavigationPanel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Color;

public class MainFrame extends JFrame {
    private final CardLayout cards = new CardLayout(); private final JPanel content = new JPanel(cards);
    private final DashboardPanel dashboard; private final TransactionPanel transactions; private final CategoryPanel categories; private final AccountPanel accounts; private final BudgetPanel budgets; private final ReportsPanel reports;
    public MainFrame(AppContext context) {
        super("FinTrack — Personal Finance Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setMinimumSize(new Dimension(900, 600)); setSize(1120, 720); setLocationRelativeTo(null); getContentPane().setBackground(Color.WHITE);
        dashboard = new DashboardPanel(context.getDashboardService()); transactions = new TransactionPanel(context); categories = new CategoryPanel(context); accounts = new AccountPanel(context); budgets = new BudgetPanel(context); reports = new ReportsPanel(context);
        content.add(dashboard,"dashboard"); content.add(transactions,"transactions"); content.add(categories,"categories"); content.add(accounts,"accounts"); content.add(budgets,"budgets"); content.add(reports,"reports");
        setLayout(new BorderLayout()); add(new NavigationPanel(this::showPage),BorderLayout.WEST); add(content,BorderLayout.CENTER);
    }
    private void showPage(String page) {
        switch(page) {
            case "dashboard" -> dashboard.refreshData(); case "transactions" -> transactions.refreshData(); case "categories" -> categories.refreshData();
            case "accounts" -> accounts.refreshData(); case "budgets" -> budgets.refreshData(); case "reports" -> reports.refreshData(); default -> { }
        }
        cards.show(content,page);
    }
}
