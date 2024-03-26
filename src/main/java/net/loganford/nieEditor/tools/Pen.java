package net.loganford.nieEditor.tools;

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

    public Pen(EditorWindow editorWindow, Room room, Layer layer, EntityDefinition selectedEntity, boolean isEntity, boolean isLeftClick) {
        super(editorWindow, room, layer, selectedEntity, isEntity, isLeftClick);

        entitiesToAdd = new ArrayList<>();
        entitiesToRemove = new ArrayList<>();
    }

    @Override
    public void mousePressed(int x, int y) {
        if(isLeftClick()) {

        }
        else {

        }
    }

    @Override
    public void mouseMoved(int x, int y) {
        if(isLeftClick()) {

        }
        else {

        }
    }

    @Override
    public void mouseReleased(int x, int y) {

    }

    @Override
    public void render(Graphics g) {

    }

    private void removeEntitiesAt(int x, int y) {

    }
}
