package net.loganford.nieEditor.tools;

import net.loganford.nieEditor.data.EntityDefinition;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.EditorWindow;

import java.awt.*;

public class Pen extends Tool{
    public Pen(EditorWindow editorWindow, Room room, Layer layer, EntityDefinition selectedEntity, boolean isEntity, boolean isLeftClick) {
        super(editorWindow, room, layer, selectedEntity, isEntity, isLeftClick);
    }

    @Override
    public void mousePressed(int x, int y) {

    }

    @Override
    public void mouseMoved(int x, int y) {

    }

    @Override
    public void mouseReleased(int x, int y) {

    }

    @Override
    public void render(Graphics g) {

    }
}
