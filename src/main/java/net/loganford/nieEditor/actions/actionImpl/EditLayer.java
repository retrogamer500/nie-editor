package net.loganford.nieEditor.actions.actionImpl;

import net.loganford.nieEditor.actions.Action;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.ui.Window;

public class EditLayer implements Action {

    private Window window;
    private Layer layer;

    private String oldName;
    private String newName;

    private String oldTilesetUuid;
    private String newTilesetUuid;

    public EditLayer(Window window, Layer layer, String newName, String newTilesetUuid) {
        this.window = window;
        this.layer = layer;

        this.oldName = layer.getName();
        this.newName = newName;

        this.oldTilesetUuid = layer.getTilesetUuid();
        this.newTilesetUuid = newTilesetUuid;
    }

    @Override
    public void perform() {
        layer.setName(newName);
        layer.setTilesetUuid(newTilesetUuid);
        window.getListeners().forEach(l -> l.layersChanged(window.getSelectedRoom()));
    }

    @Override
    public void undo() {
        layer.setName(oldName);
        layer.setTilesetUuid(oldTilesetUuid);
        window.getListeners().forEach(l -> l.layersChanged(window.getSelectedRoom()));
    }

    @Override
    public String toString() {
        return "Edit Layer";
    }
}
