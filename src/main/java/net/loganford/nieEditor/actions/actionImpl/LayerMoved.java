package net.loganford.nieEditor.actions.actionImpl;

import net.loganford.nieEditor.actions.Action;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.Window;

import java.util.ArrayList;
import java.util.List;

public class LayerMoved implements Action {
    private Window window;
    private Room room;

    private ArrayList<Layer> layersBefore;
    private ArrayList<Layer> layersAfter;

    public LayerMoved(Window window, Room room, List<Layer> layersBefore, List<Layer> layersAfter) {
        this.window = window;
        this.room = room;

        this.layersBefore = new ArrayList<>(layersBefore);
        this.layersAfter = new ArrayList<>(layersAfter);
    }

    @Override
    public void perform() {
        room.setLayerList(layersAfter);

        window.getListeners().forEach(l -> l.layersChanged(window.getSelectedRoom()));
    }

    @Override
    public void undo() {
        room.setLayerList(layersBefore);

        window.getListeners().forEach(l -> l.layersChanged(window.getSelectedRoom()));
    }

    @Override
    public String toString() {
        return "Layers Moved";
    }
}
