package net.loganford.nieEditor.actions;

import lombok.Getter;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.Window;

import java.util.ArrayList;
import java.util.List;

public class ActionPerformer {

    public static int MAX_HISTORY = 32;

    private Room room;

    @Getter private List<Action> actionList = new ArrayList<>();
    @Getter private int lastActionIndex = -1;

    public ActionPerformer(Room room) {
        this.room = room;
    }

    public void clearHistory() {
        actionList = new ArrayList<>();
        lastActionIndex = -1;
    }

    public void perform(Window window, Action action) {
        actionList = actionList.subList(0, lastActionIndex + 1);

        action.perform();

        actionList.add(action);
        lastActionIndex++;

        if(actionList.size() > MAX_HISTORY) {
            actionList.remove(0);
            lastActionIndex--;
        }

        window.getListeners().forEach(l -> l.historyChanged(room));
        window.setProjectDirty(true);
    }

    public void undo(Window window) {
        if(lastActionIndex < 0) {
            return;
        }

        actionList.get(lastActionIndex).undo();
        lastActionIndex--;

        window.getListeners().forEach(l -> l.historyChanged(room));
        window.setProjectDirty(true);
    }

    public void redo(Window window) {
        if(lastActionIndex < actionList.size() - 1) {
            lastActionIndex++;
            actionList.get(lastActionIndex).perform();

            window.getListeners().forEach(l -> l.historyChanged(room));
            window.setProjectDirty(true);
        }
    }
}
