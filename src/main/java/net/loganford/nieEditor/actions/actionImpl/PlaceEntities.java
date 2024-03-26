package net.loganford.nieEditor.actions.actionImpl;

import net.loganford.nieEditor.actions.Action;
import net.loganford.nieEditor.data.Entity;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.EditorWindow;

import java.util.List;

public class PlaceEntities implements Action {

    private EditorWindow editorWindow;
    private Room room;
    private Layer layer;

    private List<Entity> entitiesToAdd;
    private List<Entity> entitiesToRemove;

    public PlaceEntities(EditorWindow editorWindow, Room room, Layer layer, List<Entity> entitiesToAdd, List<Entity> entitiesToRemove) {
        this.editorWindow = editorWindow;
        this.room = room;
        this.layer = layer;
        this.entitiesToAdd = entitiesToAdd;
        this.entitiesToRemove = entitiesToRemove;
    }

    @Override
    public void perform() {
        if(entitiesToRemove != null) {
            layer.getEntities().removeAll(entitiesToRemove);
        }

        if(entitiesToAdd != null) {
            layer.getEntities().addAll(entitiesToAdd);
        }

        editorWindow.getListeners().forEach(l -> l.selectedRoomChanged(room));
    }

    @Override
    public void undo() {
        if(entitiesToAdd != null) {
            layer.getEntities().removeAll(entitiesToAdd);
        }

        if(entitiesToRemove != null) {
            layer.getEntities().addAll(entitiesToRemove);
        }

        editorWindow.getListeners().forEach(l -> l.selectedRoomChanged(room));
    }

    @Override
    public String toString() {
        return "Entities Added";
    }
}
