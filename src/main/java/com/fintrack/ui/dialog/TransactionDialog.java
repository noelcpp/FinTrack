package com.fintrack.ui.dialog;

import com.fintrack.model.Account;
import com.fintrack.model.Category;
import com.fintrack.model.FinanceTransaction;
import com.fintrack.model.enums.TransactionType;
import com.fintrack.service.AccountService;
import com.fintrack.service.CategoryService;
import com.fintrack.service.TransactionService;
import com.fintrack.util.DateUtils;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class TransactionDialog {
    private TransactionDialog() { }
    public static void open(Component parent, TransactionService transactions, CategoryService categories, AccountService accounts, Runnable onSaved) {
        List<Category> allCategories = categories.findAll(); List<Account> allAccounts = accounts.findAll();
        if (allAccounts.isEmpty()) { JOptionPane.showMessageDialog(parent, "Create an account first."); return; }
        JTextField description = new JTextField(); JTextField amount = new JTextField(); JTextField date = new JTextField(LocalDate.now().toString());
        JComboBox<TransactionType> type = new JComboBox<>(TransactionType.values());
        JComboBox<Category> category = new JComboBox<>(allCategories.toArray(Category[]::new));
        JComboBox<Account> account = new JComboBox<>(allAccounts.toArray(Account[]::new));
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Description")); form.add(description); form.add(new JLabel("Amount (INR)")); form.add(amount);
        form.add(new JLabel("Date (YYYY-MM-DD)")); form.add(date); form.add(new JLabel("Type")); form.add(type);
        form.add(new JLabel("Category")); form.add(category); form.add(new JLabel("Account")); form.add(account);
        if (JOptionPane.showConfirmDialog(parent, form, "Add transaction", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        try {
            Category selectedCategory = (Category) category.getSelectedItem(); Account selectedAccount = (Account) account.getSelectedItem();
            FinanceTransaction tx = new FinanceTransaction(description.getText(), new BigDecimal(amount.getText().trim()), DateUtils.parseDate(date.getText()),
                    (TransactionType) type.getSelectedItem(), selectedCategory == null ? null : selectedCategory.getId(), selectedAccount == null ? null : selectedAccount.getId());
            transactions.create(tx); onSaved.run();
        } catch (RuntimeException ex) { JOptionPane.showMessageDialog(parent, ex.getMessage(), "Could not save transaction", JOptionPane.ERROR_MESSAGE); }
    }
}
