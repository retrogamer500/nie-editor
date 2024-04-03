package net.loganford.nieEditor.ui.leftPane;

import net.loganford.nieEditor.data.Project;
import net.loganford.nieEditor.ui.Window;
import net.loganford.nieEditor.util.ProjectListener;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class ProjectProperties extends JPanel implements ActionListener, ProjectListener, TableModelListener {

    private Window window;
    private DefaultTableModel model;
    private JTable table;

    public ProjectProperties(Window window) {
        setLayout(new BorderLayout());

        add(scrollTable(), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setLayout(new GridLayout(1, 2));

        JButton addButton = new JButton("Add Property");
        addButton.addActionListener(this);
        bottom.add(addButton);

        JButton removeButton = new JButton("Remove Property");
        removeButton.addActionListener(this);
        bottom.add(removeButton);

        add(bottom, BorderLayout.SOUTH);

        window.getListeners().add(this);

        this.window = window;
    }

    private JScrollPane scrollTable() {
        JScrollPane scroll = new JScrollPane();
        scroll.setPreferredSize(new Dimension(200, 200));
        model = new DefaultTableModel(0, 2);
        model.setColumnIdentifiers(new String[] {"Key", "Value"});
        table = new JTable(model);
        model.addTableModelListener(this);
        scroll.getViewport().add(table);
        return scroll;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Add Property")) {
            model.addRow(new String[] {"Your Key", "Your Value"});
        }
        if(e.getActionCommand().equals("Remove Property")) {
            int row = table.getSelectedRow();
            if(row != -1) {
                if (table.isEditing()) {
                    table.getCellEditor().stopCellEditing();
                }

                model.removeRow(row);
            }
        }
    }

    @Override
    public void projectChanged(Project project) {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }

        while(model.getRowCount() > 0) {
            model.removeRow(0);
        }

        for(Map.Entry<String, String> entry : project.getProperties().entrySet()) {
            model.addRow(new String[] {entry.getKey(), entry.getValue()});
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        window.getProject().getProperties().clear();

        for(int i = 0; i < model.getRowCount(); i++) {
            String key = (String) model.getValueAt(i, 0);
            String value = (String) model.getValueAt(i, 1);


            if(StringUtils.isNotEmpty(key)) {
                window.getProject().getProperties().put(key, value);
            }
        }
    }

    @Override
    public void leftTabChanged() {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
    }
}
