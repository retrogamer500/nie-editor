package net.loganford.nieEditor.actions.actionImpl;

import net.loganford.nieEditor.actions.Action;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.EditorWindow;

public class MoveLayerDown implements Action {
    private EditorWindow editorWindow;
    private Room room;
    private int layerPosition;

    public MoveLayerDown(EditorWindow editorWindow, Room room, Layer layer) {
        this.editorWindow = editorWindow;
        this.room = room;

        this.layerPosition = room.getLayerList().indexOf(layer);
    }

    @Override
    public void perform() {
        if(layerPosition < room.getLayerList().size() - 1) {
            Layer layer = room.getLayerList().get(layerPosition );
            Layer otherLayer = room.getLayerList().get(layerPosition + 1);

            room.getLayerList().set(layerPosition + 1, layer);
            room.getLayerList().set(layerPosition, otherLayer);
        }

        editorWindow.getListeners().forEach(l -> l.layersChanged(editorWindow.getSelectedRoom()));
    }

    @Override
    public void undo() {
        if(layerPosition < room.getLayerList().size() - 1) {
            Layer layer = room.getLayerList().get(layerPosition );
            Layer otherLayer = room.getLayerList().get(layerPosition + 1);

            room.getLayerList().set(layerPosition + 1, layer);
            room.getLayerList().set(layerPosition, otherLayer);
        }

        editorWindow.getListeners().forEach(l -> l.layersChanged(editorWindow.getSelectedRoom()));
    }

    @Override
    public String toString() {
        return "Move Layer Down";
    }
}
