package com.fintrack.ui;

import com.fintrack.app.AppContext;
import com.fintrack.model.Category;
import com.fintrack.model.FinanceTransaction;
import com.fintrack.model.enums.TransactionType;
import com.fintrack.ui.components.AppTheme;
import com.fintrack.ui.components.FinancialChartPanel;
import com.fintrack.ui.components.RoundedPanel;
import com.fintrack.util.CurrencyFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportsPanel extends JPanel {
    private final AppContext context;
    private final JTextArea output = new JTextArea();
    private final JPanel body = new JPanel(new java.awt.GridLayout(1, 2, 16, 16));

    public ReportsPanel(AppContext context) {
        this.context = context;
        setLayout(new BorderLayout()); setBackground(AppTheme.BACKGROUND);
        JPanel page = new JPanel(); page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS)); page.setBackground(AppTheme.BACKGROUND);
        page.setBorder(BorderFactory.createEmptyBorder(24, 26, 26, 26));
        JLabel title = new JLabel("Reports & insights"); title.setFont(title.getFont().deriveFont(Font.BOLD, 24f)); title.setForeground(AppTheme.TEXT); title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel subtitle = new JLabel("Understand where your money is going."); subtitle.setForeground(AppTheme.MUTED); subtitle.setAlignmentX(LEFT_ALIGNMENT);
        page.add(title); page.add(Box.createVerticalStrut(5)); page.add(subtitle); page.add(Box.createVerticalStrut(20));
        body.setOpaque(false); body.setAlignmentX(LEFT_ALIGNMENT); page.add(body);
        JScrollPane scroll = new JScrollPane(page); scroll.setBorder(BorderFactory.createEmptyBorder()); scroll.getViewport().setBackground(AppTheme.BACKGROUND); scroll.getVerticalScrollBar().setUnitIncrement(18);
        add(scroll, BorderLayout.CENTER); refreshData();
    }

    public void refreshData() {
        YearMonth month = YearMonth.now();
        List<FinanceTransaction> transactions = context.getTransactionService().findAll();
        Map<Long, String> names = new java.util.HashMap<>();
        for (Category c : context.getCategoryService().findAll()) names.put(c.getId(), c.getName());
        Map<String, java.math.BigDecimal> spending = new LinkedHashMap<>();
        for (FinanceTransaction tx : transactions) {
            if (tx.getType() != TransactionType.EXPENSE || !YearMonth.from(tx.getDate()).equals(month)) continue;
            String category = tx.getCategoryId() == null ? "Uncategorised" : names.getOrDefault(tx.getCategoryId(), "Other");
            spending.merge(category, tx.getAmount(), java.math.BigDecimal::add);
        }
        body.removeAll();
        RoundedPanel breakdown = new RoundedPanel(AppTheme.CARD, 20, AppTheme.BORDER);
        breakdown.setLayout(new BorderLayout(10, 12)); breakdown.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        JLabel listTitle = new JLabel("This month's spending"); listTitle.setFont(listTitle.getFont().deriveFont(Font.BOLD, 14f)); listTitle.setForeground(AppTheme.TEXT);
        breakdown.add(listTitle, BorderLayout.NORTH);
        output.setEditable(false); output.setFont(new Font("SansSerif", Font.PLAIN, 13)); output.setForeground(AppTheme.TEXT); output.setBackground(AppTheme.CARD); output.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));
        StringBuilder text = new StringBuilder(month.format(DateTimeFormatter.ofPattern("MMMM yyyy"))).append("\n\n");
        if (spending.isEmpty()) text.append("No expense transactions recorded this month.\nAdd transactions to generate a breakdown.");
        else spending.forEach((name, amount) -> text.append(name).append("\n    ").append(CurrencyFormatter.format(amount)).append("\n\n"));
        output.setText(text.toString()); output.setCaretPosition(0);
        JScrollPane textScroll = new JScrollPane(output); textScroll.setBorder(BorderFactory.createEmptyBorder()); textScroll.getViewport().setBackground(AppTheme.CARD); breakdown.add(textScroll, BorderLayout.CENTER);
        breakdown.setPreferredSize(new Dimension(360, 330));
        body.add(breakdown);
        if (!spending.isEmpty()) {
            String[] labels = spending.keySet().toArray(String[]::new);
            double[] values = spending.values().stream().mapToDouble(java.math.BigDecimal::doubleValue).toArray();
            body.add(new FinancialChartPanel("Expense breakdown", FinancialChartPanel.Type.DONUT, labels, new double[][]{values}, new String[0]));
        } else {
            RoundedPanel empty = new RoundedPanel(AppTheme.CARD, 20, AppTheme.BORDER); empty.setLayout(new BorderLayout()); empty.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
            JLabel label = new JLabel("The chart will appear after you record expenses."); label.setForeground(AppTheme.MUTED); empty.add(label, BorderLayout.CENTER); body.add(empty);
        }
        revalidate(); repaint();
    }
}
