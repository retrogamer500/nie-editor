package net.loganford.nieEditor.tools;

import lombok.Getter;
import lombok.Setter;
import net.loganford.nieEditor.data.EntityDefinition;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.EditorWindow;

import java.awt.*;

public abstract class Tool {
    @Getter private EditorWindow editorWindow;
    @Getter private Room room;
    @Getter private Layer layer;
    @Getter private EntityDefinition selectedEntity;
    @Getter private boolean isEntity;
    @Getter private boolean isLeftClick;

    public Tool(EditorWindow editorWindow, Room room, Layer layer, EntityDefinition selectedEntity, boolean isEntity, boolean isLeftClick) {
        this.editorWindow = editorWindow;
        this.room = room;
        this.layer = layer;
        this.selectedEntity = selectedEntity;
        this.isEntity = isEntity;
        this.isLeftClick = isLeftClick;
    }

    public abstract void mousePressed(int x, int y);
    public abstract void mouseMoved(int x, int y);
    public abstract void mouseReleased(int x, int y);
    public abstract void render(Graphics g);
}
