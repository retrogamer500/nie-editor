package net.loganford.nieEditor.ui.leftPane;

import lombok.Getter;
import net.loganford.nieEditor.data.Project;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.data.Tileset;
import net.loganford.nieEditor.ui.Window;
import net.loganford.nieEditor.util.ImageCache;
import net.loganford.nieEditor.util.ProjectListener;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class TilePicker extends JPanel implements ProjectListener {
    private Window window;
    private Tileset tileset;
    private ImageIcon tileImage;

    private int zoom = 1;
    private boolean showGrid = true;

    @Getter private int tileSelectionX = 0;
    @Getter private int tileSelectionY = 0;

    public TilePicker(Window window) {
        this.window = window;
        window.getListeners().add(this);

        setPreferredSize(new Dimension(500, 500));

        setSize(new Dimension(500, 500));
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = ((Graphics2D) g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        if(tileImage == null) {
            g.setColor(Color.WHITE);
            g.drawString("Select a layer with a tileset to view picker.", 16, 16);
        }
        else {
            g2d.scale(zoom, zoom);

            //Draw tileset
            g.drawImage(tileImage.getImage(), 0, 0, null);

            //Draw Grid
            if(showGrid) {
                g.setColor(new Color(0, 0, 0, 128));
                for (int i = tileset.getTileWidth() - 1; i < tileImage.getIconWidth(); i += tileset.getTileWidth()) {
                    g.fillRect(i, 0, 1, tileImage.getIconHeight());
                }

                for (int j = tileset.getTileHeight() - 1; j < tileImage.getIconHeight(); j += tileset.getTileHeight()) {
                    g.fillRect(0, j, tileImage.getIconWidth(), 1);
                }
            }

            //Draw Selection
            g.setColor(new Color(255, 255 , 255, 96));
            g.fillRect(
                    tileset.getTileWidth() * tileSelectionX,
                    tileset.getTileHeight() * tileSelectionY,
                    tileset.getTileWidth() * (tileSelectionX + 1) - 1,
                    tileset.getTileHeight() * (tileSelectionY + 1) - 1
            );
        }

    }

    private void updatePicker() {
        if(window.getProject() != null && window.getSelectedRoom() != null && window.getSelectedRoom().getSelectedLayer() != null
        && window.getSelectedRoom().getSelectedLayer().getTilesetUuid() != null) {
            tileset = window.getProject().getTileset(window.getSelectedRoom().getSelectedLayer().getTilesetUuid());
            if(tileset.getImagePath() != null) {
                //Todo: Fix scrollbars when zoomed or image changed
                tileImage = ImageCache.getInstance().getImage(new File(tileset.getImagePath()));
                setPreferredSize(new Dimension(tileImage.getIconWidth(), tileImage.getIconHeight()));

            }
            else {
                tileImage = null;
            }
        }
        else {
            tileImage = null;
        }

        repaint();
    }

    @Override
    public void projectChanged(Project project) {
        updatePicker();
    }

    @Override
    public void tilesetsChanged() {
        updatePicker();
    }

    @Override
    public void selectedRoomChanged(Room room) {
        updatePicker();
    }

    @Override
    public void layerSelectionChanged() {
        updatePicker();
    }

    @Override
    public void tilePickerSettingsChanged(int zoom, boolean showGrid) {
        this.zoom = zoom;
        this.showGrid = showGrid;
        updatePicker();
    }
}
