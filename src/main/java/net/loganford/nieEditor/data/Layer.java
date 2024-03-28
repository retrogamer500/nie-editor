package net.loganford.nieEditor.data;

import lombok.Getter;
import lombok.Setter;
import net.loganford.nieEditor.ui.Window;

import java.util.ArrayList;
import java.util.List;

public class Layer {
    @Getter @Setter private transient boolean visible = true;

    @Getter @Setter private String name = "New Layer";
    @Getter @Setter private List<Entity> entities = new ArrayList<>();
    @Getter @Setter private String tilesetUuid = null;

    @Override
    public String toString() {
        if(visible) {
            return name;
        }
        else {
            return name + " (HIDDEN)";
        }
    }

    public Tileset getTileset(Window window) {
        return window.getProject().getTileset(tilesetUuid);
    }
}
