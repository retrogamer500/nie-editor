package net.loganford.nieEditor.data;

import lombok.Getter;
import lombok.Setter;
import net.loganford.nieEditor.actions.ActionPerformer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Room {
    @Getter @Setter private transient ActionPerformer actionPerformer = new ActionPerformer(this);
    @Getter @Setter private transient Layer selectedLayer;

    @Getter @Setter private String name;
    @Getter @Setter private String group;
    @Getter @Setter private int width, height;
    @Getter @Setter private List<Layer> layerList = new ArrayList<>();

    @Getter @Setter private int bgColorR = 128;
    @Getter @Setter private int bgColorG = 128;
    @Getter @Setter private int bgColorB = 128;

    @Override
    public String toString() {
        return name;
    }

    public Color getBackgroundColor() {
        return new Color(bgColorR, bgColorG, bgColorB);
    }

    public void setBackgroundColor(Color color) {
        bgColorR = color.getRed();
        bgColorG = color.getGreen();
        bgColorB = color.getBlue();
    }

    public Room duplicate(String name) {
        Room room = new Room();
        room.setName(name);
        room.setGroup(this.getGroup());
        room.setWidth(this.width);
        room.setHeight(this.height);
        room.setBgColorR(this.getBgColorR());
        room.setBgColorG(this.getBgColorG());
        room.setBgColorB(this.getBgColorB());

        for(Layer layer: this.getLayerList()) {
            room.getLayerList().add(layer.duplicate());
        }

        return room;
    }
}
