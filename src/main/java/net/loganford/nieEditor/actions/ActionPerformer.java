package net.loganford.nieEditor.actions;

import lombok.Getter;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.EditorWindow;

import java.util.ArrayList;
import java.util.List;

public class ActionPerformer {

    public static int MAX_HISTORY = 256;

    private Room room;

    @Getter private List<Action> actionList = new ArrayList<>();
    @Getter private int lastActionIndex = -1;

    public ActionPerformer(Room room) {
        this.room = room;
    }

    public void perform(EditorWindow editorWindow, Action action) {
        actionList = actionList.subList(0, lastActionIndex + 1);

        action.perform();

        actionList.add(action);
        lastActionIndex++;

        if(actionList.size() > MAX_HISTORY) {
            actionList.remove(0);
            lastActionIndex--;
        }

        editorWindow.getListeners().forEach(l -> l.historyChanged(room));
    }

    public void undo(EditorWindow editorWindow) {
        if(lastActionIndex < 0) {
            return;
        }

        actionList.get(lastActionIndex).undo();
        lastActionIndex--;

        editorWindow.getListeners().forEach(l -> l.historyChanged(room));
    }

    public void redo(EditorWindow editorWindow) {
        if(lastActionIndex < actionList.size() - 1) {
            lastActionIndex++;
            actionList.get(lastActionIndex).perform();

            editorWindow.getListeners().forEach(l -> l.historyChanged(room));
        }
    }
}
