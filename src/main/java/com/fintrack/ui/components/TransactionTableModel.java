package com.fintrack.ui.components;

import com.fintrack.model.Account;
import com.fintrack.model.Category;
import com.fintrack.model.FinanceTransaction;
import com.fintrack.util.CurrencyFormatter;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionTableModel extends AbstractTableModel {
    private final String[] columns = {"Date", "Description", "Type", "Amount", "Category", "Account"};
    private List<FinanceTransaction> rows = new ArrayList<>();
    private Map<Long, String> categoryNames = Map.of();
    private Map<Long, String> accountNames = Map.of();

    public void setRows(List<FinanceTransaction> rows) {
        setRows(rows, List.of(), List.of());
    }

    public void setRows(List<FinanceTransaction> rows, List<Category> categories, List<Account> accounts) {
        this.rows = new ArrayList<>(rows);
        Map<Long, String> cats = new HashMap<>();
        for (Category category : categories) cats.put(category.getId(), category.getName());
        Map<Long, String> accts = new HashMap<>();
        for (Account account : accounts) accts.put(account.getId(), account.getName());
        categoryNames = cats;
        accountNames = accts;
        fireTableDataChanged();
    }

    public FinanceTransaction getTransactionAt(int row) { return rows.get(row); }
    @Override public int getRowCount() { return rows.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public String getColumnName(int column) { return columns[column]; }

    @Override public Object getValueAt(int rowIndex, int columnIndex) {
        FinanceTransaction tx = rows.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> tx.getDate();
            case 1 -> tx.getDescription();
            case 2 -> tx.getType();
            case 3 -> CurrencyFormatter.format(tx.getAmount());
            case 4 -> tx.getCategoryId() == null ? "—" : categoryNames.getOrDefault(tx.getCategoryId(), "Unknown");
            case 5 -> accountNames.getOrDefault(tx.getAccountId(), "Unknown");
            default -> "";
        };
    }
}
