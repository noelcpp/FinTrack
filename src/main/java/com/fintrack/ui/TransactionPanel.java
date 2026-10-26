package com.fintrack.ui;

import com.fintrack.app.AppContext;
import com.fintrack.model.FinanceTransaction;
import com.fintrack.ui.components.AppTheme;
import com.fintrack.ui.components.RoundedButton;
import com.fintrack.ui.components.RoundedPanel;
import com.fintrack.ui.components.TransactionTableModel;
import com.fintrack.ui.dialog.TransactionDialog;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

public class TransactionPanel extends JPanel {
    private final AppContext context;
    private final TransactionTableModel model = new TransactionTableModel();
    private final JTable table = new JTable(model);

    public TransactionPanel(AppContext context) {
        this.context = context;
        setLayout(new BorderLayout(18, 18));
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 26, 26, 26));

        JPanel top = new JPanel(new BorderLayout(12, 10)); top.setOpaque(false);
        JLabel title = new JLabel("Transactions"); title.setFont(title.getFont().deriveFont(Font.BOLD, 24f)); title.setForeground(AppTheme.TEXT);
        JLabel sub = new JLabel("Record and review money moving in and out."); sub.setForeground(AppTheme.MUTED);
        JPanel heading = new JPanel(); heading.setOpaque(false); heading.setLayout(new javax.swing.BoxLayout(heading, javax.swing.BoxLayout.Y_AXIS)); heading.add(title); heading.add(javax.swing.Box.createVerticalStrut(4)); heading.add(sub);
        top.add(heading, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); actions.setOpaque(false);
        RoundedButton add = new RoundedButton("Add transaction");
        RoundedButton delete = new RoundedButton("Delete selected", RoundedButton.Variant.DANGER);
        RoundedButton refresh = new RoundedButton("Refresh", RoundedButton.Variant.SECONDARY);
        add.addActionListener(e -> TransactionDialog.open(this, context.getTransactionService(), context.getCategoryService(), context.getAccountService(), this::refreshData));
        delete.addActionListener(e -> deleteSelected()); refresh.addActionListener(e -> refreshData());
        actions.add(add); actions.add(delete); actions.add(refresh); top.add(actions, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        AppTheme.styleTable(table);
        JScrollPane scroll = new JScrollPane(table); scroll.setBorder(BorderFactory.createEmptyBorder()); scroll.getViewport().setBackground(AppTheme.CARD);
        RoundedPanel tableCard = new RoundedPanel(AppTheme.CARD, 20, AppTheme.BORDER); tableCard.setLayout(new BorderLayout()); tableCard.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8)); tableCard.add(scroll, BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);
        refreshData();
    }

    public void refreshData() { model.setRows(context.getTransactionService().findAll(), context.getCategoryService().findAll(), context.getAccountService().findAll()); }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a transaction first."); return; }
        FinanceTransaction tx = model.getTransactionAt(table.convertRowIndexToModel(row));
        if (JOptionPane.showConfirmDialog(this, "Delete '" + tx.getDescription() + "'?", "Confirm delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try { context.getTransactionService().deleteById(tx.getId()); refreshData(); }
            catch (RuntimeException ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete failed", JOptionPane.ERROR_MESSAGE); }
        }
    }
}
