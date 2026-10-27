package com.fintrack.ui;

import com.fintrack.app.AppContext;
import com.fintrack.model.Budget;
import com.fintrack.model.Category;
import com.fintrack.model.FinanceTransaction;
import com.fintrack.model.enums.TransactionType;
import com.fintrack.service.ReportService;
import com.fintrack.ui.components.AppTheme;
import com.fintrack.ui.components.FinancialChartPanel;
import com.fintrack.ui.components.RoundedPanel;
import com.fintrack.ui.components.SummaryCard;
import com.fintrack.util.CurrencyFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Date-filterable, data-driven financial reports and plain-language insights. */
public class ReportsPanel extends JPanel {
    private enum PeriodMode {
        THIS_MONTH("This month"), SPECIFIC_MONTH("Specific month"), THIS_YEAR("This year");
        private final String label;
        PeriodMode(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }

    private record DateRange(LocalDate start, LocalDate end, String label, boolean wholeYear) { }

    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MMMM yyyy");
    private final AppContext context;
    private final JComboBox<PeriodMode> periodSelector = new JComboBox<>(PeriodMode.values());
    private final JComboBox<YearMonth> monthPicker = new JComboBox<>();
    private final SummaryCard incomeCard = new SummaryCard("Income", AppTheme.TEAL);
    private final SummaryCard expenseCard = new SummaryCard("Expenses", AppTheme.RED);
    private final SummaryCard netCard = new SummaryCard("Net cash flow", AppTheme.PRIMARY);
    private final SummaryCard savingsCard = new SummaryCard("Savings rate", new java.awt.Color(151, 103, 216));
    private final JTextArea insightsText = new JTextArea();
    private final JPanel charts = new JPanel(new java.awt.GridLayout(0, 2, 16, 16));

    public ReportsPanel(AppContext context) {
        this.context = context;
        setLayout(new BorderLayout());
        setBackground(AppTheme.BACKGROUND);

        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setBackground(AppTheme.BACKGROUND);
        page.setBorder(BorderFactory.createEmptyBorder(24, 26, 30, 26));

        JLabel title = new JLabel("Reports & insights");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        title.setForeground(AppTheme.TEXT);
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel subtitle = new JLabel("Understand where your money is going.");
        subtitle.setForeground(AppTheme.MUTED);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        page.add(title);
        page.add(Box.createVerticalStrut(5));
        page.add(subtitle);
        page.add(Box.createVerticalStrut(16));

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filters.setOpaque(false);
        filters.setAlignmentX(LEFT_ALIGNMENT);
        JLabel periodLabel = new JLabel("Report period");
        periodLabel.setForeground(AppTheme.TEXT);
        periodLabel.setFont(periodLabel.getFont().deriveFont(Font.BOLD, 12f));
        filters.add(periodLabel);
        filters.add(periodSelector);
        filters.add(new JLabel("Month"));
        populateMonthPicker();
        monthPicker.setEnabled(false);
        monthPicker.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(javax.swing.JList<?> list, Object value,
                                                                     int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof YearMonth month) label.setText(month.format(MONTH_FORMAT));
                return label;
            }
        });
        filters.add(monthPicker);
        page.add(filters);
        page.add(Box.createVerticalStrut(12));

        JPanel metricCards = new JPanel(new java.awt.GridLayout(1, 4, 14, 14));
        metricCards.setOpaque(false);
        metricCards.setAlignmentX(LEFT_ALIGNMENT);
        metricCards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 118));
        metricCards.add(incomeCard);
        metricCards.add(expenseCard);
        metricCards.add(netCard);
        metricCards.add(savingsCard);
        page.add(metricCards);
        page.add(Box.createVerticalStrut(16));

        RoundedPanel insightCard = new RoundedPanel(AppTheme.CARD, 20, AppTheme.BORDER);
        insightCard.setLayout(new BorderLayout(8, 10));
        insightCard.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        JLabel insightHeading = new JLabel("What this period says");
        insightHeading.setFont(insightHeading.getFont().deriveFont(Font.BOLD, 15f));
        insightHeading.setForeground(AppTheme.TEXT);
        insightCard.add(insightHeading, BorderLayout.NORTH);
        insightsText.setEditable(false);
        insightsText.setFocusable(false);
        insightsText.setLineWrap(true);
        insightsText.setWrapStyleWord(true);
        insightsText.setFont(new Font("SansSerif", Font.PLAIN, 13));
        insightsText.setForeground(AppTheme.TEXT);
        insightsText.setBackground(AppTheme.CARD);
        insightsText.setBorder(BorderFactory.createEmptyBorder());
        insightCard.add(insightsText, BorderLayout.CENTER);
        insightCard.setAlignmentX(LEFT_ALIGNMENT);
        insightCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));
        page.add(insightCard);
        page.add(Box.createVerticalStrut(16));

        charts.setOpaque(false);
        charts.setAlignmentX(LEFT_ALIGNMENT);
        charts.setMinimumSize(new Dimension(600, 280));
        page.add(charts);

        JScrollPane scroll = new JScrollPane(page);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(AppTheme.BACKGROUND);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        add(scroll, BorderLayout.CENTER);

        periodSelector.addActionListener(event -> {
            monthPicker.setEnabled(periodSelector.getSelectedItem() == PeriodMode.SPECIFIC_MONTH);
            refreshData();
        });
        monthPicker.addActionListener(event -> {
            if (periodSelector.getSelectedItem() == PeriodMode.SPECIFIC_MONTH) refreshData();
        });
        refreshData();
    }

    private void populateMonthPicker() {
        YearMonth current = YearMonth.now();
        // Twenty years of selectable months is ample for a personal finance history
        // while keeping the combo manageable.
        for (int offset = 239; offset >= 0; offset--) monthPicker.addItem(current.minusMonths(offset));
        monthPicker.setSelectedItem(current);
    }

    private DateRange selectedRange() {
        PeriodMode mode = (PeriodMode) periodSelector.getSelectedItem();
        LocalDate today = LocalDate.now();
        if (mode == PeriodMode.THIS_YEAR) {
            return new DateRange(today.withDayOfYear(1), today,
                    "1 January " + today.getYear() + " – " + today.format(DateTimeFormatter.ofPattern("d MMMM yyyy")), true);
        }
        YearMonth month = mode == PeriodMode.SPECIFIC_MONTH && monthPicker.getSelectedItem() instanceof YearMonth selected
                ? selected : YearMonth.from(today);
        LocalDate end = month.equals(YearMonth.from(today)) ? today : month.atEndOfMonth();
        String label = month.format(MONTH_FORMAT);
        if (month.equals(YearMonth.from(today))) label += " · through today";
        return new DateRange(month.atDay(1), end, label, false);
    }

    public void refreshData() {
        DateRange range = selectedRange();
        List<FinanceTransaction> allTransactions = context.getTransactionService().findAll();
        List<Category> categories = context.getCategoryService().findAll();
        List<Budget> budgets = context.getBudgetService().findAll();
        ReportService reports = context.getReportService();
        ReportService.Summary summary = reports.summary(range.start(), range.end());
        Map<String, BigDecimal> spending = reports.spendingByCategory(range.start(), range.end());

        incomeCard.setValue(CurrencyFormatter.format(summary.income()));
        incomeCard.setValueColor(AppTheme.TEAL);
        expenseCard.setValue(CurrencyFormatter.format(summary.expenses()));
        expenseCard.setValueColor(AppTheme.RED);
        netCard.setValue(CurrencyFormatter.format(summary.netCashFlow()));
        netCard.setValueColor(summary.netCashFlow().signum() >= 0 ? AppTheme.TEAL : AppTheme.RED);
        savingsCard.setValue(summary.savingsRatePercent() == null ? "N/A" : String.format(java.util.Locale.ROOT, "%.1f%%", summary.savingsRatePercent().doubleValue()));
        savingsCard.setValueColor(summary.savingsRatePercent() == null ? AppTheme.MUTED
                : summary.savingsRatePercent().signum() >= 0 ? AppTheme.TEAL : AppTheme.RED);

        insightsText.setText(buildInsights(range, summary, spending, budgets));
        insightsText.setCaretPosition(0);

        charts.removeAll();
        if (!spending.isEmpty()) {
            String[] labels = spending.keySet().toArray(String[]::new);
            double[] values = spending.values().stream().mapToDouble(BigDecimal::doubleValue).toArray();
            charts.add(new FinancialChartPanel("Expense breakdown · " + range.label(),
                    FinancialChartPanel.Type.DONUT, labels, new double[][]{values}, new String[0]));
        }

        if (range.wholeYear()) {
            addYearTrendIfAvailable(range, allTransactions);
        }
        addBudgetChartIfAvailable(range, budgets, categories);

        if (charts.getComponentCount() == 0) {
            RoundedPanel empty = new RoundedPanel(AppTheme.CARD, 20, AppTheme.BORDER);
            empty.setLayout(new BorderLayout());
            empty.setBorder(BorderFactory.createEmptyBorder(24, 26, 24, 26));
            JLabel message = new JLabel("No charts are available for this period yet. Record income or expenses to see your data here.");
            message.setForeground(AppTheme.MUTED);
            empty.add(message, BorderLayout.CENTER);
            empty.setPreferredSize(new Dimension(600, 140));
            charts.setLayout(new java.awt.GridLayout(1, 1, 16, 16));
            charts.add(empty);
        } else {
            charts.setLayout(new java.awt.GridLayout(0, 2, 16, 16));
        }
        revalidate();
        repaint();
    }

    private String buildInsights(DateRange range, ReportService.Summary summary,
                                 Map<String, BigDecimal> spending, List<Budget> budgets) {
        List<String> lines = new ArrayList<>();
        lines.add("Period: " + range.label());
        if (summary.savingsRatePercent() == null) {
            lines.add("Savings rate: not available because no income was recorded in this period.");
        } else if (summary.netCashFlow().signum() >= 0) {
            lines.add(String.format(java.util.Locale.ROOT,
                    "Savings rate: %.1f%%. Income exceeded expenses by %s.",
                    summary.savingsRatePercent().doubleValue(), CurrencyFormatter.format(summary.netCashFlow())));
        } else {
            lines.add(String.format(java.util.Locale.ROOT,
                    "Savings rate: %.1f%%. Expenses exceeded income by %s.",
                    summary.savingsRatePercent().doubleValue(), CurrencyFormatter.format(summary.netCashFlow().abs())));
        }

        if (!spending.isEmpty() && summary.expenses().signum() > 0) {
            Map.Entry<String, BigDecimal> highest = spending.entrySet().iterator().next();
            BigDecimal share = highest.getValue().multiply(BigDecimal.valueOf(100))
                    .divide(summary.expenses(), 1, java.math.RoundingMode.HALF_UP);
            lines.add("Largest expense category: " + highest.getKey() + " (" + CurrencyFormatter.format(highest.getValue())
                    + ", " + share.toPlainString() + "% of recorded expenses).");
        } else {
            lines.add("No expenses were recorded in this period, so there is no category breakdown yet.");
        }

        long months = java.time.temporal.ChronoUnit.MONTHS.between(
                YearMonth.from(range.start()), YearMonth.from(range.end())) + 1;
        if (range.wholeYear() && months > 0) {
            BigDecimal average = summary.expenses().divide(BigDecimal.valueOf(months), 2, java.math.RoundingMode.HALF_UP);
            lines.add("Average monthly expenses to date: " + CurrencyFormatter.format(average) + ".");
        }

        List<Budget> relevantBudgets = budgets.stream()
                .filter(b -> !b.getMonth().atDay(1).isBefore(range.start()))
                .filter(b -> !b.getMonth().atDay(1).isAfter(range.end()))
                .toList();
        if (!relevantBudgets.isEmpty()) {
            BigDecimal planned = BigDecimal.ZERO;
            BigDecimal spent = BigDecimal.ZERO;
            int over = 0;
            for (Budget budget : relevantBudgets) {
                planned = planned.add(budget.getLimitAmount());
                BigDecimal amount = context.getBudgetService().spentFor(budget);
                spent = spent.add(amount);
                if (amount.compareTo(budget.getLimitAmount()) > 0) over++;
            }
            BigDecimal used = planned.signum() == 0 ? BigDecimal.ZERO : spent.multiply(BigDecimal.valueOf(100))
                    .divide(planned, 1, java.math.RoundingMode.HALF_UP);
            lines.add("Budget usage: " + CurrencyFormatter.format(spent) + " of " + CurrencyFormatter.format(planned)
                    + " planned (" + used.toPlainString() + "%, across " + relevantBudgets.size()
                    + " budget entries; " + over + " over limit).");
        } else {
            lines.add("No budgets are configured for this period.");
        }
        lines.add("Transactions recorded: " + summary.transactionCount() + ".");
        return String.join("\n\n", lines);
    }

    private void addYearTrendIfAvailable(DateRange range, List<FinanceTransaction> transactions) {
        int months = YearMonth.from(range.end()).getMonthValue();
        String[] labels = new String[months];
        double[] income = new double[months];
        double[] expenses = new double[months];
        for (int i = 0; i < months; i++) labels[i] = java.time.Month.of(i + 1).getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH);
        for (FinanceTransaction transaction : transactions) {
            if (transaction.getDate().isBefore(range.start()) || transaction.getDate().isAfter(range.end())) continue;
            int index = transaction.getDate().getMonthValue() - 1;
            if (index < 0 || index >= months) continue;
            if (transaction.getType() == TransactionType.INCOME) income[index] += transaction.getAmount().doubleValue();
            else if (transaction.getType() == TransactionType.EXPENSE) expenses[index] += transaction.getAmount().doubleValue();
        }
        boolean hasTrend = false;
        for (int i = 0; i < months; i++) if (income[i] != 0 || expenses[i] != 0) { hasTrend = true; break; }
        if (hasTrend) charts.add(new FinancialChartPanel("Monthly income vs. expenses",
                FinancialChartPanel.Type.LINE, labels, new double[][]{income, expenses}, new String[]{"Income", "Expenses"}));
    }

    private void addBudgetChartIfAvailable(DateRange range, List<Budget> budgets, List<Category> categories) {
        Map<Long, String> categoryNames = new java.util.HashMap<>();
        for (Category category : categories) categoryNames.put(category.getId(), category.getName());
        Map<Long, BigDecimal> plannedByCategory = new LinkedHashMap<>();
        Map<Long, BigDecimal> spentByCategory = new LinkedHashMap<>();
        for (Budget budget : budgets) {
            LocalDate budgetDate = budget.getMonth().atDay(1);
            if (budgetDate.isBefore(range.start()) || budgetDate.isAfter(range.end())) continue;
            plannedByCategory.merge(budget.getCategoryId(), budget.getLimitAmount(), BigDecimal::add);
            spentByCategory.merge(budget.getCategoryId(), context.getBudgetService().spentFor(budget), BigDecimal::add);
        }
        if (plannedByCategory.isEmpty()) return;

        List<Long> ids = new ArrayList<>(plannedByCategory.keySet());
        List<String> labels = new ArrayList<>();
        double[] spent = new double[ids.size()];
        double[] remaining = new double[ids.size()];
        double[] over = new double[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            Long id = ids.get(i);
            String name = categoryNames.getOrDefault(id, "Category " + id);
            labels.add(name.length() > 13 ? name.substring(0, 12) + "…" : name);
            double limit = plannedByCategory.getOrDefault(id, BigDecimal.ZERO).doubleValue();
            double amount = spentByCategory.getOrDefault(id, BigDecimal.ZERO).doubleValue();
            spent[i] = Math.min(amount, limit);
            remaining[i] = Math.max(0, limit - amount);
            over[i] = Math.max(0, amount - limit);
        }
        charts.add(new FinancialChartPanel("Budget vs. actual", FinancialChartPanel.Type.STACKED_BAR,
                labels.toArray(String[]::new), new double[][]{spent, remaining, over},
                new String[]{"Spent", "Remaining", "Over budget"}));
    }
}
