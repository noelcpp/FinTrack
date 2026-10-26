package com.fintrack.ui;

import com.fintrack.app.AppContext;
import com.fintrack.model.Category;
import com.fintrack.ui.components.AppTheme;
import com.fintrack.ui.components.RoundedButton;
import com.fintrack.ui.components.RoundedPanel;
import com.fintrack.ui.dialog.CategoryDialog;
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

public class CategoryPanel extends JPanel {
    private final AppContext context;
    private final ReadOnlyTableModel model = new ReadOnlyTableModel(new Object[]{"ID", "Name", "Type"});
    private final JTable table = new JTable(model);

    public CategoryPanel(AppContext context) {
        this.context = context; setLayout(new BorderLayout(18, 18)); setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 26, 26, 26));
        JPanel top = new JPanel(new BorderLayout(12, 10)); top.setOpaque(false);
        JLabel title = new JLabel("Categories"); title.setFont(title.getFont().deriveFont(Font.BOLD, 24f)); title.setForeground(AppTheme.TEXT);
        JLabel subtitle = new JLabel("Organise income and expenses into meaningful groups."); subtitle.setForeground(AppTheme.MUTED);
        JPanel heading = new JPanel(); heading.setOpaque(false); heading.setLayout(new javax.swing.BoxLayout(heading, javax.swing.BoxLayout.Y_AXIS)); heading.add(title); heading.add(javax.swing.Box.createVerticalStrut(4)); heading.add(subtitle); top.add(heading, BorderLayout.WEST);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); actions.setOpaque(false);
        RoundedButton add = new RoundedButton("Add category"); RoundedButton delete = new RoundedButton("Delete selected", RoundedButton.Variant.DANGER);
        add.addActionListener(e -> CategoryDialog.open(this, context.getCategoryService(), this::refreshData)); delete.addActionListener(e -> deleteSelected());
        actions.add(add); actions.add(delete); top.add(actions, BorderLayout.EAST); add(top, BorderLayout.NORTH);
        AppTheme.styleTable(table);
        AppTheme.hideColumn(table, "ID");
        table.setAutoCreateRowSorter(true);
        JScrollPane scroll = new JScrollPane(table); scroll.setBorder(BorderFactory.createEmptyBorder()); scroll.getViewport().setBackground(AppTheme.CARD);
        RoundedPanel card = new RoundedPanel(AppTheme.CARD, 20, AppTheme.BORDER); card.setLayout(new BorderLayout()); card.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8)); card.add(scroll);
        add(card, BorderLayout.CENTER); refreshData();
    }

    public void refreshData() { model.setRowCount(0); for (Category c : context.getCategoryService().findAll()) model.addRow(new Object[]{c.getId(), c.getName(), c.getType()}); }

    private void deleteSelected() {
        int row = table.getSelectedRow(); if (row < 0) { JOptionPane.showMessageDialog(this, "Select a category first."); return; }
        int modelRow = table.convertRowIndexToModel(row);
        long id = ((Number) model.getValueAt(modelRow, 0)).longValue();
        String name = String.valueOf(model.getValueAt(modelRow, 1));
        int answer = JOptionPane.showConfirmDialog(this,
                "Delete category '" + name + "'? Existing transactions will become uncategorised and related budgets will be removed.",
                "Confirm category deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) return;
        try { context.getCategoryService().deleteById(id); refreshData(); }
        catch (RuntimeException ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete failed", JOptionPane.ERROR_MESSAGE); }
    }
}
