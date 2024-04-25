package net.loganford.nieEditor.data;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Layer {
    @Getter @Setter private transient boolean visible = true;

    @Getter @Setter private String name = "New Layer";
    @Getter @Setter private List<Entity> entities = new ArrayList<>();
    @Getter @Setter private TileMap tileMap = new TileMap();

    @Override
    public String toString() {
        if(visible) {
            return name;
        }
        else {
            return name + " (HIDDEN)";
        }
    }

    public Layer duplicate(boolean cloneTiles, boolean cloneEntities) {
        Layer layer = new Layer();
        layer.setName(this.name);
        layer.getTileMap().setTilesetUuid(this.getTileMap().getTilesetUuid());

        if(cloneTiles) {
            layer.getTileMap().setWidth(this.getTileMap().getWidth());
            layer.getTileMap().setHeight(this.getTileMap().getHeight());
            layer.getTileMap().setTileData(this.getTileMap().getTileData().clone());
        }
        if(cloneEntities) {
            layer.setEntities(new ArrayList<>(this.getEntities()));
        }
        return layer;
    }
}
