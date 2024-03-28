package net.loganford.nieEditor.ui.dialog;

import lombok.Getter;
import lombok.Setter;
import net.loganford.nieEditor.data.Tileset;
import net.loganford.nieEditor.ui.Window;

import javax.swing.*;

public class LayerDialog {
    private Window window;
    private boolean newLayer;

    @Getter
    private boolean accepted = false;
    @Getter @Setter private String layerName = "New Layer";
    @Getter @Setter private String tilesetUuid = null;

    public LayerDialog(Window window, boolean newLayer) {
        this.window = window;
        this.newLayer = newLayer;
    }

    public void show() {
        String title = newLayer ? "Create Layer" : "Edit Layer";

        JTextField layerNameField = new JTextField(layerName);
        JComboBox<Tileset> tilesetCb = new JComboBox<>(window.getProject().getTilesets().toArray(new Tileset[] {}));
        tilesetCb.insertItemAt(null, 0);

        if(tilesetUuid != null) {
            tilesetCb.setSelectedItem(window.getProject().getTileset(tilesetUuid));
        }

        tilesetCb.setSelectedItem(tilesetUuid);

        JComponent[] inputs = {
                new JLabel("Layer Name:"),
                layerNameField,
                new JLabel("Tileset:"),
                tilesetCb
        };

        int result = JOptionPane.showConfirmDialog(null, inputs, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        this.layerName = layerNameField.getText();
        if(tilesetCb.getSelectedItem() != null) {
            this.tilesetUuid = ((Tileset) tilesetCb.getSelectedItem()).getUuid();
        }
        else {
            this.tilesetUuid = null;
        }
        this.accepted = result == JOptionPane.YES_OPTION;
    }
}
