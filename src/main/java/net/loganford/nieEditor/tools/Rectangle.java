package net.loganford.nieEditor.tools;

import net.loganford.nieEditor.data.EntityDefinition;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.EditorWindow;

import java.awt.*;

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
        if(isEntity()) {
            if (isLeftClick()) {
                //If overwrite entities, delete all objects within the bounds

                //Place objects in rectangle, relative to snap if inabled

            } else {
                //Delete all objects within the bounds
            }
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(new Color(Color.BLUE.getRed(), Color.BLUE.getGreen(), Color.BLUE.getBlue(), 64));
        g.fillRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
        g.setColor(Color.BLUE);
        g.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
    }
}
