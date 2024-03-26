package net.loganford.nieEditor.ui.leftPane;

import com.sun.source.tree.Tree;
import net.loganford.nieEditor.data.EntityDefinition;
import net.loganford.nieEditor.data.Project;
import net.loganford.nieEditor.ui.EditorWindow;
import net.loganford.nieEditor.ui.ProjectListener;
import net.loganford.nieEditor.ui.dialog.EntityDialog;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Array;
import java.util.*;

public class EntitiesTab extends JPanel implements ActionListener, ProjectListener, TreeSelectionListener {

    private EditorWindow editorWindow;
    private DefaultMutableTreeNode root;
    private JTree tree;

    public EntitiesTab(EditorWindow editorWindow) {
        this.editorWindow = editorWindow;
        editorWindow.getListeners().add(this);

        setLayout(new BorderLayout());

        //Setup history list
        ScrollPane scrollPane = new ScrollPane();

        root = new DefaultMutableTreeNode("Entities");

        tree = new JTree(root);
        tree.setCellRenderer(new EntityTreeRenderer());
        tree.setRootVisible(false);
        tree.addTreeSelectionListener(this);
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
            EntityDialog ed = new EntityDialog(true);
            ed.show();
            if(ed.isAccepted()) {
                EntityDefinition def = new EntityDefinition();
                updateEntity(def, ed);
                editorWindow.getProject().getEntityDefinitions().add(def);
                editorWindow.getListeners().forEach(ProjectListener::entitiesChanged);
                editorWindow.setProjectDirty(true);
            }
        }
        if(e.getActionCommand().equals("Edit")) {
            EntityDefinition def = editorWindow.getSelectedEntity();
            if(def != null) {
                EntityDialog ed = new EntityDialog(false);

                ed.setName(def.getName());
                ed.setGroup(def.getGroup());
                ed.setWidth(def.getWidth());
                ed.setHeight(def.getHeight());
                ed.setClassPath(def.getClassPath());

                if(def.getImagePath() != null) {
                    ed.setImageFile(new File(def.getImagePath()));
                }
                ed.show();

                if(ed.isAccepted()) {
                    updateEntity(def, ed);
                    editorWindow.getListeners().forEach(ProjectListener::entitiesChanged);
                    if(editorWindow.getSelectedRoom() != null) {
                        editorWindow.getListeners().forEach(l -> l.selectedRoomChanged(editorWindow.getSelectedRoom()));
                    }
                    editorWindow.setProjectDirty(true);
                }
            }
        }
        if(e.getActionCommand().equals("Remove")) {
            if(editorWindow.getSelectedEntity() != null) {
                editorWindow.getProject().getEntityDefinitions().remove(editorWindow.getSelectedEntity());

                //Todo: Remove entity from all rooms, and clear room histories after asking with dialog

                editorWindow.setSelectedEntity(null);
                editorWindow.getListeners().forEach(ProjectListener::entitiesChanged);
                editorWindow.setProjectDirty(true);
            }
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
            def.setImagePath(ed.getImageFile().getAbsolutePath());
        }
    }

    private void renderEntities() {
        Set<String> expandedGroups = new HashSet<>();
        Iterator<TreeNode> it = root.children().asIterator();
        while(it.hasNext()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) it.next();
            if(node.getAllowsChildren() && tree.isExpanded(new TreePath(node.getPath()))) {
                expandedGroups.add(node.toString());
            }
        }

        root.removeAllChildren();

        HashMap<String, ArrayList<EntityDefinition>> edGroups = new HashMap<>();
        for(EntityDefinition ed: editorWindow.getProject().getEntityDefinitions()) {
            edGroups.computeIfAbsent(ed.getGroup(), v -> new ArrayList<>()).add(ed);
        }

        for(Map.Entry<String, ArrayList<EntityDefinition>> entry : edGroups.entrySet()) {
            String groupName = entry.getKey();
            ArrayList<EntityDefinition> eds = entry.getValue();

            DefaultMutableTreeNode folder;
            if(StringUtils.isBlank(groupName)) {
                folder = root;
            }
            else {
                folder = new DefaultMutableTreeNode(groupName, true);
                root.add(folder);
            }

            for(EntityDefinition ed: eds) {
                DefaultMutableTreeNode entityNode = new DefaultMutableTreeNode(ed, false);
                folder.add(entityNode);
            }
        }

        ((DefaultTreeModel)tree.getModel()).reload(root);

        //Expand the right nodes and make sure the correct node is selected
        for (int i = 0; i < tree.getRowCount(); i++) {
            if(expandedGroups.contains(tree.getPathForRow(i).getLastPathComponent().toString())) {
                tree.expandRow(i);
            }

            DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) tree.getPathForRow(i).getLastPathComponent();
            if(dmtn.getUserObject().equals(editorWindow.getSelectedEntity())) {
                tree.setSelectionPath(tree.getPathForRow(i));
            }
        }
        repaint();
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if(e.getNewLeadSelectionPath() != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getNewLeadSelectionPath().getLastPathComponent();
            tree.setSelectionPath(e.getNewLeadSelectionPath());
            if (node.getUserObject() instanceof EntityDefinition) {
                EntityDefinition ed = (EntityDefinition) node.getUserObject();
                editorWindow.setSelectedEntity(ed);
                return;
            }
        }
        else {
            tree.setSelectionPath(e.getOldLeadSelectionPath());
        }
    }
}
