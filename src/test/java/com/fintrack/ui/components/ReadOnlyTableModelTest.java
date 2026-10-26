package com.fintrack.ui.components;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ReadOnlyTableModelTest {
    @Test
    void cellsAreNotEditable() {
        ReadOnlyTableModel model = new ReadOnlyTableModel(new Object[]{"ID", "Name"});
        model.addRow(new Object[]{1L, "Food"});

        assertFalse(model.isCellEditable(0, 0));
        assertFalse(model.isCellEditable(0, 1));
    }
}
