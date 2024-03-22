package net.loganford.nieEditor.actions.actionImpl;

import net.loganford.nieEditor.actions.Action;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.EditorWindow;

public class RemoveLayer implements Action {
    private EditorWindow editorWindow;
    private Room room;

    public RemoveLayer(EditorWindow editorWindow, Room room) {
        this.editorWindow = editorWindow;
        this.room = room;
    }

    @Override
    public void perform() {

    }

    @Override
    public void undo() {

    }
}
