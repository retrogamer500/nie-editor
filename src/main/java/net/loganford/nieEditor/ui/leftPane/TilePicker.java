package net.loganford.nieEditor.ui.leftPane;

import lombok.Getter;
import net.loganford.nieEditor.data.Project;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.data.Tileset;
import net.loganford.nieEditor.ui.Window;
import net.loganford.nieEditor.util.ImageCache;
import net.loganford.nieEditor.util.ProjectListener;
import net.loganford.nieEditor.util.TilesetCache;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;

public class TilePicker extends JPanel implements ProjectListener, MouseListener, MouseMotionListener {
    private Window window;
    @Getter private Tileset tileset;
    private ImageIcon tileImage;

    private int zoom = 1;
    private boolean showGrid = true;
    private JScrollPane container;
    private int dragMouseX, dragMouseY;
    private boolean middleMouseDown = false;
    private boolean leftMouseDown = false;

    @Getter private int tileSelectionX = 0;
    @Getter private int tileSelectionY = 0;
    @Getter private int tileSelectionX2 = 0;
    @Getter private int tileSelectionY2 = 0;

    public TilePicker(Window window, JScrollPane container) {
        this.container = container;
        this.window = window;
        window.setTilePicker(this);

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

        window.getListeners().add(this);
        //setPreferredSize(new Dimension(500, 500));
        //setSize(new Dimension(32, 32));
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
                g.setXORMode(new Color(48, 48, 48, 48));
                g.setColor(new Color(0, 0, 0, 128));
                for (int i = tileset.getTileWidth() - 1; i < tileImage.getIconWidth(); i += tileset.getTileWidth()) {
                    g.fillRect(i, 0, 1, tileImage.getIconHeight());
                }

                for (int j = tileset.getTileHeight() - 1; j < tileImage.getIconHeight(); j += tileset.getTileHeight()) {
                    g.fillRect(0, j, tileImage.getIconWidth(), 1);
                }
                g.setPaintMode();
            }

            //Draw Selection
            g.setColor(new Color(255, 255 , 255, 96));
            g.fillRect(
                    tileset.getTileWidth() * Math.min(tileSelectionX, tileSelectionX2),
                    tileset.getTileHeight() * Math.min(tileSelectionY, tileSelectionY2),
                    tileset.getTileWidth() * (Math.max(tileSelectionX, tileSelectionX2) - Math.min(tileSelectionX, tileSelectionX2) + 1),
                    tileset.getTileHeight() * (Math.max(tileSelectionY, tileSelectionY2) - Math.min(tileSelectionY, tileSelectionY2) + 1)
            );
        }

    }

    private void updatePicker() {
        if(window.getProject() != null && window.getSelectedRoom() != null && window.getSelectedRoom().getSelectedLayer() != null
                && window.getSelectedRoom().getSelectedLayer().getTileMap() != null
                && window.getSelectedRoom().getSelectedLayer().getTileMap().getTilesetUuid() != null) {
            tileset = TilesetCache.getInstance().getTileset(window.getSelectedRoom().getSelectedLayer().getTileMap().getTilesetUuid());
            if(tileset.getImagePath() != null) {
                ImageIcon newTileImage = ImageCache.getInstance().getImage(new File(tileset.getImagePath()));

                if(tileImage == null || newTileImage.getIconWidth() != tileImage.getIconWidth() || newTileImage.getIconHeight() != tileImage.getIconHeight()) {
                    setPreferredSize(new Dimension(newTileImage.getIconWidth() * zoom, newTileImage.getIconHeight() * zoom));
                    setMinimumSize(new Dimension(newTileImage.getIconWidth() * zoom, newTileImage.getIconHeight() * zoom));
                    setMaximumSize(new Dimension(newTileImage.getIconWidth() * zoom, newTileImage.getIconHeight() * zoom));
                    container.getHorizontalScrollBar().setValue(0);
                    container.getHorizontalScrollBar().setValue(0);
                    container.revalidate();
                }

                tileImage = newTileImage;
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

        if(tileImage != null) {
            setPreferredSize(new Dimension(tileImage.getIconWidth() * zoom, tileImage.getIconHeight() * zoom));
            setMinimumSize(new Dimension(tileImage.getIconWidth() * zoom, tileImage.getIconHeight() * zoom));
            setMaximumSize(new Dimension(tileImage.getIconWidth() * zoom, tileImage.getIconHeight() * zoom));
            container.getHorizontalScrollBar().setValue(0);
            container.getHorizontalScrollBar().setValue(0);
            revalidate();
            container.revalidate();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1 && tileset != null && tileImage != null) {
            if (e.getX() < 0) {
                return;
            }
            if (e.getY() < 0) {
                return;
            }
            if (e.getX() > tileImage.getIconWidth() * zoom) {
                return;
            }
            if (e.getY() > tileImage.getIconHeight() * zoom) {
                return;
            }

            tileSelectionX = e.getX() / (tileset.getTileWidth() * zoom);
            tileSelectionY = e.getY() / (tileset.getTileHeight() * zoom);
            tileSelectionX2 = tileSelectionX;
            tileSelectionY2 = tileSelectionY;
            leftMouseDown = true;
            repaint();
        }

        if(e.getButton() == MouseEvent.BUTTON2) {
            dragMouseX = e.getXOnScreen();
            dragMouseY = e.getYOnScreen();
            middleMouseDown = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1 && tileset != null) {
            leftMouseDown = false;

            if(tileSelectionX2 < tileSelectionX) {
                int t = tileSelectionX2;
                tileSelectionX2 = tileSelectionX;
                tileSelectionX = t;
            }
            if(tileSelectionY2 < tileSelectionY) {
                int t = tileSelectionY2;
                tileSelectionY2 = tileSelectionY;
                tileSelectionY = t;
            }
        }

        if(e.getButton() == MouseEvent.BUTTON2) {
            middleMouseDown = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(leftMouseDown && tileset != null) {
            tileSelectionX2 = e.getX() / (tileset.getTileWidth() * zoom);
            tileSelectionY2 = e.getY() / (tileset.getTileHeight() * zoom);
            tileSelectionX2 = Math.min(Math.max(0, tileSelectionX2), (tileImage.getIconWidth() / tileset.getTileWidth()) - 1);
            tileSelectionY2 = Math.min(Math.max(0, tileSelectionY2), (tileImage.getIconHeight() / tileset.getTileHeight()) - 1);

            repaint();
        }

        if(middleMouseDown) {
            int deltaX = e.getXOnScreen() - dragMouseX;
            int deltaY = e.getYOnScreen() - dragMouseY;

            if(deltaX != 0) {
                container.getHorizontalScrollBar().setValue(container.getHorizontalScrollBar().getValue() - deltaX);
            }
            if(deltaY != 0) {
                container.getVerticalScrollBar().setValue(container.getVerticalScrollBar().getValue() - deltaY);
            }

            dragMouseX = e.getXOnScreen();
            dragMouseY = e.getYOnScreen();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
