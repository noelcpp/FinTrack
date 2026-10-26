package com.fintrack.ui.dialog;

import com.fintrack.model.Budget;
import com.fintrack.model.Category;
import com.fintrack.model.enums.TransactionType;
import com.fintrack.service.BudgetService;
import com.fintrack.service.CategoryService;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public final class BudgetDialog {
    private BudgetDialog() { }
    public static void open(Component parent, BudgetService budgets, CategoryService categories, Runnable onSaved) {
        List<Category> eligible = categories.findAll().stream().filter(c -> c.getType() == TransactionType.EXPENSE).toList();
        if (eligible.isEmpty()) { JOptionPane.showMessageDialog(parent, "Create an expense category first."); return; }
        JComboBox<Category> category = new JComboBox<>(eligible.toArray(Category[]::new)); JTextField month = new JTextField(YearMonth.now().toString()); JTextField limit = new JTextField();
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8)); form.add(new JLabel("Expense category")); form.add(category); form.add(new JLabel("Month (YYYY-MM)")); form.add(month); form.add(new JLabel("Limit (INR)")); form.add(limit);
        if (JOptionPane.showConfirmDialog(parent, form, "Add budget", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try { Category selected = (Category) category.getSelectedItem(); budgets.create(new Budget(selected.getId(), YearMonth.parse(month.getText().trim()), new BigDecimal(limit.getText().trim()))); onSaved.run(); }
        catch (RuntimeException ex) { JOptionPane.showMessageDialog(parent, ex.getMessage(), "Could not save budget", JOptionPane.ERROR_MESSAGE); }
    }
}
