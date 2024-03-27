package net.loganford.nieEditor.actions.actionImpl;

import net.loganford.nieEditor.actions.Action;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.Window;

public class AddLayer implements Action {

    private Window window;
    private Room room;
    private int position;
    private String layerName;

    public AddLayer(Window window, Room room, String layerName, int position) {
        this.window = window;
        this.room = room;
        this.layerName = layerName;
        this.position = position;
    }

    @Override
    public void perform() {
        Layer layer = new Layer();
        layer.setName(layerName);
        room.getLayerList().add(position, layer);

        window.getListeners().forEach(l -> l.layersChanged(room));
    }

    @Override
    public void undo() {
        room.getLayerList().remove(position);
        window.getListeners().forEach(l -> l.layersChanged(room));
    }

    @Override
    public String toString() {
        return "Add Layer";
    }
}
