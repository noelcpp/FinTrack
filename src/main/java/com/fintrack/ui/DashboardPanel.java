package com.fintrack.ui;

import com.fintrack.app.AppContext;
import com.fintrack.model.Account;
import com.fintrack.model.Budget;
import com.fintrack.model.Category;
import com.fintrack.model.FinanceTransaction;
import com.fintrack.model.enums.AccountType;
import com.fintrack.model.enums.TransactionType;
import com.fintrack.ui.components.AppTheme;
import com.fintrack.ui.components.FinancialChartPanel;
import com.fintrack.ui.components.RoundedButton;
import com.fintrack.ui.components.RoundedPanel;
import com.fintrack.ui.components.SummaryCard;
import com.fintrack.ui.dialog.TransactionDialog;
import com.fintrack.util.CurrencyFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Data-driven dashboard. Charts are added only when useful source data exists. */
public class DashboardPanel extends JPanel {
    private final AppContext context;
    private final SummaryCard income = new SummaryCard("Income this month", AppTheme.TEAL);
    private final SummaryCard expenses = new SummaryCard("Expenses this month", AppTheme.ORANGE);
    private final SummaryCard net = new SummaryCard("Net cash flow", AppTheme.PRIMARY);
    private final JLabel monthLabel = new JLabel();
    private final JPanel page = new JPanel();
    private final JPanel chartGrid = new JPanel(new java.awt.GridLayout(0, 2, 16, 16));

    public DashboardPanel(AppContext context) {
        this.context = context;
        setLayout(new BorderLayout());
        setBackground(AppTheme.BACKGROUND);
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setBackground(AppTheme.BACKGROUND);
        page.setBorder(BorderFactory.createEmptyBorder(24, 26, 30, 26));

        JPanel heading = new JPanel(new BorderLayout(12, 8));
        heading.setOpaque(false);
        JLabel title = new JLabel("Dashboard");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        title.setForeground(AppTheme.TEXT);
        monthLabel.setForeground(AppTheme.MUTED);
        monthLabel.setFont(monthLabel.getFont().deriveFont(Font.PLAIN, 12f));
        heading.add(title, BorderLayout.WEST);
        heading.add(monthLabel, BorderLayout.EAST);
        heading.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        page.add(heading);
        page.add(Box.createVerticalStrut(18));

        JPanel cards = new JPanel(new java.awt.GridLayout(1, 3, 16, 16));
        cards.setOpaque(false);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 122));
        cards.setPreferredSize(new Dimension(1000, 122));
        cards.add(income); cards.add(expenses); cards.add(net);
        page.add(cards);
        page.add(Box.createVerticalStrut(27));

        JLabel chartTitle = new JLabel("Your financial overview");
        chartTitle.setFont(chartTitle.getFont().deriveFont(Font.BOLD, 16f));
        chartTitle.setForeground(AppTheme.TEXT);
        chartTitle.setAlignmentX(LEFT_ALIGNMENT);
        page.add(chartTitle);
        page.add(Box.createVerticalStrut(12));

        chartGrid.setOpaque(false);
        chartGrid.setAlignmentX(LEFT_ALIGNMENT);
        page.add(chartGrid);

        JScrollPane scroll = new JScrollPane(page);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(AppTheme.BACKGROUND);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        add(scroll, BorderLayout.CENTER);
        refreshData();
    }

    public void refreshData() {
        YearMonth current = YearMonth.now();
        monthLabel.setText(current.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        List<FinanceTransaction> transactions = context.getTransactionService().findAll();
        List<Category> categories = context.getCategoryService().findAll();
        List<Account> accounts = context.getAccountService().findAll();
        List<Budget> budgets = context.getBudgetService().findAll();

        BigDecimal monthIncome = BigDecimal.ZERO;
        BigDecimal monthExpense = BigDecimal.ZERO;
        for (FinanceTransaction tx : transactions) {
            if (!YearMonth.from(tx.getDate()).equals(current)) continue;
            if (tx.getType() == TransactionType.INCOME) monthIncome = monthIncome.add(tx.getAmount());
            else if (tx.getType() == TransactionType.EXPENSE) monthExpense = monthExpense.add(tx.getAmount());
        }
        income.setValue(CurrencyFormatter.format(monthIncome));
        expenses.setValue(CurrencyFormatter.format(monthExpense));
        net.setValue(CurrencyFormatter.format(monthIncome.subtract(monthExpense)));

        chartGrid.removeAll();
        Map<Long, String> categoryNames = new HashMap<>();
        for (Category c : categories) categoryNames.put(c.getId(), c.getName());

        if (!transactions.isEmpty()) {
            Map<String, Double> spending = new LinkedHashMap<>();
            for (FinanceTransaction tx : transactions) {
                if (tx.getType() != TransactionType.EXPENSE) continue;
                String category = tx.getCategoryId() == null ? "Uncategorised" : categoryNames.getOrDefault(tx.getCategoryId(), "Other");
                spending.merge(category, tx.getAmount().doubleValue(), Double::sum);
            }
            if (!spending.isEmpty()) {
                String[] names = spending.keySet().toArray(String[]::new);
                double[] values = spending.values().stream().mapToDouble(Double::doubleValue).toArray();
                chartGrid.add(chart("Expense breakdown by category", FinancialChartPanel.Type.DONUT, names, new double[][]{values}, new String[0]));
            }

            YearMonth[] periods = new YearMonth[6];
            String[] periodLabels = new String[6];
            double[] monthIncomes = new double[6];
            double[] monthExpenses = new double[6];
            double[] monthlySavings = new double[6];
            for (int i = 0; i < 6; i++) {
                periods[i] = current.minusMonths(5 - i);
                periodLabels[i] = periods[i].format(DateTimeFormatter.ofPattern("MMM"));
            }
            for (FinanceTransaction tx : transactions) {
                YearMonth txMonth = YearMonth.from(tx.getDate());
                for (int i = 0; i < periods.length; i++) {
                    if (periods[i].equals(txMonth)) {
                        if (tx.getType() == TransactionType.INCOME) monthIncomes[i] += tx.getAmount().doubleValue();
                        else if (tx.getType() == TransactionType.EXPENSE) monthExpenses[i] += tx.getAmount().doubleValue();
                    }
                }
            }
            for (int i = 0; i < 6; i++) monthlySavings[i] = monthIncomes[i] - monthExpenses[i];
            chartGrid.add(chart("Income vs. expenses · 6 months", FinancialChartPanel.Type.LINE,
                    periodLabels, new double[][]{monthIncomes, monthExpenses}, new String[]{"Income", "Expenses"}));
            chartGrid.add(chart("Monthly savings comparison", FinancialChartPanel.Type.BAR,
                    periodLabels, new double[][]{monthlySavings}, new String[]{"Savings"}));

            if (!accounts.isEmpty()) {
                chartGrid.add(chart("Estimated net worth trend", FinancialChartPanel.Type.AREA,
                        periodLabels, new double[][]{estimateNetWorth(periods, transactions, accounts)}, new String[]{"Net worth"}));
            }

            List<Budget> currentBudgets = budgets.stream().filter(b -> b.getMonth().equals(current)).toList();
            if (!currentBudgets.isEmpty()) {
                List<String> budgetLabels = new ArrayList<>();
                double[] actual = new double[currentBudgets.size()];
                double[] remaining = new double[currentBudgets.size()];
                double[] over = new double[currentBudgets.size()];
                for (int i = 0; i < currentBudgets.size(); i++) {
                    Budget b = currentBudgets.get(i);
                    String name = categoryNames.getOrDefault(b.getCategoryId(), "Category " + b.getCategoryId());
                    budgetLabels.add(name.length() > 12 ? name.substring(0, 11) + "…" : name);
                    double limit = b.getLimitAmount().doubleValue();
                    double spent = context.getBudgetService().spentFor(b).doubleValue();
                    actual[i] = Math.min(spent, limit);
                    remaining[i] = Math.max(0, limit - spent);
                    over[i] = Math.max(0, spent - limit);
                }
                chartGrid.add(chart("Budget vs. actual · " + current, FinancialChartPanel.Type.STACKED_BAR,
                        budgetLabels.toArray(String[]::new), new double[][]{actual, remaining, over},
                        new String[]{"Spent", "Remaining", "Over budget"}));
            }

            if (budgets.stream().anyMatch(b -> b.getMonth().equals(current)) && !accounts.isEmpty()) {
                double[] radar = healthMetrics(transactions, budgets, accounts, current);
                chartGrid.add(chart("Financial health snapshot", FinancialChartPanel.Type.RADAR,
                        new String[]{"Savings rate", "Budget control", "Cash reserve", "Debt load", "Spending balance"},
                        new double[][]{radar}, new String[]{"Score"}));
            }
        }

        if (chartGrid.getComponentCount() == 0) {
            RoundedPanel empty = new RoundedPanel(AppTheme.CARD, 20, AppTheme.BORDER);
            empty.setLayout(new BorderLayout(12, 12));
            empty.setBorder(BorderFactory.createEmptyBorder(24, 26, 24, 26));
            JLabel emptyTitle = new JLabel("Your charts will appear here");
            emptyTitle.setFont(emptyTitle.getFont().deriveFont(Font.BOLD, 16f));
            emptyTitle.setForeground(AppTheme.TEXT);
            JLabel emptyText = new JLabel("Add a transaction to start seeing spending patterns, savings trends and account estimates.");
            emptyText.setForeground(AppTheme.MUTED);
            RoundedButton add = new RoundedButton("Add transaction");
            add.addActionListener(e -> TransactionDialog.open(this, context.getTransactionService(), context.getCategoryService(), context.getAccountService(), this::refreshData));
            JPanel message = new JPanel(); message.setOpaque(false); message.setLayout(new BoxLayout(message, BoxLayout.Y_AXIS));
            emptyTitle.setAlignmentX(LEFT_ALIGNMENT); emptyText.setAlignmentX(LEFT_ALIGNMENT); add.setAlignmentX(LEFT_ALIGNMENT);
            message.add(emptyTitle); message.add(Box.createVerticalStrut(8)); message.add(emptyText); message.add(Box.createVerticalStrut(16)); message.add(add);
            empty.add(message, BorderLayout.CENTER);
            empty.setPreferredSize(new Dimension(700, 150));
            chartGrid.add(empty);
        }
        revalidate(); repaint();
    }

    private FinancialChartPanel chart(String title, FinancialChartPanel.Type type, String[] labels, double[][] data, String[] names) {
        return new FinancialChartPanel(title, type, labels, data, names);
    }

    private double[] estimateNetWorth(YearMonth[] periods, List<FinanceTransaction> transactions, List<Account> accounts) {
        Map<Long, Account> byId = new HashMap<>();
        double base = 0;
        for (Account a : accounts) {
            byId.put(a.getId(), a);
            base += a.getType() == AccountType.CREDIT_CARD ? -a.getOpeningBalance().doubleValue() : a.getOpeningBalance().doubleValue();
        }
        YearMonth first = periods[0];
        for (FinanceTransaction tx : transactions) {
            Account a = byId.get(tx.getAccountId());
            if (a == null || !YearMonth.from(tx.getDate()).isBefore(first)) continue;
            base += netWorthDelta(tx, a);
        }
        double[] trend = new double[periods.length];
        double running = base;
        for (int i = 0; i < periods.length; i++) {
            for (FinanceTransaction tx : transactions) {
                if (!YearMonth.from(tx.getDate()).equals(periods[i])) continue;
                Account a = byId.get(tx.getAccountId());
                if (a != null) running += netWorthDelta(tx, a);
            }
            trend[i] = running;
        }
        return trend;
    }

    private double netWorthDelta(FinanceTransaction tx, Account account) {
        double amount = tx.getAmount().doubleValue();
        boolean income = tx.getType() == TransactionType.INCOME;
        if (account.getType() == AccountType.CREDIT_CARD) return income ? amount : -amount;
        return income ? amount : -amount;
    }

    private double[] healthMetrics(List<FinanceTransaction> transactions, List<Budget> budgets, List<Account> accounts, YearMonth current) {
        YearMonth start = current.minusMonths(5);
        double incomeTotal = 0, expenseTotal = 0;
        double[] monthlyExpense = new double[6];
        Map<Long, Account> byId = new HashMap<>();
        for (Account a : accounts) byId.put(a.getId(), a);
        double liquidity = 0, creditDebt = 0;
        for (Account account : accounts) {
            double bal = account.getOpeningBalance().doubleValue();
            for (FinanceTransaction tx : transactions) {
                if (tx.getAccountId() == null || !tx.getAccountId().equals(account.getId())) continue;
                double amount = tx.getAmount().doubleValue();
                if (account.getType() == AccountType.CREDIT_CARD) bal += tx.getType() == TransactionType.EXPENSE ? amount : -amount;
                else bal += tx.getType() == TransactionType.INCOME ? amount : -amount;
            }
            if (account.getType() == AccountType.CREDIT_CARD) creditDebt += Math.max(0, bal);
            else liquidity += bal;
        }
        for (FinanceTransaction tx : transactions) {
            YearMonth m = YearMonth.from(tx.getDate());
            if (m.isBefore(start) || m.isAfter(current)) continue;
            if (tx.getType() == TransactionType.INCOME) incomeTotal += tx.getAmount().doubleValue();
            else {
                expenseTotal += tx.getAmount().doubleValue();
                int idx = (int) java.time.temporal.ChronoUnit.MONTHS.between(start, m);
                if (idx >= 0 && idx < 6) monthlyExpense[idx] += tx.getAmount().doubleValue();
            }
        }
        double savingsRate = incomeTotal <= 0 ? 0 : clamp((incomeTotal - expenseTotal) / incomeTotal * 100);
        int onTrack = 0;
        for (Budget b : budgets) {
            if (!b.getMonth().equals(current)) continue;
            if (context.getBudgetService().spentFor(b).compareTo(b.getLimitAmount()) <= 0) onTrack++;
        }
        long currentBudgetCount = budgets.stream().filter(b -> b.getMonth().equals(current)).count();
        double budgetControl = currentBudgetCount == 0 ? 0 : onTrack * 100.0 / currentBudgetCount;
        double avgExpense = expenseTotal / 6.0;
        double reserve = avgExpense <= 0 ? 0 : clamp(liquidity / avgExpense / 6.0 * 100);
        double avgMonthlyIncome = incomeTotal / 6.0;
        double debtLoad = avgMonthlyIncome <= 0 ? (creditDebt <= 0 ? 100 : 0) : clamp(100 - creditDebt / avgMonthlyIncome * 100);
        double mean = 0;
        for (double v : monthlyExpense) mean += v;
        mean /= monthlyExpense.length;
        double variance = 0;
        for (double v : monthlyExpense) variance += (v - mean) * (v - mean);
        variance /= monthlyExpense.length;
        double spendingBalance = mean <= 0 ? 0 : clamp(100 / (1 + Math.sqrt(variance) / mean));
        return new double[]{savingsRate, budgetControl, reserve, debtLoad, spendingBalance};
    }

    private double clamp(double value) { return Math.max(0, Math.min(100, value)); }
}
