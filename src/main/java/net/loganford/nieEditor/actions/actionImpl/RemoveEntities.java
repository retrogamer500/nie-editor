package net.loganford.nieEditor.actions.actionImpl;

import net.loganford.nieEditor.data.Entity;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.Window;

import java.util.List;

public class RemoveEntities extends PlaceEntities {

    public RemoveEntities(Window window, Room room, Layer layer, List<Entity> entitiesToAdd, List<Entity> entitiesToRemove) {
        super(window, room, layer, entitiesToAdd, entitiesToRemove);
    }

    @Override
    public String toString() {
        return "Entities Removed";
    }
}
