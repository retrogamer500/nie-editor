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

    public Layer duplicate() {
        Layer layer = new Layer();
        layer.setName(this.name);
        layer.getTileMap().setTilesetUuid(this.getTileMap().getTilesetUuid());
        return layer;
    }
}
