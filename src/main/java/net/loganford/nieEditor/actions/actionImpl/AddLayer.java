package net.loganford.nieEditor.actions.actionImpl;

import net.loganford.nieEditor.actions.Action;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.EditorWindow;

public class AddLayer implements Action {

    private EditorWindow editorWindow;
    private Room room;
    private int position;

    public AddLayer(EditorWindow editorWindow, Room room, int position) {
        this.editorWindow = editorWindow;
        this.position = position;
        this.room = room;
    }

    @Override
    public void perform() {
        room.getLayerList().add(position, new Layer());

        editorWindow.getListeners().forEach(l -> l.layersChanged(room));
        editorWindow.setProjectDirty(true);
    }

    @Override
    public void undo() {
        room.getLayerList().remove(position);
        editorWindow.getListeners().forEach(l -> l.layersChanged(room));
        editorWindow.setProjectDirty(true);
    }

    @Override
    public String toString() {
        return "Add Layer";
    }
}
