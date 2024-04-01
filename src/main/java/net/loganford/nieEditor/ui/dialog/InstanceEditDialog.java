package net.loganford.nieEditor.ui.dialog;

import lombok.Getter;
import lombok.Setter;
import net.loganford.nieEditor.data.Entity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InstanceEditDialog implements ActionListener {
    @Getter private boolean accepted;
    @Getter private int x;
    @Getter private int y;

    private DefaultTableModel model;
    private JTable table;

    private Entity entity;
    public InstanceEditDialog(Entity entity) {
        this.entity = entity;
    }

    public void show() {
        JSpinner xSpinner = new JSpinner(new SpinnerNumberModel(entity.getX(), 1, 1000000, 1));
        JSpinner ySpinner = new JSpinner(new SpinnerNumberModel(entity.getY(), 1, 1000000, 1));


        JButton addButton = new JButton("Add Row");
        addButton.addActionListener(this);

        JComponent[] inputs = {
                new JLabel("X Position:"),
                xSpinner,
                new JLabel("Y Position:"),
                ySpinner,
                new JLabel("Custom properties:"),
                scrollTable(),
                addButton

        };

        int result = JOptionPane.showConfirmDialog(null, inputs, "Editing " + entity.getDefinition().getName(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        this.x = (Integer) xSpinner.getValue();
        this.y = (Integer) ySpinner.getValue();
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
        if(e.getActionCommand().equals("Add Row")) {
            model.addRow(new String[] {"Your Key", "Your Value"});
        }
    }
}
