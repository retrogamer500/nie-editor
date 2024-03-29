package net.loganford.nieEditor.actions.actionImpl;

import net.loganford.nieEditor.actions.Action;
import net.loganford.nieEditor.data.Entity;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.Window;
import net.loganford.nieEditor.util.TilePlacement;

import java.util.List;

public class PlaceTiles implements Action {
    private Window window;
    private Room room;
    private Layer layer;

    private List<TilePlacement> tilesToAdd;
    private List<TilePlacement> tilesToRemove;

    public PlaceTiles(Window window, Room room, Layer layer, List<TilePlacement> tilesToAdd, List<TilePlacement> tilesToRemove) {
        this.window = window;
        this.room = room;
        this.layer = layer;
        this.tilesToAdd = tilesToAdd;
        this.tilesToRemove = tilesToRemove;
    }

    @Override
    public void perform() {
        if(tilesToRemove != null) {
            for(TilePlacement tp: tilesToRemove) {
                layer.getTileMap().removeTile(tp.getX(), tp.getY());
            }
        }
        if(tilesToAdd != null) {
            for(TilePlacement tp: tilesToAdd) {
                layer.getTileMap().placeTile(tp.getX(), tp.getY(), tp.getTileX(), tp.getTileY());
            }
        }

        window.getListeners().forEach(l -> l.selectedRoomChanged(room));
    }

    @Override
    public void undo() {
        if(tilesToAdd != null) {
            for(TilePlacement tp: tilesToAdd) {
                layer.getTileMap().removeTile(tp.getX(), tp.getY());
            }
        }

        if(tilesToRemove != null) {
            for(TilePlacement tp: tilesToRemove) {
                layer.getTileMap().placeTile(tp.getX(), tp.getY(), tp.getTileX(), tp.getTileY());
            }
        }

        window.getListeners().forEach(l -> l.selectedRoomChanged(room));
    }

    @Override
    public String toString() {
        return "Tiles Added";
    }
}
