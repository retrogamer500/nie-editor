package net.loganford.nieEditor.ui.leftPane;

import net.loganford.nieEditor.data.EntityDefinition;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.data.Project;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.Window;
import net.loganford.nieEditor.util.FolderTree;
import net.loganford.nieEditor.util.ImageCache;
import net.loganford.nieEditor.util.ProjectListener;
import net.loganford.nieEditor.ui.dialog.EntityDialog;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;

public class EntitiesTab extends JPanel implements ActionListener, ProjectListener {

    private Window window;
    private DefaultMutableTreeNode root;
    private FolderTree<EntityDefinition> tree;

    public EntitiesTab(Window window) {
        this.window = window;
        window.getListeners().add(this);

        setLayout(new BorderLayout());

        //Setup entities list
        ScrollPane scrollPane = new ScrollPane();

        root = new DefaultMutableTreeNode("Entities");

        tree = new FolderTree<>(
                window,
                EntityDefinition.class,
                EntityDefinition::getGroup,
                (e) -> e.getImagePath() != null ? ImageCache.getInstance().getImage(window.getRelativeFile(e.getImagePath()), 14, 14) : ImageCache.getInstance().getImage(new File("./editor-data/obj.png"), 14, 14),
                window::setSelectedEntity
        );

        tree.setOnClickAction(this::editEntity);


        scrollPane.add(tree);
        add(scrollPane, BorderLayout.CENTER);

        //Setup buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3));

        JButton addButton = new JButton("Add");
        addButton.addActionListener(this);
        buttonPanel.add(addButton);

        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(this);
        buttonPanel.add(removeButton);

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(this);
        buttonPanel.add(editButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Add")) {
            EntityDialog ed = new EntityDialog(window, true);
            ed.show();
            if(ed.isAccepted()) {
                EntityDefinition def = new EntityDefinition();
                ImageCache.getInstance().clearCache(ed.getImageFile());
                updateEntity(def, ed);
                window.getProject().getEntityDefinitions().add(def);
                window.getListeners().forEach(ProjectListener::entitiesChanged);
                window.setProjectDirty(true);
            }
        }
        if(e.getActionCommand().equals("Edit") && !((DefaultMutableTreeNode)tree.getLastSelectedPathComponent()).getAllowsChildren()) {
            EntityDefinition def = window.getSelectedEntity();
            if(def != null) {
                editEntity(def);
            }
        }
        if(e.getActionCommand().equals("Remove")) {
            if(window.getSelectedEntity() != null && !((DefaultMutableTreeNode)tree.getLastSelectedPathComponent()).getAllowsChildren()) {
                int dialogResult = JOptionPane.showConfirmDialog (null, "Deleting this entity cannot be undone. This will delete this entity from all rooms and clear their undo histories. Are you sure?", "Warning", JOptionPane.YES_NO_OPTION);
                if(dialogResult == JOptionPane.YES_OPTION){
                    window.getProject().getEntityDefinitions().remove(window.getSelectedEntity());

                    for(Room room: window.getProject().getRooms()) {
                        for(Layer layer: room.getLayerList()) {
                            layer.getEntities().removeIf(ent -> ent.getEntityDefinitionUUID().equals(window.getSelectedEntity().getUuid()));
                        }
                        room.getActionPerformer().clearHistory();
                    }

                    window.setSelectedEntity(null);
                    window.getListeners().forEach(ProjectListener::entitiesChanged);
                    window.getListeners().forEach(l -> l.selectedRoomChanged(window.getSelectedRoom()));
                    window.setProjectDirty(true);
                }



            }
        }
    }

    public void editEntity(EntityDefinition def) {
        EntityDialog ed = new EntityDialog(window, false);

        ed.setName(def.getName());
        ed.setGroup(def.getGroup());
        ed.setWidth(def.getWidth());
        ed.setHeight(def.getHeight());
        ed.setClassPath(def.getClassPath());

        if(def.getImagePath() != null) {
            ed.setImageFile(window.getRelativeFile(def.getImagePath()));
        }
        ed.show();

        if(ed.isAccepted()) {
            ImageCache.getInstance().clearCache(ed.getImageFile());
            updateEntity(def, ed);
            window.getListeners().forEach(ProjectListener::entitiesChanged);
            if(window.getSelectedRoom() != null) {
                window.getListeners().forEach(l -> l.selectedRoomChanged(window.getSelectedRoom()));
            }
            window.setProjectDirty(true);
        }
    }

    @Override
    public void projectChanged(Project project) {
        renderEntities();
    }

    @Override
    public void entitiesChanged() {
        renderEntities();
    }

    public void updateEntity(EntityDefinition def, EntityDialog ed) {
        if(def.getUuid() == null) {
            //Entity definition is new, set new UUID
            def.setUuid(UUID.randomUUID().toString());
        }
        def.setName(ed.getName());
        def.setClassPath(ed.getClassPath());
        def.setGroup(ed.getGroup());
        def.setWidth(ed.getWidth());
        def.setHeight(ed.getHeight());

        if(ed.getImageFile() != null) {
            ImageCache.getInstance().clearCache(ed.getImageFile());
            def.setImagePath(window.getRelativeFilePath(ed.getImageFile()));
        }
    }

    private void renderEntities() {
        tree.render(window.getProject() != null ? window.getProject().getEntityDefinitions() : new ArrayList<>());
        repaint();
    }
}
