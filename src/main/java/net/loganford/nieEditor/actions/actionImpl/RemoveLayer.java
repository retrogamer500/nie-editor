package net.loganford.nieEditor.actions.actionImpl;

import net.loganford.nieEditor.actions.Action;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.Window;

public class RemoveLayer implements Action {
    private Window window;
    private Room room;
    private Layer layer;
    private int layerPosition;

    public RemoveLayer(Window window, Room room, Layer layer) {
        this.window = window;
        this.room = room;

        this.layer = layer;
        this.layerPosition = room.getLayerList().indexOf(layer);
    }

    @Override
    public void perform() {
        room.getLayerList().remove(layerPosition);
        window.getListeners().forEach(l -> l.layersChanged(window.getSelectedRoom()));
        window.getSelectedRoom().setSelectedLayer(null);
    }

    @Override
    public void undo() {
        room.getLayerList().add(layerPosition, layer);
        window.getListeners().forEach(l -> l.layersChanged(window.getSelectedRoom()));
    }

    @Override
    public String toString() {
        return "Remove Layer";
    }
}
