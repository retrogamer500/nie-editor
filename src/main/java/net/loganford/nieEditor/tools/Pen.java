package net.loganford.nieEditor.tools;

import net.loganford.nieEditor.actions.actionImpl.RemoveEntities;
import net.loganford.nieEditor.data.Entity;
import net.loganford.nieEditor.data.EntityDefinition;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.EditorWindow;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Pen extends Tool{
    private List<Entity> entitiesToAdd;
    private List<Entity> entitiesToRemove;

    private java.awt.Rectangle inactiveZone;

    public Pen(EditorWindow editorWindow, Room room, Layer layer, EntityDefinition selectedEntity, boolean isEntity, boolean isLeftClick) {
        super(editorWindow, room, layer, selectedEntity, isEntity, isLeftClick);

        entitiesToAdd = new ArrayList<>();
        entitiesToRemove = new ArrayList<>();
    }

    @Override
    public void mousePressed(int x, int y) {
        if(isEntity()) {
            if (isLeftClick()) {

            } else {
                removeEntitiesAt(x, y);
            }
        }
    }

    @Override
    public void mouseMoved(int x, int y) {
        if(isEntity()) {
            if (isLeftClick()) {

            } else {
                removeEntitiesAt(x, y);
            }
        }
    }

    @Override
    public void mouseReleased(int x, int y) {
        if(isEntity()) {
            if (isLeftClick()) {

            } else {
                RemoveEntities removeEntities = new RemoveEntities(getEditorWindow(), getRoom(), getLayer(), null, entitiesToRemove);
                getRoom().getActionPerformer().perform(getEditorWindow(), removeEntities);
                entitiesToRemove.forEach(e -> e.setHidden(false));
            }
        }
    }

    @Override
    public void render(Graphics g) {

    }

    @Override
    public void cancelTool(int x, int y) {
        entitiesToRemove.forEach(e -> e.setHidden(false));
    }

    private void removeEntitiesAt(int x, int y) {
        List<Entity> hitEntities = new ArrayList<>();
        for(Entity e: getLayer().getEntities()) {
            if(!e.isHidden()) {
                EntityDefinition def = getEditorWindow().getProject().getEntityInfo(e);
                if (e.collidesWith(def, x, y)) {
                    hitEntities.add(e);
                    e.setHidden(true);
                }
            }
        }

        entitiesToRemove.addAll(hitEntities);
    }
}
