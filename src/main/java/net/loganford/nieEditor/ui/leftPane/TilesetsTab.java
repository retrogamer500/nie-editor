package net.loganford.nieEditor.ui.leftPane;

import net.loganford.nieEditor.data.*;
import net.loganford.nieEditor.ui.Window;
import net.loganford.nieEditor.ui.dialog.TilesetDialog;
import net.loganford.nieEditor.util.FolderTree;
import net.loganford.nieEditor.util.ImageCache;
import net.loganford.nieEditor.util.ProjectListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class TilesetsTab extends JPanel implements ActionListener, ProjectListener {

    private FolderTree<Tileset> tree;

    private Window window;

    public TilesetsTab(Window window) {
        this.window = window;
        window.getListeners().add(this);

        setLayout(new BorderLayout());

        //Setup list of tilesets
        JScrollPane scrollPane = new JScrollPane();

        tree = new FolderTree<>(
                window,
                Tileset.class,
                () -> window.getProject() != null ? window.getProject().getTilesets() : new ArrayList<>(),
                Tileset::getGroup,
                Tileset::setGroup,
                (ts) -> ImageCache.getInstance().getImage(new File("./editor-data/tileset.png")),
                window::setSelectedTileset
        );
        tree.setOnClickAction(this::editTileset);
        tree.setOnCreateAction(this::createTileset);
        tree.setOnDeleteAction(this::deleteTileset);

        scrollPane.getViewport().add(tree);
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
            createTileset("");
        }
        if(e.getActionCommand().equals("Edit")) {
            Tileset ts = window.getSelectedTileset();
            if(ts != null) {
                editTileset(ts);
            }
        }
        if(e.getActionCommand().equals("Remove")) {
            if(window.getSelectedTileset() != null) {
                deleteTileset(window.getSelectedTileset());
            }
        }
    }

    private void createTileset(String group) {
        TilesetDialog td = new TilesetDialog(window, true);
        td.setGroup(group);
        td.show();

        if(td.isAccepted()) {
            ImageCache.getInstance().clearCache(td.getImageFile());
            Tileset ts = new Tileset();
            ts.setName(td.getTilesetName());
            ts.setGroup(td.getGroup());
            ts.setTileWidth(td.getTileWidth());
            ts.setTileHeight(td.getTileHeight());
            ts.setEngineResourceKey(td.getEngineResourceKey());
            ts.setUuid(UUID.randomUUID().toString());

            if(td.getImageFile() != null) {
                ts.setImagePath(window.getRelativeFilePath(td.getImageFile()));
            }

            window.getProject().getTilesets().add(ts);
            window.getListeners().forEach(ProjectListener::tilesetsChanged);
            window.setProjectDirty(true);
        }
    }

    private void deleteTileset(Tileset ts) {
        int dialogResult = JOptionPane.showConfirmDialog (null, "Deleting this tileset cannot be undone. This will delete all usages of this tileset from all rooms and clear their undo histories. Are you sure?", "Warning", JOptionPane.YES_NO_OPTION);

        window.getProject().getTilesets().remove(ts);

        if(dialogResult == JOptionPane.YES_OPTION){
            for(Room room: window.getProject().getRooms()) {
                for(Layer layer: room.getLayerList()) {
                    if(ts.getUuid().equals(layer.getTileMap().getTilesetUuid())) {
                        layer.setTileMap(new TileMap());
                    }
                }
                room.getActionPerformer().clearHistory();
            }
        }
        window.getListeners().forEach(ProjectListener::tilesetsChanged);
    }

    private void editTileset(Tileset ts) {
        TilesetDialog td = new TilesetDialog(window, false);
        td.setTilesetName(ts.getName());
        td.setGroup(ts.getGroup());
        td.setTileWidth(ts.getTileWidth());
        td.setTileHeight(ts.getTileHeight());
        td.setEngineResourceKey(ts.getEngineResourceKey());
        if(ts.getImagePath() != null) {
            td.setImageFile(window.getRelativeFile(ts.getImagePath()));
        }

        td.show();

        if(td.isAccepted()) {
            ImageCache.getInstance().clearCache(td.getImageFile());
            ts.setName(td.getTilesetName());
            ts.setGroup(td.getGroup());
            ts.setTileWidth(td.getTileWidth());
            ts.setTileHeight(td.getTileHeight());
            ts.setEngineResourceKey(td.getEngineResourceKey());

            if(td.getImageFile() != null) {
                ImageCache.getInstance().clearCache(td.getImageFile());
                ts.setImagePath(window.getRelativeFilePath(td.getImageFile()));
            }

            window.getListeners().forEach(ProjectListener::tilesetsChanged);
            window.setProjectDirty(true);
        }
    }

    @Override
    public void tilesetsChanged() {
        renderTilesets();
    }

    @Override
    public void projectChanged(Project project) {
        renderTilesets();
    }

    private void renderTilesets() {
        tree.render(window.getProject().getTilesets());
    }
}
