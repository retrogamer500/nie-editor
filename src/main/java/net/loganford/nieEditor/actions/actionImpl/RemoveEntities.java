package net.loganford.nieEditor.actions.actionImpl;

import net.loganford.nieEditor.data.Entity;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.EditorWindow;

import java.util.List;

public class RemoveEntities extends PlaceEntities {

    public RemoveEntities(EditorWindow editorWindow, Room room, Layer layer, List<Entity> entitiesToAdd, List<Entity> entitiesToRemove) {
        super(editorWindow, room, layer, entitiesToAdd, entitiesToRemove);
    }

    @Override
    public String toString() {
        return "Entities Removed";
    }
}
