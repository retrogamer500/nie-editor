package net.loganford.nieEditor.data;

import lombok.Getter;
import lombok.Setter;
import net.loganford.nieEditor.actions.ActionPerformer;

import java.util.ArrayList;
import java.util.List;

public class Room {
    @Getter @Setter private transient ActionPerformer actionPerformer = new ActionPerformer(this);
    @Getter @Setter private transient Layer selectedLayer;

    @Getter @Setter private String name;
    @Getter @Setter private int width, height;
    @Getter @Setter private List<Layer> layerList = new ArrayList<>();

    @Override
    public String toString() {
        return name;
    }
}
