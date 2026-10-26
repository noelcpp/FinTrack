package com.fintrack.ui.components;

import javax.swing.table.DefaultTableModel;

/** A table model for data that must be changed through the application's forms/services. */
public final class ReadOnlyTableModel extends DefaultTableModel {
    private static final long serialVersionUID = 1L;

    public ReadOnlyTableModel(Object[] columnNames) {
        super(columnNames, 0);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
