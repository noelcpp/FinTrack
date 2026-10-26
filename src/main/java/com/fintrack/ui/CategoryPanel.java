package com.fintrack.ui;

import com.fintrack.app.AppContext;
import com.fintrack.model.Category;
import com.fintrack.ui.dialog.CategoryDialog;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class CategoryPanel extends JPanel {
    private final AppContext context; private final DefaultTableModel model = new DefaultTableModel(new Object[]{"ID","Name","Type"},0); private final JTable table = new JTable(model);
    public CategoryPanel(AppContext context) {
        this.context=context; setLayout(new BorderLayout(12,12)); setBorder(javax.swing.BorderFactory.createEmptyBorder(24,24,24,24));
        JPanel top=new JPanel(new BorderLayout()); top.add(new javax.swing.JLabel("Categories"),BorderLayout.WEST); JPanel actions=new JPanel(new FlowLayout(FlowLayout.RIGHT)); JButton add=new JButton("Add category"); JButton delete=new JButton("Delete selected");
        add.addActionListener(e->CategoryDialog.open(this,context.getCategoryService(),this::refreshData)); delete.addActionListener(e->deleteSelected()); actions.add(add); actions.add(delete); top.add(actions,BorderLayout.EAST);
        add(top,BorderLayout.NORTH); add(new JScrollPane(table),BorderLayout.CENTER); refreshData();
    }
    public void refreshData(){ model.setRowCount(0); for(Category c:context.getCategoryService().findAll()) model.addRow(new Object[]{c.getId(),c.getName(),c.getType()}); }
    private void deleteSelected(){ int row=table.getSelectedRow(); if(row<0)return; long id=((Number)model.getValueAt(table.convertRowIndexToModel(row),0)).longValue(); try { context.getCategoryService().deleteById(id); refreshData(); } catch(RuntimeException ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"Delete failed",JOptionPane.ERROR_MESSAGE);} }
}
