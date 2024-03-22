package net.loganford.nieEditor.actions.actionImpl;

import net.loganford.nieEditor.actions.Action;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.EditorWindow;

public class AddLayer implements Action {

    private EditorWindow editorWindow;
    private Room room;
    private int position;
    private String layerName;

    public AddLayer(EditorWindow editorWindow, Room room, String layerName, int position) {
        this.editorWindow = editorWindow;
        this.room = room;
        this.layerName = layerName;
        this.position = position;
    }

    @Override
    public void perform() {
        Layer layer = new Layer();
        layer.setName(layerName);
        room.getLayerList().add(position, layer);

        editorWindow.getListeners().forEach(l -> l.layersChanged(room));
    }

    @Override
    public void undo() {
        room.getLayerList().remove(position);
        editorWindow.getListeners().forEach(l -> l.layersChanged(room));
    }

    @Override
    public String toString() {
        return "Add Layer";
    }
}
