package net.loganford.nieEditor.tools;

import net.loganford.nieEditor.actions.actionImpl.PlaceEntities;
import net.loganford.nieEditor.actions.actionImpl.RemoveEntities;
import net.loganford.nieEditor.data.Entity;
import net.loganford.nieEditor.data.EntityDefinition;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.EditorWindow;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Rectangle extends Tool {

    private int x1, y1, x2, y2;

    public Rectangle(EditorWindow editorWindow, Room room, Layer layer, EntityDefinition selectedEntity, boolean isEntity, boolean isLeftClick) {
        super(editorWindow, room, layer, selectedEntity, isEntity, isLeftClick);
    }

    @Override
    public void mousePressed(int x, int y) {
        x1 = x;
        y1 = y;
        x2 = x;
        y2 = y;
    }

    @Override
    public void mouseMoved(int x, int y) {
        x2 = x;
        y2 = y;
    }

    @Override
    public void mouseReleased(int x, int y) {
        x2 = x;
        y2 = y;
        int snapX = (Integer)getEditorWindow().getToolPane().getGridWidth().getValue();
        int snapY = (Integer)getEditorWindow().getToolPane().getGridHeight().getValue();
        boolean isSnapped = getEditorWindow().getToolPane().getSnapEntities().isSelected();
        boolean isOverwrite = getEditorWindow().getToolPane().getOverwriteEntities().isSelected();
        if(x2 < x1) {
            int tx = x1;
            x1 = x2;
            x2 = tx;
        }
        if(y2 < y1) {
            int ty = y1;
            y1 = y2;
            y2 = ty;
        }
        if(isSnapped) {
            x1 = (int) (Math.floor(((double)x1)/snapX) * snapX);
            y1 = (int) (Math.floor(((double)y1)/snapY) * snapY);
            x2 = (int) (Math.ceil(((double)x2)/snapX) * snapX);
            y2 = (int) (Math.ceil(((double)y2)/snapY) * snapY);
        }


        if(isEntity()) {
            if (isLeftClick()) {
                EntityDefinition def = getEditorWindow().getSelectedEntity();
                List<Entity> entitiesToAdd = new ArrayList<Entity>();
                List<Entity> entitiesToRemove = new ArrayList<>();

                //If overwrite entities, delete all objects within the bounds
                if(isOverwrite) {
                    int width = (int)(Math.ceil(((double)(x2 - x1)) / def.getWidth()) * def.getWidth());
                    int height = (int)(Math.ceil(((double)(y2 - y1)) / def.getHeight()) * def.getHeight());
                    entitiesToRemove = getEntitiesWithinBounds(new java.awt.Rectangle(x1, y1, width, height));
                }

                //Place objects in rectangle, relative to snap if enabled

                if(isSnapped) {
                    for(int i = x1; i < x2; i+= snapX) {
                        for(int j = y1; j < y2; j+= snapY) {
                            entitiesToAdd.add(new Entity(def, i, j));
                        }
                    }
                }
                else {
                    for(int i = x1; i < x2; i+= def.getWidth()) {
                        for(int j = y1; j < y2; j+= def.getHeight()) {
                            entitiesToAdd.add(new Entity(def, i, j));
                        }
                    }
                }

                PlaceEntities placeEntities = new PlaceEntities(getEditorWindow(), getRoom(), getLayer(), entitiesToAdd, entitiesToRemove);
                getRoom().getActionPerformer().perform(getEditorWindow(), placeEntities);


            } else {
                //Delete all objects within the bounds
                List<Entity> entitiesToRemove = getEntitiesWithinBounds(new java.awt.Rectangle(x1, y1, x2 - x1, y2 - y1));
                RemoveEntities removeEntities = new RemoveEntities(getEditorWindow(), getRoom(), getLayer(), null, entitiesToRemove);
                getRoom().getActionPerformer().perform(getEditorWindow(), removeEntities);
            }
        }
    }

    private List<Entity> getEntitiesWithinBounds(java.awt.Rectangle rectangle) {
        List<Entity> entities = new ArrayList<Entity>();

        for(Entity entity : getLayer().getEntities()) {
            EntityDefinition ed = getEditorWindow().getProject().getEntityInfo(entity);
            if(entity.collidesWith(ed, rectangle)) {
                entities.add(entity);
            }
        }

        return entities;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(new Color(Color.BLUE.getRed(), Color.BLUE.getGreen(), Color.BLUE.getBlue(), 64));
        g.fillRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
        g.setColor(Color.BLUE);
        g.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
    }
}
