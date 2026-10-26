package com.fintrack.ui.dialog;

import com.fintrack.model.Account;
import com.fintrack.model.enums.AccountType;
import com.fintrack.service.AccountService;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.GridLayout;
import java.math.BigDecimal;

public final class AccountDialog {
    private AccountDialog() { }
    public static void open(Component parent, AccountService service, Runnable onSaved) {
        JTextField name = new JTextField(); JTextField balance = new JTextField("0.00"); JComboBox<AccountType> type = new JComboBox<>(AccountType.values());
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8)); form.add(new JLabel("Name")); form.add(name); form.add(new JLabel("Type")); form.add(type); form.add(new JLabel("Opening balance")); form.add(balance);
        if (JOptionPane.showConfirmDialog(parent, form, "Add account", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try { service.create(new Account(name.getText(), (AccountType) type.getSelectedItem(), new BigDecimal(balance.getText().trim()))); onSaved.run(); }
        catch (RuntimeException ex) { JOptionPane.showMessageDialog(parent, ex.getMessage(), "Could not save account", JOptionPane.ERROR_MESSAGE); }
    }
}
