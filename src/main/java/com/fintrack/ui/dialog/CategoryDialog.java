package com.fintrack.ui.dialog;

import com.fintrack.model.Category;
import com.fintrack.model.enums.TransactionType;
import com.fintrack.service.CategoryService;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.GridLayout;

public final class CategoryDialog {
    private CategoryDialog() { }
    public static void open(Component parent, CategoryService service, Runnable onSaved) {
        JTextField name = new JTextField(); JComboBox<TransactionType> type = new JComboBox<>(TransactionType.values());
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8)); form.add(new JLabel("Name")); form.add(name); form.add(new JLabel("Type")); form.add(type);
        if (JOptionPane.showConfirmDialog(parent, form, "Add category", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try { service.create(new Category(name.getText(), (TransactionType) type.getSelectedItem())); onSaved.run(); }
        catch (RuntimeException ex) { JOptionPane.showMessageDialog(parent, ex.getMessage(), "Could not save category", JOptionPane.ERROR_MESSAGE); }
    }
}
