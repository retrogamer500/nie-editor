package net.loganford.nieEditor.actions.actionImpl;

import net.loganford.nieEditor.actions.Action;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.ui.EditorWindow;

public class EditLayer implements Action {

    private EditorWindow editorWindow;
    private Layer layer;

    private String oldName;
    private String newName;

    public EditLayer(EditorWindow editorWindow, Layer layer, String newName) {
        this.editorWindow = editorWindow;
        this.layer = layer;

        this.oldName = layer.getName();
        this.newName = newName;
    }

    @Override
    public void perform() {
        layer.setName(newName);
        editorWindow.getListeners().forEach(l -> l.layersChanged(editorWindow.getSelectedRoom()));
    }

    @Override
    public void undo() {
        layer.setName(oldName);
        editorWindow.getListeners().forEach(l -> l.layersChanged(editorWindow.getSelectedRoom()));
    }

    @Override
    public String toString() {
        return "Edit Layer";
    }
}
