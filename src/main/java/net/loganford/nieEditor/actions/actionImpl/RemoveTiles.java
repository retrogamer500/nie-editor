package net.loganford.nieEditor.actions.actionImpl;

import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.Window;
import net.loganford.nieEditor.util.TilePlacement;

import java.util.List;

public class RemoveTiles extends PlaceTiles {

    public RemoveTiles(Window window, Room room, Layer layer, List<TilePlacement> tilesToAdd, List<TilePlacement> tilesToRemove) {
        super(window, room, layer, tilesToAdd, tilesToRemove);
    }

    @Override
    public String toString() {
        return "Tiles Removed";
    }
}
