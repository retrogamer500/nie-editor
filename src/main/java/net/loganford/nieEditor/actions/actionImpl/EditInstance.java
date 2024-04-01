package net.loganford.nieEditor.actions.actionImpl;

import net.loganford.nieEditor.actions.Action;
import net.loganford.nieEditor.data.Entity;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.Window;

import java.util.HashMap;

public class EditInstance implements Action {

    private Window window;
    private Room room;
    private Entity entity;

    private int oldX;
    private int newX;

    private int oldY;
    private int newY;

    private HashMap<String, String> oldProperties;
    private HashMap<String, String> newProperties;

    public EditInstance(Window window, Room room, Entity entity, int newX, int newY, HashMap<String, String> newProperties) {
        this.window = window;
        this.room = room;
        this.entity = entity;

        oldX = entity.getX();
        oldY = entity.getY();

        this.newX = newX;
        this.newY = newY;

        this.oldProperties = entity.getProperties();
        this.newProperties = newProperties;
    }

    @Override
    public void perform() {
        entity.setX(newX);
        entity.setY(newY);
        entity.setProperties(newProperties);

        window.getListeners().forEach(l -> l.selectedRoomChanged(room));
    }

    @Override
    public void undo() {
        entity.setX(oldX);
        entity.setY(oldY);
        entity.setProperties(oldProperties);

        window.getListeners().forEach(l -> l.selectedRoomChanged(room));
    }

    @Override
    public String toString() {
        return "Instance Edited";
    }
}
