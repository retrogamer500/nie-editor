package net.loganford.nieEditor.util;

import net.loganford.nieEditor.data.Project;
import net.loganford.nieEditor.data.Room;

public interface ProjectListener {
    default void projectChanged(Project project) {}
    default void selectedRoomChanged(Room room) {}
    default void roomListChanged() {}
    default void historyChanged(Room room) {}
    default void layersChanged(Room room) {}
    default void tilesetsChanged() {}
    default void entitiesChanged() {}
    default void layerSelectionChanged() {}
    default void tilePickerSettingsChanged(int zoom, boolean showGrid) {}
    default void leftTabChanged() {}
}
