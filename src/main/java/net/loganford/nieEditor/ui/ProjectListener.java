package net.loganford.nieEditor.ui;

import net.loganford.nieEditor.data.Project;
import net.loganford.nieEditor.data.Room;

public interface ProjectListener {
    default void projectChanged(Project project) {}
    default void roomSelectionChanged(Room room) {}
    default void roomListChanged() {}
    default void historyChanged(Room room) {}
    default void layersChanged(Room room) {}
}
