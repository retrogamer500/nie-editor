package net.loganford.nieEditor.tools;

import lombok.Getter;
import net.loganford.nieEditor.data.Entity;
import net.loganford.nieEditor.data.EntityDefinition;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.Window;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Tool {
    @Getter private Window window;
    @Getter private Room room;
    @Getter private Layer layer;
    @Getter private EntityDefinition selectedEntity;
    @Getter private boolean isEntity;
    @Getter private boolean isLeftClick;

    public Tool(Window window, Room room, Layer layer, EntityDefinition selectedEntity, boolean isEntity, boolean isLeftClick) {
        this.window = window;
        this.room = room;
        this.layer = layer;
        this.selectedEntity = selectedEntity;
        this.isEntity = isEntity;
        this.isLeftClick = isLeftClick;
    }

    public void cancelTool(int x, int y) {}

    public abstract void mousePressed(int x, int y);
    public abstract void mouseMoved(int x, int y);
    public abstract void mouseReleased(int x, int y);
    public abstract void render(Graphics g);
    public void renderAboveEntities(Graphics g) {};
    public void renderBelowEntities(Graphics g) {};


    protected java.util.List<Entity> getEntitiesWithinBounds(java.awt.Rectangle rectangle) {
        List<Entity> entities = new ArrayList<Entity>();

        for(Entity entity : getLayer().getEntities()) {
            if(!entity.isHidden()) {
                if (entity.collidesWith(rectangle)) {
                    entities.add(entity);
                }
            }
        }

        return entities;
    }
}
