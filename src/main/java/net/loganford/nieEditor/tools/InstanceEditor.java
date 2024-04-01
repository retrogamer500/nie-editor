package net.loganford.nieEditor.tools;

import net.loganford.nieEditor.actions.actionImpl.EditInstance;
import net.loganford.nieEditor.data.Entity;
import net.loganford.nieEditor.data.EntityDefinition;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.Window;
import net.loganford.nieEditor.ui.dialog.InstanceEditDialog;

import java.awt.*;

public class InstanceEditor extends Tool {
    public InstanceEditor(Window window, Room room, Layer layer, EntityDefinition selectedEntity, boolean isEntity, boolean isLeftClick) {
        super(window, room, layer, selectedEntity, isEntity, isLeftClick);
    }

    @Override
    public void mousePressed(int x, int y) {

    }

    @Override
    public void mouseMoved(int x, int y) {

    }

    @Override
    public void mouseReleased(int x, int y) {
        Entity hit = null;

        for(Entity e: getLayer().getEntities()) {
            if(e.collidesWith(x, y)) {
                hit = e;
            }
        }

        if(hit != null) {
            InstanceEditDialog eed = new InstanceEditDialog(hit);
            eed.show();

            if(eed.isAccepted()) {
                EditInstance ee = new EditInstance(getWindow(), getRoom(), hit, eed.getX(), eed.getY(), eed.getProperties());
                getRoom().getActionPerformer().perform(getWindow(), ee);
            }
        }
    }

    @Override
    public void render(Graphics g) {

    }
}
