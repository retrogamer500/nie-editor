package net.loganford.nieEditor.ui.dialog;

import lombok.Getter;
import lombok.Setter;
import net.loganford.nieEditor.data.Entity;

import javax.swing.*;

public class EntityEditDialog {
    @Getter private boolean accepted;
    @Getter private int x;
    @Getter private int y;

    private Entity entity;
    public EntityEditDialog(Entity entity) {
        this.entity = entity;
    }

    public void show() {
        JSpinner xSpinner = new JSpinner(new SpinnerNumberModel(entity.getX(), 1, 1000000, 1));
        JSpinner ySpinner = new JSpinner(new SpinnerNumberModel(entity.getY(), 1, 1000000, 1));

        JComponent[] inputs = {
                new JLabel("X Position:"),
                xSpinner,
                new JLabel("Y Position:"),
                ySpinner
        };

        int result = JOptionPane.showConfirmDialog(null, inputs, "Editing " + entity.getDefinition().getName(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        this.x = (Integer) xSpinner.getValue();
        this.y = (Integer) ySpinner.getValue();
        this.accepted = result == JOptionPane.YES_OPTION;
    }
}
