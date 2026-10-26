package com.fintrack.ui;

import com.fintrack.app.AppContext;
import com.fintrack.model.Budget;
import com.fintrack.model.Category;
import com.fintrack.ui.dialog.BudgetDialog;
import com.fintrack.util.CurrencyFormatter;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Map;
import java.util.HashMap;

public class BudgetPanel extends JPanel {
    private final AppContext context; private final DefaultTableModel model=new DefaultTableModel(new Object[]{"ID","Month","Category","Limit","Spent","Remaining"},0); private final JTable table=new JTable(model);
    public BudgetPanel(AppContext context){this.context=context;setLayout(new BorderLayout(12,12));setBorder(javax.swing.BorderFactory.createEmptyBorder(24,24,24,24)); JPanel top=new JPanel(new BorderLayout());top.add(new javax.swing.JLabel("Budgets"),BorderLayout.WEST); JPanel actions=new JPanel(new FlowLayout(FlowLayout.RIGHT));JButton add=new JButton("Add budget");JButton delete=new JButton("Delete selected");add.addActionListener(e->BudgetDialog.open(this,context.getBudgetService(),context.getCategoryService(),this::refreshData));delete.addActionListener(e->deleteSelected());actions.add(add);actions.add(delete);top.add(actions,BorderLayout.EAST);add(top,BorderLayout.NORTH);add(new JScrollPane(table),BorderLayout.CENTER);refreshData();}
    public void refreshData(){model.setRowCount(0);Map<Long,String> names=new HashMap<>();for(Category c:context.getCategoryService().findAll())names.put(c.getId(),c.getName());for(Budget b:context.getBudgetService().findAll()){java.math.BigDecimal spent=context.getBudgetService().spentFor(b);model.addRow(new Object[]{b.getId(),b.getMonth(),names.getOrDefault(b.getCategoryId(),"Unknown"),CurrencyFormatter.format(b.getLimitAmount()),CurrencyFormatter.format(spent),CurrencyFormatter.format(b.getLimitAmount().subtract(spent))});}}
    private void deleteSelected(){int row=table.getSelectedRow();if(row<0)return;long id=((Number)model.getValueAt(table.convertRowIndexToModel(row),0)).longValue();try{context.getBudgetService().deleteById(id);refreshData();}catch(RuntimeException ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"Delete failed",JOptionPane.ERROR_MESSAGE);}}
}
