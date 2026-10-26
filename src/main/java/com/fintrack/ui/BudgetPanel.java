package com.fintrack.ui;

import com.fintrack.app.AppContext;
import com.fintrack.model.Budget;
import com.fintrack.model.Category;
import com.fintrack.ui.components.AppTheme;
import com.fintrack.ui.components.RoundedButton;
import com.fintrack.ui.components.RoundedPanel;
import com.fintrack.ui.dialog.BudgetDialog;
import com.fintrack.util.CurrencyFormatter;
import com.fintrack.ui.components.ReadOnlyTableModel;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

public class BudgetPanel extends JPanel {
    private final AppContext context;
    private final ReadOnlyTableModel model = new ReadOnlyTableModel(new Object[]{"ID", "Month", "Category", "Limit", "Spent", "Remaining"});
    private final JTable table = new JTable(model);

    public BudgetPanel(AppContext context) {
        this.context = context; setLayout(new BorderLayout(18, 18)); setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 26, 26, 26));
        JPanel top = new JPanel(new BorderLayout(12, 10)); top.setOpaque(false);
        JLabel title = new JLabel("Budgets"); title.setFont(title.getFont().deriveFont(Font.BOLD, 24f)); title.setForeground(AppTheme.TEXT);
        JLabel subtitle = new JLabel("Set monthly limits and see how much remains."); subtitle.setForeground(AppTheme.MUTED);
        JPanel heading = new JPanel(); heading.setOpaque(false); heading.setLayout(new javax.swing.BoxLayout(heading, javax.swing.BoxLayout.Y_AXIS)); heading.add(title); heading.add(javax.swing.Box.createVerticalStrut(4)); heading.add(subtitle); top.add(heading, BorderLayout.WEST);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); actions.setOpaque(false);
        RoundedButton add = new RoundedButton("Add budget"); RoundedButton delete = new RoundedButton("Delete selected", RoundedButton.Variant.DANGER);
        add.addActionListener(e -> BudgetDialog.open(this, context.getBudgetService(), context.getCategoryService(), this::refreshData)); delete.addActionListener(e -> deleteSelected());
        actions.add(add); actions.add(delete); top.add(actions, BorderLayout.EAST); add(top, BorderLayout.NORTH);
        AppTheme.styleTable(table);
        AppTheme.hideColumn(table, "ID");
        JScrollPane scroll = new JScrollPane(table); scroll.setBorder(BorderFactory.createEmptyBorder()); scroll.getViewport().setBackground(AppTheme.CARD);
        RoundedPanel card = new RoundedPanel(AppTheme.CARD, 20, AppTheme.BORDER); card.setLayout(new BorderLayout()); card.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8)); card.add(scroll);
        add(card, BorderLayout.CENTER); refreshData();
    }

    public void refreshData() {
        model.setRowCount(0); Map<Long, String> names = new HashMap<>();
        for (Category c : context.getCategoryService().findAll()) names.put(c.getId(), c.getName());
        for (Budget b : context.getBudgetService().findAll()) {
            java.math.BigDecimal spent = context.getBudgetService().spentFor(b);
            model.addRow(new Object[]{b.getId(), b.getMonth(), names.getOrDefault(b.getCategoryId(), "Unknown"), CurrencyFormatter.format(b.getLimitAmount()), CurrencyFormatter.format(spent), CurrencyFormatter.format(b.getLimitAmount().subtract(spent))});
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow(); if (row < 0) { JOptionPane.showMessageDialog(this, "Select a budget first."); return; }
        int modelRow = table.convertRowIndexToModel(row);
        long id = ((Number) model.getValueAt(modelRow, 0)).longValue();
        int answer = JOptionPane.showConfirmDialog(this, "Delete this budget?", "Confirm budget deletion",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) return;
        try { context.getBudgetService().deleteById(id); refreshData(); }
        catch (RuntimeException ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete failed", JOptionPane.ERROR_MESSAGE); }
    }
}
