package com.fintrack.ui;

import com.fintrack.app.AppContext;
import com.fintrack.model.FinanceTransaction;
import com.fintrack.ui.components.TransactionTableModel;
import com.fintrack.ui.dialog.TransactionDialog;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class TransactionPanel extends JPanel {
    private final AppContext context;
    private final TransactionTableModel model = new TransactionTableModel();
    private final JTable table = new JTable(model);
    public TransactionPanel(AppContext context) {
        this.context = context; setLayout(new BorderLayout(12,12)); setBorder(BorderFactory.createEmptyBorder(24,24,24,24));
        JPanel top = new JPanel(new BorderLayout()); top.add(new javax.swing.JLabel("Transactions"), BorderLayout.WEST);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT)); JButton add = new JButton("Add transaction"); JButton delete = new JButton("Delete selected"); JButton refresh = new JButton("Refresh");
        add.addActionListener(e -> TransactionDialog.open(this, context.getTransactionService(), context.getCategoryService(), context.getAccountService(), this::refreshData));
        delete.addActionListener(e -> deleteSelected()); refresh.addActionListener(e -> refreshData()); actions.add(add); actions.add(delete); actions.add(refresh); top.add(actions,BorderLayout.EAST);
        add(top,BorderLayout.NORTH); table.setFillsViewportHeight(true); add(new JScrollPane(table),BorderLayout.CENTER); refreshData();
    }
    public void refreshData() { model.setRows(context.getTransactionService().findAll()); }
    private void deleteSelected() {
        int row = table.getSelectedRow(); if(row<0) { JOptionPane.showMessageDialog(this,"Select a transaction first."); return; }
        FinanceTransaction tx = model.getTransactionAt(table.convertRowIndexToModel(row));
        if(JOptionPane.showConfirmDialog(this,"Delete '"+tx.getDescription()+"'?","Confirm delete",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
            try { context.getTransactionService().deleteById(tx.getId()); refreshData(); }
            catch(RuntimeException ex) { JOptionPane.showMessageDialog(this,ex.getMessage(),"Delete failed",JOptionPane.ERROR_MESSAGE); }
        }
    }
}
