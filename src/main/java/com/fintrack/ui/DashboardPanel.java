package com.fintrack.ui;

import com.fintrack.service.DashboardService;
import com.fintrack.ui.components.SummaryCard;
import com.fintrack.util.CurrencyFormatter;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.math.BigDecimal;

public class DashboardPanel extends JPanel {
    private final DashboardService service;
    private final SummaryCard income = new SummaryCard("Income this month");
    private final SummaryCard expenses = new SummaryCard("Expenses this month");
    private final SummaryCard net = new SummaryCard("Net cash flow");
    private final JLabel monthLabel = new JLabel();
    public DashboardPanel(DashboardService service) {
        this.service = service; setLayout(new BorderLayout(16, 16)); setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        JPanel heading = new JPanel(new BorderLayout()); heading.add(new JLabel("Dashboard"), BorderLayout.WEST); heading.add(monthLabel, BorderLayout.EAST); add(heading, BorderLayout.NORTH);
        JPanel cards = new JPanel(new GridLayout(1, 3, 14, 14)); cards.add(income); cards.add(expenses); cards.add(net); add(cards, BorderLayout.CENTER); refreshData();
    }
    public void refreshData() {
        YearMonth month = YearMonth.now(); monthLabel.setText(month.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        Map<String, BigDecimal> summary = service.monthSummary(month);
        income.setValue(CurrencyFormatter.format(summary.get("income"))); expenses.setValue(CurrencyFormatter.format(summary.get("expense"))); net.setValue(CurrencyFormatter.format(summary.get("net")));
    }
}
