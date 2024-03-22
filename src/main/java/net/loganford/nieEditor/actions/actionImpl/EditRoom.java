package net.loganford.nieEditor.actions.actionImpl;

import net.loganford.nieEditor.actions.Action;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.EditorWindow;
import net.loganford.nieEditor.ui.ProjectListener;

public class EditRoom implements Action {

    private EditorWindow editorWindow;
    private Room room;
    private String oldName, newName;
    private int oldWidth, newWidth;
    private int oldHeight, newHeight;


    public EditRoom(EditorWindow editorWindow, Room room, String newName, int newWidth, int newHeight) {
        this.editorWindow = editorWindow;
        this.room = room;
        this.newName = newName;
        this.newWidth = newWidth;
        this.newHeight = newHeight;

        this.oldName = room.getName();
        this.oldWidth = room.getWidth();
        this.oldHeight = room.getHeight();
    }

    @Override
    public void perform() {
        room.setName(newName);
        room.setWidth(newWidth);
        room.setHeight(newHeight);

        editorWindow.getListeners().forEach(ProjectListener::roomListChanged);
        editorWindow.getListeners().forEach(l -> l.roomSelectionChanged(editorWindow.getSelectedRoom()));
        editorWindow.setProjectDirty(true);
    }

    @Override
    public void undo() {
        room.setName(oldName);
        room.setWidth(oldWidth);
        room.setHeight(oldHeight);

        editorWindow.getListeners().forEach(ProjectListener::roomListChanged);
        editorWindow.getListeners().forEach(l -> l.roomSelectionChanged(editorWindow.getSelectedRoom()));
        editorWindow.setProjectDirty(true);
    }

    @Override
    public String toString() {
        return "Room Edited";
    }
}
