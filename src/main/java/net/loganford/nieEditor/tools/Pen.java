package net.loganford.nieEditor.tools;

import net.loganford.nieEditor.actions.actionImpl.PlaceEntities;
import net.loganford.nieEditor.actions.actionImpl.RemoveEntities;
import net.loganford.nieEditor.data.Entity;
import net.loganford.nieEditor.data.EntityDefinition;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.Window;

import java.awt.*;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Pen extends Tool{
    private List<Entity> entitiesToAdd;
    private List<Entity> entitiesToRemove;

    private java.awt.Rectangle inactiveZone;

    public Pen(Window window, Room room, Layer layer, EntityDefinition selectedEntity, boolean isEntity, boolean isLeftClick) {
        super(window, room, layer, selectedEntity, isEntity, isLeftClick);

        entitiesToAdd = new ArrayList<>();
        entitiesToRemove = new ArrayList<>();
    }

    @Override
    public void mousePressed(int x, int y) {


        if(isEntity()) {
            if (isLeftClick()) {
               addEntityAt(x, y);
            } else {
                removeEntitiesAt(x, y);
            }
        }
    }

    @Override
    public void mouseMoved(int x, int y) {
        if(isEntity()) {
            if (isLeftClick()) {
                addEntityAt(x, y);
            } else {
                removeEntitiesAt(x, y);
            }
        }
    }

    @Override
    public void mouseReleased(int x, int y) {
        if(isEntity()) {
            if (isLeftClick()) {
                PlaceEntities placeEntities = new PlaceEntities(getWindow(), getRoom(), getLayer(), entitiesToAdd, entitiesToRemove);
                getRoom().getActionPerformer().perform(getWindow(), placeEntities);
            } else {
                RemoveEntities removeEntities = new RemoveEntities(getWindow(), getRoom(), getLayer(), null, entitiesToRemove);
                getRoom().getActionPerformer().perform(getWindow(), removeEntities);
            }
            entitiesToRemove.forEach(e -> e.setHidden(false));
        }
    }

    @Override
    public void renderOnLayer(Graphics g) {
        for(Entity e: entitiesToAdd) {
            e.render(getWindow(), g);
        }
    }

    @Override
    public void render(Graphics g) {

    }

    @Override
    public void cancelTool(int x, int y) {
        entitiesToRemove.forEach(e -> e.setHidden(false));
    }

    private void addEntityAt(int x, int y) {
        if(inactiveZone != null && inactiveZone.contains(new Point(x, y))) {
            return;
        }

        int snapX = (Integer) getWindow().getToolPane().getGridWidth().getValue();
        int snapY = (Integer) getWindow().getToolPane().getGridHeight().getValue();
        boolean isSnapped = getWindow().getToolPane().getSnapEntities().isSelected();
        boolean isOverwrite = getWindow().getToolPane().getOverwriteEntities().isSelected();

        int px = x;
        int py = y;
        EntityDefinition def = getWindow().getSelectedEntity();

        if(isSnapped) {
            px = (int) (Math.floor(((double)x)/snapX) * snapX);
            py = (int) (Math.floor(((double)y)/snapY) * snapY);
            inactiveZone = new Rectangle(px, py, def.getWidth(), def.getHeight());
        }
        else {
            inactiveZone = new Rectangle(px - def.getWidth(), py - def.getHeight(), def.getWidth() * 2, def.getHeight() * 2);
        }

        if(isOverwrite) {
            List<Entity> tempEntitiesToRemove = getEntitiesWithinBounds(new java.awt.Rectangle(px, py, def.getWidth(), def.getHeight()));
            tempEntitiesToRemove.forEach(e -> e.setHidden(true));
            entitiesToRemove.addAll(tempEntitiesToRemove);
        }

        entitiesToAdd.add(new Entity(def, px, py));
    }

    private void removeEntitiesAt(int x, int y) {
        List<Entity> hitEntities = new ArrayList<>();
        for(Entity e: getLayer().getEntities()) {
            if(!e.isHidden()) {
                EntityDefinition def = getWindow().getProject().getEntityCache(e);
                if (e.collidesWith(def, x, y)) {
                    hitEntities.add(e);
                    e.setHidden(true);
                }
            }
        }

        entitiesToRemove.addAll(hitEntities);
    }
}
