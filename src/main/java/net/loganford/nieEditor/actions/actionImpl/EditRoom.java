package net.loganford.nieEditor.actions.actionImpl;

import net.loganford.nieEditor.actions.Action;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.Window;
import net.loganford.nieEditor.util.ProjectListener;

import java.awt.*;

public class EditRoom implements Action {

    private Window window;
    private Room room;
    private String oldName, newName;
    private int oldWidth, newWidth;
    private int oldHeight, newHeight;
    private Color oldBgColor;
    private Color newBgColor;


    public EditRoom(Window window, Room room, String newName, int newWidth, int newHeight, Color newBgColor) {
        this.window = window;
        this.room = room;
        this.newName = newName;
        this.newWidth = newWidth;
        this.newHeight = newHeight;
        this.newBgColor = newBgColor;

        this.oldName = room.getName();
        this.oldWidth = room.getWidth();
        this.oldHeight = room.getHeight();
        this.oldBgColor = room.getBackgroundColor();
    }

    @Override
    public void perform() {
        room.setName(newName);
        room.setWidth(newWidth);
        room.setHeight(newHeight);
        room.setBackgroundColor(newBgColor);

        window.getListeners().forEach(ProjectListener::roomListChanged);
        window.getListeners().forEach(l -> l.selectedRoomChanged(window.getSelectedRoom()));
    }

    @Override
    public void undo() {
        room.setName(oldName);
        room.setWidth(oldWidth);
        room.setHeight(oldHeight);
        room.setBackgroundColor(oldBgColor);

        window.getListeners().forEach(ProjectListener::roomListChanged);
        window.getListeners().forEach(l -> l.selectedRoomChanged(window.getSelectedRoom()));
    }

    @Override
    public String toString() {
        return "Room Edited";
    }
}
