package net.loganford.nieEditor.actions.actionImpl;

import net.loganford.nieEditor.actions.Action;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.EditorWindow;

public class RemoveLayer implements Action {
    private EditorWindow editorWindow;
    private Room room;
    private Layer layer;
    private int layerPosition;

    public RemoveLayer(EditorWindow editorWindow, Room room, Layer layer) {
        this.editorWindow = editorWindow;
        this.room = room;

        this.layer = layer;
        this.layerPosition = room.getLayerList().indexOf(layer);
    }

    @Override
    public void perform() {
        room.getLayerList().remove(layerPosition);
        editorWindow.getListeners().forEach(l -> l.layersChanged(editorWindow.getSelectedRoom()));
        editorWindow.getSelectedRoom().setSelectedLayer(null);
    }

    @Override
    public void undo() {
        room.getLayerList().add(layerPosition, layer);
        editorWindow.getListeners().forEach(l -> l.layersChanged(editorWindow.getSelectedRoom()));
    }

    @Override
    public String toString() {
        return "Remove Layer";
    }
}
