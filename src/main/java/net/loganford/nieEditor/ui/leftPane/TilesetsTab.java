package net.loganford.nieEditor.ui.leftPane;

import net.loganford.nieEditor.data.*;
import net.loganford.nieEditor.ui.Window;
import net.loganford.nieEditor.ui.dialog.TilesetDialog;
import net.loganford.nieEditor.util.ImageCache;
import net.loganford.nieEditor.util.ProjectListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.UUID;

public class TilesetsTab extends JPanel implements ActionListener, ProjectListener, ListSelectionListener {
    private JList<Object> jList;

    private Window window;

    public TilesetsTab(Window window) {
        this.window = window;
        window.getListeners().add(this);

        setLayout(new BorderLayout());

        //Setup list of tilesets
        ScrollPane scrollPane = new ScrollPane();
        jList = new JList<>();
        jList.addListSelectionListener(this);
        jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.add(jList);
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
            TilesetDialog td = new TilesetDialog(true);
            td.show();

            if(td.isAccepted()) {
                ImageCache.getInstance().clearCache(td.getImageFile());
                Tileset ts = new Tileset();
                ts.setName(td.getTilesetName());
                ts.setTileWidth(td.getTileWidth());
                ts.setTileHeight(td.getTileHeight());
                ts.setEngineResourceKey(td.getEngineResourceKey());
                ts.setUuid(UUID.randomUUID().toString());

                if(td.getImageFile() != null) {
                    ts.setImagePath(td.getImageFile().getAbsolutePath());
                }

                window.getProject().getTilesets().add(ts);
                window.getListeners().forEach(ProjectListener::tilesetsChanged);
                window.setProjectDirty(true);
            }
        }
        if(e.getActionCommand().equals("Edit")) {
            Tileset ts = window.getSelectedTileset();

            TilesetDialog td = new TilesetDialog(false);
            td.setTilesetName(ts.getName());
            td.setTileWidth(ts.getTileWidth());
            td.setTileHeight(ts.getTileHeight());
            td.setEngineResourceKey(ts.getEngineResourceKey());
            if(ts.getImagePath() != null) {
                td.setImageFile(new File(ts.getImagePath()));
            }

            td.show();

            if(td.isAccepted()) {
                ImageCache.getInstance().clearCache(td.getImageFile());
                ts.setName(td.getTilesetName());
                ts.setTileWidth(td.getTileWidth());
                ts.setTileHeight(td.getTileHeight());
                ts.setEngineResourceKey(td.getEngineResourceKey());

                if(td.getImageFile() != null) {
                    ts.setImagePath(td.getImageFile().getAbsolutePath());
                }

                window.getListeners().forEach(ProjectListener::tilesetsChanged);
                window.setProjectDirty(true);
            }
        }
        if(e.getActionCommand().equals("Remove")) {

            if(window.getSelectedTileset() != null) {
                int dialogResult = JOptionPane.showConfirmDialog (null, "Deleting this tileset cannot be undone. This will delete all usages of this tileset from all rooms and clear their undo histories. Are you sure?", "Warning", JOptionPane.YES_NO_OPTION);

                Tileset ts = window.getSelectedTileset();
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
            }
            window.getListeners().forEach(ProjectListener::tilesetsChanged);
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
        if(window.getProject() == null) {
            jList.setListData(new String[] {});
        }
        else {
            jList.setListData(window.getProject().getTilesets().toArray());

            if(window.getSelectedTileset() != null) {
                jList.setSelectedIndex(window.getProject().getTilesets().indexOf(window.getSelectedTileset()));
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(((JList)e.getSource()).getSelectedIndices().length > 0) {
            int selectedPos = ((JList) e.getSource()).getSelectedIndices()[0];
            Tileset tileset = window.getProject().getTilesets().get(selectedPos);
            window.setSelectedTileset(tileset);
        }
    }
}
