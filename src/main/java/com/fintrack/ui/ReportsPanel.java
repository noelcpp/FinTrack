package com.fintrack.ui;

import com.fintrack.app.AppContext;
import com.fintrack.util.CurrencyFormatter;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.time.YearMonth;
import java.util.Map;
import java.math.BigDecimal;

public class ReportsPanel extends JPanel {
    private final AppContext context; private final JTextArea output=new JTextArea();
    public ReportsPanel(AppContext context){this.context=context;setLayout(new BorderLayout(12,12));setBorder(BorderFactory.createEmptyBorder(24,24,24,24));add(new JLabel("Reports — current month spending by category"),BorderLayout.NORTH);output.setEditable(false);output.setFont(new java.awt.Font(java.awt.Font.MONOSPACED,java.awt.Font.PLAIN,13));add(new JScrollPane(output),BorderLayout.CENTER);refreshData();}
    public void refreshData(){YearMonth month=YearMonth.now();Map<String,BigDecimal> spending=context.getReportService().spendingByCategory(month);StringBuilder text=new StringBuilder("Month: ").append(month).append("\n\n");if(spending.isEmpty())text.append("No expense transactions recorded this month.\n");else spending.forEach((name,amount)->text.append(String.format("%-28s %s%n",name,CurrencyFormatter.format(amount))));output.setText(text.toString());output.setCaretPosition(0);}
}
