package net.loganford.nieEditor.ui.dialog;

import lombok.Getter;
import lombok.Setter;
import net.loganford.nieEditor.data.Entity;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InstanceEditDialog implements ActionListener {
    @Getter private boolean accepted;
    @Getter private int x;
    @Getter private int y;
    @Getter private HashMap<String, String> properties = new HashMap<>();

    private DefaultTableModel model;
    private JTable table;

    private Entity entity;
    public InstanceEditDialog(Entity entity) {
        this.entity = entity;
    }

    public void show() {
        JSpinner xSpinner = new JSpinner(new SpinnerNumberModel(entity.getX(), 1, 1000000, 1));
        JSpinner ySpinner = new JSpinner(new SpinnerNumberModel(entity.getY(), 1, 1000000, 1));


        JButton addButton = new JButton("Add Property");
        addButton.addActionListener(this);

        JButton removeButton = new JButton("Remove Property");
        removeButton.addActionListener(this);

        JComponent[] inputs = {
                new JLabel("X Position:"),
                xSpinner,
                new JLabel("Y Position:"),
                ySpinner,
                new JLabel("Custom Properties:"),
                scrollTable(),
                addButton,
                removeButton

        };

        if(entity.getProperties() != null) {
            for (Map.Entry<String, String> entry : entity.getProperties().entrySet()) {
                model.addRow(new String[]{entry.getKey(), entry.getValue()});
            }
        }

        int result = JOptionPane.showConfirmDialog(null, inputs, "Editing " + entity.getDefinition().getName(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        this.x = (Integer) xSpinner.getValue();
        this.y = (Integer) ySpinner.getValue();

        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }

        for(int i = 0; i < model.getRowCount(); i++) {
            String key = (String) model.getValueAt(i, 0);
            String value = (String) model.getValueAt(i, 1);

            if(StringUtils.isNotBlank(key)) {
                properties.put(key, value);
            }
        }

        this.accepted = result == JOptionPane.YES_OPTION;
    }

    private JScrollPane scrollTable() {
        JScrollPane scroll = new JScrollPane();
        scroll.setPreferredSize(new Dimension(200, 200));
        model = new DefaultTableModel(0, 2);
        model.setColumnIdentifiers(new String[] {"Key", "Value"});
        table = new JTable(model);
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
}
