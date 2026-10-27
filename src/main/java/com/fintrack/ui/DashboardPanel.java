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
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Dashboard combining monthly summaries, recent activity, and data-backed charts. */
public class DashboardPanel extends JPanel {
    private final AppContext context;
    private final Runnable openTransactions;
    private final SummaryCard income = new SummaryCard("Income this month", AppTheme.TEAL);
    private final SummaryCard expenses = new SummaryCard("Expenses this month", AppTheme.RED);
    private final SummaryCard net = new SummaryCard("Net cash flow", AppTheme.PRIMARY);
    private final JLabel monthLabel = new JLabel();
    private final JPanel page = new JPanel();
    private final JPanel chartGrid = new JPanel(new java.awt.GridLayout(0, 2, 16, 16));
    private final JPanel recentRows = new JPanel();
    private final JTextArea quickInsights = new JTextArea();
    private final RoundedPanel recentCard = new RoundedPanel(AppTheme.CARD, 20, AppTheme.BORDER);
    private final RoundedPanel insightCard = new RoundedPanel(AppTheme.CARD, 20, AppTheme.BORDER);

    public DashboardPanel(AppContext context) {
        this(context, () -> { });
    }

    public DashboardPanel(AppContext context, Runnable openTransactions) {
        this.context = context;
        this.openTransactions = openTransactions == null ? () -> { } : openTransactions;
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
        heading.setAlignmentX(LEFT_ALIGNMENT);
        page.add(heading);
        page.add(Box.createVerticalStrut(18));

        JPanel cards = new JPanel(new java.awt.GridLayout(1, 3, 16, 16));
        cards.setOpaque(false);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 122));
        cards.setPreferredSize(new Dimension(1000, 122));
        cards.setAlignmentX(LEFT_ALIGNMENT);
        cards.add(income);
        cards.add(expenses);
        cards.add(net);
        page.add(cards);
        page.add(Box.createVerticalStrut(22));

        JLabel overviewTitle = new JLabel("Your financial overview");
        overviewTitle.setFont(overviewTitle.getFont().deriveFont(Font.BOLD, 16f));
        overviewTitle.setForeground(AppTheme.TEXT);
        overviewTitle.setAlignmentX(LEFT_ALIGNMENT);
        page.add(overviewTitle);
        page.add(Box.createVerticalStrut(12));

        JPanel overview = new JPanel(new GridBagLayout());
        overview.setOpaque(false);
        overview.setAlignmentX(LEFT_ALIGNMENT);
        overview.setPreferredSize(new Dimension(1120, 760));
        overview.setMinimumSize(new Dimension(830, 600));
        GridBagConstraints left = new GridBagConstraints();
        left.gridx = 0; left.gridy = 0; left.weightx = 0.34; left.weighty = 1;
        left.fill = GridBagConstraints.BOTH; left.insets = new Insets(0, 0, 0, 8);
        JPanel leftColumn = new JPanel();
        leftColumn.setOpaque(false);
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        buildRecentCard();
        buildInsightCard();
        recentCard.setAlignmentX(LEFT_ALIGNMENT);
        recentCard.setPreferredSize(new Dimension(360, 420));
        recentCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 430));
        insightCard.setAlignmentX(LEFT_ALIGNMENT);
        insightCard.setPreferredSize(new Dimension(360, 270));
        insightCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        leftColumn.add(recentCard);
        leftColumn.add(Box.createVerticalStrut(16));
        leftColumn.add(insightCard);
        leftColumn.add(Box.createVerticalGlue());
        overview.add(leftColumn, left);

        GridBagConstraints right = new GridBagConstraints();
        right.gridx = 1; right.gridy = 0; right.weightx = 0.66; right.weighty = 1;
        right.fill = GridBagConstraints.BOTH; right.insets = new Insets(0, 8, 0, 0);
        chartGrid.setOpaque(false);
        chartGrid.setAlignmentX(LEFT_ALIGNMENT);
        overview.add(chartGrid, right);
        page.add(overview);

        JScrollPane scroll = new JScrollPane(page);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(AppTheme.BACKGROUND);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        add(scroll, BorderLayout.CENTER);
        refreshData();
    }

    private void buildRecentCard() {
        recentCard.setLayout(new BorderLayout(8, 12));
        recentCard.setBorder(BorderFactory.createEmptyBorder(17, 17, 15, 17));
        JPanel heading = new JPanel(new BorderLayout(8, 8));
        heading.setOpaque(false);
        JLabel title = new JLabel("Latest transactions");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 15f));
        title.setForeground(AppTheme.TEXT);
        RoundedButton viewAll = new RoundedButton("View all", RoundedButton.Variant.GHOST);
        viewAll.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        viewAll.addActionListener(e -> openTransactions.run());
        heading.add(title, BorderLayout.WEST);
        heading.add(viewAll, BorderLayout.EAST);
        recentCard.add(heading, BorderLayout.NORTH);

        recentRows.setOpaque(false);
        recentRows.setLayout(new BoxLayout(recentRows, BoxLayout.Y_AXIS));
        JScrollPane rowScroll = new JScrollPane(recentRows);
        rowScroll.setBorder(BorderFactory.createEmptyBorder());
        rowScroll.setOpaque(false);
        rowScroll.getViewport().setOpaque(false);
        rowScroll.getVerticalScrollBar().setUnitIncrement(16);
        recentCard.add(rowScroll, BorderLayout.CENTER);
    }

    private void buildInsightCard() {
        insightCard.setLayout(new BorderLayout(8, 10));
        insightCard.setBorder(BorderFactory.createEmptyBorder(17, 18, 17, 18));
        JLabel title = new JLabel("This month at a glance");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 15f));
        title.setForeground(AppTheme.TEXT);
        insightCard.add(title, BorderLayout.NORTH);
        quickInsights.setEditable(false);
        quickInsights.setFocusable(false);
        quickInsights.setLineWrap(true);
        quickInsights.setWrapStyleWord(true);
        quickInsights.setFont(new Font("SansSerif", Font.PLAIN, 12));
        quickInsights.setForeground(AppTheme.TEXT);
        quickInsights.setBackground(AppTheme.CARD);
        quickInsights.setBorder(BorderFactory.createEmptyBorder());
        insightCard.add(quickInsights, BorderLayout.CENTER);
    }

    public void refreshData() {
        YearMonth current = YearMonth.now();
        monthLabel.setText(current.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        List<FinanceTransaction> transactions = new ArrayList<>(context.getTransactionService().findAll());
        transactions.sort(Comparator.comparing(FinanceTransaction::getDate).reversed()
                .thenComparing(FinanceTransaction::getId, Comparator.nullsLast(Comparator.reverseOrder())));
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
        income.setValueColor(AppTheme.TEAL);
        expenses.setValue(CurrencyFormatter.format(monthExpense));
        expenses.setValueColor(AppTheme.RED);
        BigDecimal netValue = monthIncome.subtract(monthExpense);
        net.setValue(CurrencyFormatter.format(netValue));
        net.setValueColor(netValue.signum() >= 0 ? AppTheme.TEAL : AppTheme.RED);

        Map<Long, String> categoryNames = new HashMap<>();
        for (Category category : categories) categoryNames.put(category.getId(), category.getName());
        refreshRecentTransactions(transactions, categoryNames);
        refreshQuickInsights(transactions, current, monthIncome, monthExpense, categoryNames, budgets);

        chartGrid.removeAll();
        chartGrid.setLayout(new java.awt.GridLayout(0, 2, 16, 16));

        Map<String, Double> currentMonthSpending = new LinkedHashMap<>();
        for (FinanceTransaction tx : transactions) {
            if (tx.getType() != TransactionType.EXPENSE || !YearMonth.from(tx.getDate()).equals(current)) continue;
            String category = tx.getCategoryId() == null ? "Uncategorised" : categoryNames.getOrDefault(tx.getCategoryId(), "Other");
            currentMonthSpending.merge(category, tx.getAmount().doubleValue(), Double::sum);
        }
        if (!currentMonthSpending.isEmpty()) {
            String[] names = currentMonthSpending.keySet().toArray(String[]::new);
            double[] values = currentMonthSpending.values().stream().mapToDouble(Double::doubleValue).toArray();
            chartGrid.add(chart("Expense breakdown · this month", FinancialChartPanel.Type.DONUT,
                    names, new double[][]{values}, new String[0]));
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
                if (!periods[i].equals(txMonth)) continue;
                if (tx.getType() == TransactionType.INCOME) monthIncomes[i] += tx.getAmount().doubleValue();
                else if (tx.getType() == TransactionType.EXPENSE) monthExpenses[i] += tx.getAmount().doubleValue();
            }
        }
        boolean hasSixMonthActivity = false;
        for (int i = 0; i < 6; i++) {
            monthlySavings[i] = monthIncomes[i] - monthExpenses[i];
            if (monthIncomes[i] != 0 || monthExpenses[i] != 0) hasSixMonthActivity = true;
        }
        if (hasSixMonthActivity) {
            chartGrid.add(chart("Income vs. expenses · 6 months", FinancialChartPanel.Type.LINE,
                    periodLabels, new double[][]{monthIncomes, monthExpenses}, new String[]{"Income", "Expenses"}));
            chartGrid.add(chart("Monthly savings comparison", FinancialChartPanel.Type.BAR,
                    periodLabels, new double[][]{monthlySavings}, new String[]{"Savings"}));
        }

        if (!accounts.isEmpty() && (!transactions.isEmpty() || accounts.stream().anyMatch(a -> a.getOpeningBalance().signum() != 0))) {
            chartGrid.add(chart("Estimated net worth trend · 6 months", FinancialChartPanel.Type.AREA,
                    periodLabels, new double[][]{estimateNetWorth(periods, transactions, accounts)}, new String[]{"Net worth"}));
        }

        List<Budget> currentBudgets = budgets.stream().filter(b -> b.getMonth().equals(current)).toList();
        if (!currentBudgets.isEmpty()) {
            List<String> budgetLabels = new ArrayList<>();
            double[] actual = new double[currentBudgets.size()];
            double[] remaining = new double[currentBudgets.size()];
            double[] over = new double[currentBudgets.size()];
            for (int i = 0; i < currentBudgets.size(); i++) {
                Budget budget = currentBudgets.get(i);
                String name = categoryNames.getOrDefault(budget.getCategoryId(), "Category " + budget.getCategoryId());
                budgetLabels.add(name.length() > 12 ? name.substring(0, 11) + "…" : name);
                double limit = budget.getLimitAmount().doubleValue();
                double spent = context.getBudgetService().spentFor(budget).doubleValue();
                actual[i] = Math.min(spent, limit);
                remaining[i] = Math.max(0, limit - spent);
                over[i] = Math.max(0, spent - limit);
            }
            chartGrid.add(chart("Budget vs. actual · " + current, FinancialChartPanel.Type.STACKED_BAR,
                    budgetLabels.toArray(String[]::new), new double[][]{actual, remaining, over},
                    new String[]{"Spent", "Remaining", "Over budget"}));
        }

        if (!transactions.isEmpty() && !accounts.isEmpty()) {
            double[] radar = healthMetrics(transactions, budgets, accounts, current);
            chartGrid.add(chart("Financial health snapshot", FinancialChartPanel.Type.RADAR,
                    new String[]{"Savings rate", "Budget control", "Cash reserve", "Debt load", "Spending balance"},
                    new double[][]{radar}, new String[]{"Score"}));
        }

        if (chartGrid.getComponentCount() == 0) {
            RoundedPanel empty = new RoundedPanel(AppTheme.CARD, 20, AppTheme.BORDER);
            empty.setLayout(new BorderLayout(12, 12));
            empty.setBorder(BorderFactory.createEmptyBorder(24, 26, 24, 26));
            JLabel emptyTitle = new JLabel("Your charts will appear here");
            emptyTitle.setFont(emptyTitle.getFont().deriveFont(Font.BOLD, 16f));
            emptyTitle.setForeground(AppTheme.TEXT);
            JLabel emptyText = new JLabel("Add transactions or account balances to see spending patterns and account estimates.");
            emptyText.setForeground(AppTheme.MUTED);
            RoundedButton add = new RoundedButton("Add transaction");
            add.addActionListener(e -> TransactionDialog.open(this, context.getTransactionService(), context.getCategoryService(), context.getAccountService(), this::refreshData));
            JPanel message = new JPanel();
            message.setOpaque(false);
            message.setLayout(new BoxLayout(message, BoxLayout.Y_AXIS));
            emptyTitle.setAlignmentX(LEFT_ALIGNMENT);
            emptyText.setAlignmentX(LEFT_ALIGNMENT);
            add.setAlignmentX(LEFT_ALIGNMENT);
            message.add(emptyTitle);
            message.add(Box.createVerticalStrut(8));
            message.add(emptyText);
            message.add(Box.createVerticalStrut(16));
            message.add(add);
            empty.add(message, BorderLayout.CENTER);
            empty.setPreferredSize(new Dimension(600, 190));
            chartGrid.setLayout(new java.awt.GridLayout(1, 1, 16, 16));
            chartGrid.add(empty);
        }
        revalidate();
        repaint();
    }

    private void refreshRecentTransactions(List<FinanceTransaction> transactions, Map<Long, String> categoryNames) {
        recentRows.removeAll();
        if (transactions.isEmpty()) {
            JLabel empty = new JLabel("No transactions yet. Add your first income or expense.");
            empty.setForeground(AppTheme.MUTED);
            empty.setBorder(BorderFactory.createEmptyBorder(14, 4, 14, 4));
            recentRows.add(empty);
        } else {
            int count = Math.min(7, transactions.size());
            for (int i = 0; i < count; i++) {
                FinanceTransaction transaction = transactions.get(i);
                recentRows.add(createTransactionRow(transaction, categoryNames));
                if (i < count - 1) {
                    javax.swing.JSeparator separator = new javax.swing.JSeparator();
                    separator.setForeground(AppTheme.BORDER);
                    recentRows.add(separator);
                }
            }
        }
        revalidate();
        repaint();
    }

    private JPanel createTransactionRow(FinanceTransaction transaction, Map<Long, String> categoryNames) {
        JPanel row = new JPanel(new BorderLayout(8, 4));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(9, 3, 9, 3));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        String description = transaction.getDescription() == null || transaction.getDescription().isBlank()
                ? "Transaction" : transaction.getDescription();
        JLabel title = new JLabel(description);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 12.5f));
        title.setForeground(AppTheme.TEXT);
        String category = transaction.getCategoryId() == null ? "Uncategorised"
                : categoryNames.getOrDefault(transaction.getCategoryId(), "Other");
        JLabel detail = new JLabel(transaction.getDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) + " · " + category);
        detail.setFont(detail.getFont().deriveFont(Font.PLAIN, 10.5f));
        detail.setForeground(AppTheme.MUTED);
        JPanel labels = new JPanel();
        labels.setOpaque(false);
        labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));
        labels.add(title);
        labels.add(Box.createVerticalStrut(4));
        labels.add(detail);
        JLabel amount = new JLabel((transaction.getType() == TransactionType.INCOME ? "+ " : "− ")
                + CurrencyFormatter.format(transaction.getAmount()), SwingConstants.RIGHT);
        amount.setFont(amount.getFont().deriveFont(Font.BOLD, 12f));
        amount.setForeground(transaction.getType() == TransactionType.INCOME ? AppTheme.TEAL : AppTheme.RED);
        amount.setToolTipText("Open transactions");
        row.add(labels, BorderLayout.CENTER);
        row.add(amount, BorderLayout.EAST);
        MouseAdapter open = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent event) { openTransactions.run(); }
            @Override public void mouseEntered(MouseEvent event) { row.setBackground(new Color(245, 247, 251)); row.setOpaque(true); row.repaint(); }
            @Override public void mouseExited(MouseEvent event) { row.setOpaque(false); row.repaint(); }
        };
        attachMouseListener(row, open);
        return row;
    }

    private void attachMouseListener(java.awt.Component component, MouseAdapter listener) {
        component.addMouseListener(listener);
        if (component instanceof java.awt.Container container) {
            for (java.awt.Component child : container.getComponents()) attachMouseListener(child, listener);
        }
    }

    private void refreshQuickInsights(List<FinanceTransaction> transactions, YearMonth current,
                                      BigDecimal monthIncome, BigDecimal monthExpense,
                                      Map<Long, String> categoryNames, List<Budget> budgets) {
        List<String> lines = new ArrayList<>();
        BigDecimal netCash = monthIncome.subtract(monthExpense);
        if (monthIncome.signum() > 0) {
            BigDecimal savingsRate = netCash.multiply(BigDecimal.valueOf(100))
                    .divide(monthIncome, 1, java.math.RoundingMode.HALF_UP);
            lines.add("Savings rate: " + savingsRate.toPlainString() + "%");
        } else {
            lines.add("Savings rate: N/A (no income recorded)");
        }
        if (netCash.signum() >= 0) lines.add("Income exceeds expenses by " + CurrencyFormatter.format(netCash));
        else lines.add("Expenses exceed income by " + CurrencyFormatter.format(netCash.abs()));

        Map<String, BigDecimal> spending = new LinkedHashMap<>();
        for (FinanceTransaction tx : transactions) {
            if (tx.getType() != TransactionType.EXPENSE || !YearMonth.from(tx.getDate()).equals(current)) continue;
            String name = tx.getCategoryId() == null ? "Uncategorised" : categoryNames.getOrDefault(tx.getCategoryId(), "Other");
            spending.merge(name, tx.getAmount(), BigDecimal::add);
        }
        spending.entrySet().stream().max(Map.Entry.comparingByValue()).ifPresent(entry ->
                lines.add("Top expense: " + entry.getKey() + " · " + CurrencyFormatter.format(entry.getValue())));
        long overBudget = budgets.stream().filter(b -> b.getMonth().equals(current))
                .filter(b -> context.getBudgetService().spentFor(b).compareTo(b.getLimitAmount()) > 0).count();
        long monthBudgets = budgets.stream().filter(b -> b.getMonth().equals(current)).count();
        if (monthBudgets > 0) lines.add("Budgets over limit: " + overBudget + " of " + monthBudgets);
        else lines.add("No monthly budgets set yet.");
        quickInsights.setText(String.join("\n\n", lines));
        quickInsights.setCaretPosition(0);
    }

    private FinancialChartPanel chart(String title, FinancialChartPanel.Type type, String[] labels, double[][] data, String[] names) {
        return new FinancialChartPanel(title, type, labels, data, names);
    }

    private double[] estimateNetWorth(YearMonth[] periods, List<FinanceTransaction> transactions, List<Account> accounts) {
        Map<Long, Account> byId = new HashMap<>();
        double base = 0;
        for (Account account : accounts) {
            byId.put(account.getId(), account);
            base += account.getType() == AccountType.CREDIT_CARD ? -account.getOpeningBalance().doubleValue() : account.getOpeningBalance().doubleValue();
        }
        YearMonth first = periods[0];
        for (FinanceTransaction transaction : transactions) {
            Account account = byId.get(transaction.getAccountId());
            if (account == null || !YearMonth.from(transaction.getDate()).isBefore(first)) continue;
            base += netWorthDelta(transaction, account);
        }
        double[] trend = new double[periods.length];
        double running = base;
        for (int i = 0; i < periods.length; i++) {
            for (FinanceTransaction transaction : transactions) {
                if (!YearMonth.from(transaction.getDate()).equals(periods[i])) continue;
                Account account = byId.get(transaction.getAccountId());
                if (account != null) running += netWorthDelta(transaction, account);
            }
            trend[i] = running;
        }
        return trend;
    }

    private double netWorthDelta(FinanceTransaction transaction, Account account) {
        double amount = transaction.getAmount().doubleValue();
        boolean income = transaction.getType() == TransactionType.INCOME;
        return income ? amount : -amount;
    }

    private double[] healthMetrics(List<FinanceTransaction> transactions, List<Budget> budgets,
                                   List<Account> accounts, YearMonth current) {
        YearMonth start = current.minusMonths(5);
        double incomeTotal = 0, expenseTotal = 0;
        double[] monthlyExpense = new double[6];
        double liquidity = 0, creditDebt = 0;
        for (Account account : accounts) {
            double balance = account.getOpeningBalance().doubleValue();
            for (FinanceTransaction transaction : transactions) {
                if (transaction.getAccountId() == null || !transaction.getAccountId().equals(account.getId())) continue;
                double amount = transaction.getAmount().doubleValue();
                if (account.getType() == AccountType.CREDIT_CARD)
                    balance += transaction.getType() == TransactionType.EXPENSE ? amount : -amount;
                else balance += transaction.getType() == TransactionType.INCOME ? amount : -amount;
            }
            if (account.getType() == AccountType.CREDIT_CARD) creditDebt += Math.max(0, balance);
            else liquidity += balance;
        }
        for (FinanceTransaction transaction : transactions) {
            YearMonth month = YearMonth.from(transaction.getDate());
            if (month.isBefore(start) || month.isAfter(current)) continue;
            if (transaction.getType() == TransactionType.INCOME) incomeTotal += transaction.getAmount().doubleValue();
            else if (transaction.getType() == TransactionType.EXPENSE) {
                expenseTotal += transaction.getAmount().doubleValue();
                int index = (int) java.time.temporal.ChronoUnit.MONTHS.between(start, month);
                if (index >= 0 && index < 6) monthlyExpense[index] += transaction.getAmount().doubleValue();
            }
        }
        double savingsRate = incomeTotal <= 0 ? 0 : clamp((incomeTotal - expenseTotal) / incomeTotal * 100);
        int onTrack = 0;
        for (Budget budget : budgets) {
            if (!budget.getMonth().equals(current)) continue;
            if (context.getBudgetService().spentFor(budget).compareTo(budget.getLimitAmount()) <= 0) onTrack++;
        }
        long currentBudgetCount = budgets.stream().filter(b -> b.getMonth().equals(current)).count();
        double budgetControl = currentBudgetCount == 0 ? 0 : onTrack * 100.0 / currentBudgetCount;
        double averageExpense = expenseTotal / 6.0;
        double reserve = averageExpense <= 0 ? 0 : clamp(liquidity / averageExpense / 6.0 * 100);
        double averageMonthlyIncome = incomeTotal / 6.0;
        double debtLoad = averageMonthlyIncome <= 0 ? (creditDebt <= 0 ? 100 : 0)
                : clamp(100 - creditDebt / averageMonthlyIncome * 100);
        double mean = 0;
        for (double value : monthlyExpense) mean += value;
        mean /= monthlyExpense.length;
        double variance = 0;
        for (double value : monthlyExpense) variance += (value - mean) * (value - mean);
        variance /= monthlyExpense.length;
        double spendingBalance = mean <= 0 ? 0 : clamp(100 / (1 + Math.sqrt(variance) / mean));
        return new double[]{savingsRate, budgetControl, reserve, debtLoad, spendingBalance};
    }

    private double clamp(double value) { return Math.max(0, Math.min(100, value)); }
}
