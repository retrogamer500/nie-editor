package net.loganford.nieEditor.ui.dialog;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;

public class LayerDialog {
    private boolean newLayer;

    @Getter
    private boolean accepted = false;
    @Getter @Setter
    private String layerName = "New Layer";

    public LayerDialog(boolean newLayer) {
        this.newLayer = newLayer;
    }

    public void show() {
        String title = newLayer ? "Create Layer" : "Edit Layer";

        JTextField layerNameField = new JTextField(layerName);

        JComponent[] inputs = {
                new JLabel("Layer Name:"),
                layerNameField
        };

        int result = JOptionPane.showConfirmDialog(null, inputs, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        this.layerName = layerNameField.getText();
        this.accepted = result == JOptionPane.YES_OPTION;
    }
}
