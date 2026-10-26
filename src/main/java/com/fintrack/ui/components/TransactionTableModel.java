package com.fintrack.ui.components;

import com.fintrack.model.FinanceTransaction;
import com.fintrack.util.CurrencyFormatter;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TransactionTableModel extends AbstractTableModel {
    private final String[] columns = {"Date", "Description", "Type", "Amount", "Category ID", "Account ID"};
    private List<FinanceTransaction> rows = new ArrayList<>();
    public void setRows(List<FinanceTransaction> rows) { this.rows = new ArrayList<>(rows); fireTableDataChanged(); }
    public FinanceTransaction getTransactionAt(int row) { return rows.get(row); }
    @Override public int getRowCount() { return rows.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public String getColumnName(int column) { return columns[column]; }
    @Override public Object getValueAt(int rowIndex, int columnIndex) {
        FinanceTransaction tx = rows.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> tx.getDate(); case 1 -> tx.getDescription(); case 2 -> tx.getType();
            case 3 -> CurrencyFormatter.format(tx.getAmount()); case 4 -> tx.getCategoryId() == null ? "—" : tx.getCategoryId();
            case 5 -> tx.getAccountId(); default -> "";
        };
    }
}
