package net.loganford.nieEditor.actions.actionImpl;

import net.loganford.nieEditor.actions.Action;
import net.loganford.nieEditor.data.Entity;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.Window;

public class EditInstance implements Action {

    private Window window;
    private Room room;
    private Entity entity;

    private int oldX;
    private int newX;

    private int oldY;
    private int newY;

    public EditInstance(Window window, Room room, Entity entity, int newX, int newY) {
        this.window = window;
        this.room = room;
        this.entity = entity;

        oldX = entity.getX();
        oldY = entity.getY();

        this.newX = newX;
        this.newY = newY;
    }

    @Override
    public void perform() {
        entity.setX(newX);
        entity.setY(newY);

        window.getListeners().forEach(l -> l.selectedRoomChanged(room));
    }

    @Override
    public void undo() {
        entity.setX(oldX);
        entity.setY(oldY);

        window.getListeners().forEach(l -> l.selectedRoomChanged(room));
    }

    @Override
    public String toString() {
        return "Instance Edited";
    }
}
