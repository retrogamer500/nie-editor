package net.loganford.nieEditor.actions.actionImpl;

import net.loganford.nieEditor.actions.Action;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.data.TileMap;
import net.loganford.nieEditor.ui.Window;
import org.apache.commons.lang3.StringUtils;

public class EditLayer implements Action {

    private Window window;
    private Layer layer;

    private String oldName;
    private String newName;

    private String oldTilesetUuid;
    private String newTilesetUuid;

    private TileMap oldTilemap;

    public EditLayer(Window window, Layer layer, String newName, String newTilesetUuid) {
        this.window = window;
        this.layer = layer;

        this.oldName = layer.getName();
        this.newName = newName;

        if(layer.getTileMap() != null) {
            this.oldTilesetUuid = layer.getTileMap().getTilesetUuid();
        }
        this.newTilesetUuid = newTilesetUuid;

        if(oldTilesetUuid != null && !oldTilesetUuid.equals(newTilesetUuid)) {
            oldTilemap = layer.getTileMap();
        }
    }

    @Override
    public void perform() {
        layer.setName(newName);
        if(!StringUtils.equals(oldTilesetUuid, newTilesetUuid)) {
            layer.setTileMap(new TileMap(newTilesetUuid));
        }
        window.getListeners().forEach(l -> l.layersChanged(window.getSelectedRoom()));
        window.getListeners().forEach(l -> l.selectedRoomChanged(window.getSelectedRoom()));
    }

    @Override
    public void undo() {
        layer.setName(oldName);
        if(oldTilemap != null) {
            layer.setTileMap(oldTilemap);
        }
        else {
            layer.setTileMap(new TileMap());
        }
        window.getListeners().forEach(l -> l.layersChanged(window.getSelectedRoom()));
        window.getListeners().forEach(l -> l.selectedRoomChanged(window.getSelectedRoom()));
    }

    @Override
    public String toString() {
        return "Edit Layer";
    }
}
