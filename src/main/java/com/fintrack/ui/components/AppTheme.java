package com.fintrack.ui.components;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

/** Shared visual tokens and defaults for the FinTrack Swing UI. */
public final class AppTheme {
    public static final Color BACKGROUND = new Color(245, 247, 251);
    public static final Color CARD = Color.WHITE;
    public static final Color TEXT = new Color(31, 41, 55);
    public static final Color MUTED = new Color(111, 122, 140);
    public static final Color BORDER = new Color(226, 231, 239);
    public static final Color SIDEBAR = new Color(17, 27, 45);
    public static final Color SIDEBAR_BUTTON = new Color(28, 40, 61);
    public static final Color PRIMARY = new Color(74, 105, 224);
    public static final Color PRIMARY_DARK = new Color(56, 84, 193);
    public static final Color TEAL = new Color(21, 153, 130);
    public static final Color ORANGE = new Color(225, 143, 51);
    public static final Color RED = new Color(205, 78, 91);
    public static final Color GRID = new Color(235, 239, 245);
    public static final Color[] CHART_COLORS = {
        new Color(74, 105, 224), new Color(21, 153, 130),
        new Color(225, 143, 51), new Color(151, 103, 216),
        new Color(205, 78, 91), new Color(54, 153, 205),
        new Color(137, 153, 174), new Color(109, 171, 100)
    };

    private AppTheme() { }

    public static void install() {
        Font regular = new Font("SansSerif", Font.PLAIN, 13);
        UIManager.put("defaultFont", regular);
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("Button.font", regular.deriveFont(Font.BOLD, 12f));
        UIManager.put("Button.background", PRIMARY);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("TextField.font", regular);
        UIManager.put("TextField.background", CARD);
        UIManager.put("TextField.foreground", TEXT);
        UIManager.put("TextField.margin", new Insets(9, 12, 9, 12));
        UIManager.put("TextField.border", BorderFactory.createCompoundBorder(new RoundedLineBorder(BORDER, 16), BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        UIManager.put("PasswordField.font", regular);
        UIManager.put("PasswordField.background", CARD);
        UIManager.put("PasswordField.foreground", TEXT);
        UIManager.put("PasswordField.margin", new Insets(9, 12, 9, 12));
        UIManager.put("PasswordField.border", BorderFactory.createCompoundBorder(new RoundedLineBorder(BORDER, 16), BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        UIManager.put("Table.font", regular);
        UIManager.put("Table.background", CARD);
        UIManager.put("Table.foreground", TEXT);
        UIManager.put("Table.gridColor", GRID);
        UIManager.put("Table.selectionBackground", new Color(224, 233, 255));
        UIManager.put("Table.selectionForeground", TEXT);
        UIManager.put("TableHeader.background", new Color(248, 250, 253));
        UIManager.put("TableHeader.foreground", MUTED);
        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
        UIManager.put("OptionPane.background", CARD);
        UIManager.put("OptionPane.messageForeground", TEXT);
    }

    /** Hides an implementation-only model column without removing its underlying data. */
    public static void hideColumn(JTable table, String columnName) {
        TableColumnModel columnModel = table.getColumnModel();
        for (int index = 0; index < columnModel.getColumnCount(); index++) {
            TableColumn column = columnModel.getColumn(index);
            if (columnName.equals(column.getHeaderValue())) {
                columnModel.removeColumn(column);
                return;
            }
        }
    }

    public static void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setRowHeight(36);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setForeground(TEXT);
        table.setBackground(CARD);
        table.setSelectionBackground(new Color(224, 233, 255));
        table.setSelectionForeground(TEXT);
        table.setGridColor(GRID);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new java.awt.Dimension(0, 1));
        table.setBorder(BorderFactory.createEmptyBorder());
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setForeground(MUTED);
        header.setBackground(new Color(248, 250, 253));
        header.setPreferredSize(new java.awt.Dimension(header.getPreferredSize().width, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
    }
}
