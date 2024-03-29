package net.loganford.nieEditor.ui;

import lombok.Getter;
import lombok.Setter;
import net.loganford.nieEditor.data.Entity;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.tools.Pen;
import net.loganford.nieEditor.tools.Rectangle;
import net.loganford.nieEditor.tools.Tool;
import net.loganford.nieEditor.util.ProjectListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class RoomEditor extends JPanel implements ProjectListener, MouseListener, MouseMotionListener {

    private Tool tool;
    private Window window;
    private int width, height;
    private boolean middleMouseDown = false;
    private int dragMouseX, dragMouseY;

    @Getter @Setter private int gridWidth = 16;
    @Getter @Setter private int gridHeight = 16;
    @Getter @Setter private boolean showGrid = true;

    public RoomEditor(Window window, int width, int height) {
        this.window = window;
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        window.getListeners().add(this);
        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

        resizeRoom(width, height);
    }

    public void resizeRoom(int width, int height) {
        this.width = width;
        this.height = height;

        if(getPreferredSize().getWidth() != width * window.getZoom() || getPreferredSize().getHeight() != height * window.getZoom()) {
            window.getRoomScrollPane().getVerticalScrollBar().setValue(0);
            window.getRoomScrollPane().getHorizontalScrollBar().setValue(0);
            setPreferredSize(new Dimension(width * window.getZoom(), height * window.getZoom()));
            setMinimumSize(new Dimension(width * window.getZoom(), height * window.getZoom()));
            setMaximumSize(new Dimension(width * window.getZoom(), height * window.getZoom()));
            revalidate();
        }
    }

    @Override
    public void selectedRoomChanged(Room room) {
        if(room != null) {
            resizeRoom(room.getWidth(), room.getHeight());
        }
        repaint();
    }

    @Override
    public void layersChanged(Room room) {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = ((Graphics2D) g);
        g2d.scale(window.getZoom(), window.getZoom());

        if(window.getSelectedRoom() != null) {
            //Room background
            g.setColor(Color.lightGray);
            g.fillRect(0, 0, width, height);


            //Render layers
            for(int i = window.getSelectedRoom().getLayerList().size() - 1; i >= 0; i--) {
                Layer layer = window.getSelectedRoom().getLayerList().get(i);
                if(layer.isVisible()) {
                    //Render layer
                    if(layer.getTileMap() != null && layer.getTileMap().getTilesetUuid() != null) {
                        layer.getTileMap().renderTileMap(g);
                    }

                    if(layer == window.getSelectedRoom().getSelectedLayer() && tool != null) {
                        tool.renderBelowEntities(g);
                    }

                    //Render entities
                    for(Entity entity : layer.getEntities()) {
                        if(!entity.isHidden()) {
                            entity.render(window, g);
                        }
                    }

                    if(layer == window.getSelectedRoom().getSelectedLayer() && tool != null) {
                        tool.renderAboveEntities(g);
                    }
                }
            }

            //Render grid
            if (showGrid) {
                g.setColor(new Color(0, 0, 0, 128));
                for (int i = gridWidth - 1; i < width; i += gridWidth) {
                    g.fillRect(i, 0, 1, height);
                }

                for (int j = gridHeight - 1; j < height; j += gridHeight) {
                    g.fillRect(0, j, width, 1);
                }
            }

            //Render tool
            if(tool != null) {
                tool.render(g);
            }
        }
    }

    private void startTool(int x, int y, boolean isLeftClick) {
        if(window.getSelectedRoom() == null || window.getSelectedRoom().getSelectedLayer() == null) {
            return;
        }

        if(!window.getLeftPane().getSelectedTab().equals("Entities") &&
                !window.getLeftPane().getSelectedTab().equals("Tile Picker")) {
            return;
        }

        boolean isEntity = window.getLeftPane().getSelectedTab().equals("Entities");

        if(isLeftClick) {
            if (isEntity && window.getSelectedEntity() == null) {
                return;
            }
            if (!isEntity && (window.getSelectedRoom().getSelectedLayer().getTileMap() == null || window.getSelectedRoom().getSelectedLayer().getTileMap().getTilesetUuid() == null)) {
                return;
            }
        }

        if(window.getToolPane().getPenTool().isSelected()) {
            tool = new Pen(window, window.getSelectedRoom(), window.getSelectedRoom().getSelectedLayer(), window.getSelectedEntity(), isEntity, isLeftClick);
        }
        else {
            tool = new Rectangle(window, window.getSelectedRoom(), window.getSelectedRoom().getSelectedLayer(), window.getSelectedEntity(), isEntity, isLeftClick);
        }

        tool.mousePressed(x, y);
        repaint();
    }

    private void moveTool(int x, int y) {
        tool.mouseMoved(x, y);
        repaint();
    }

    private void endTool(int x, int y) {
        tool.mouseReleased(x, y);
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX()/ window.getZoom();
        int y = e.getY()/ window.getZoom();

        if(tool != null) {
            tool.cancelTool(x, y);
            tool = null;
            repaint();
        }
        else {
            if (e.getButton() == MouseEvent.BUTTON1) {
                startTool(x, y, true);
            }
            if (e.getButton() == MouseEvent.BUTTON3) {
                startTool(x, y, false);
            }
        }

        if(e.getButton() == MouseEvent.BUTTON2) {
            dragMouseX = e.getXOnScreen();
            dragMouseY = e.getYOnScreen();
            middleMouseDown = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(tool != null) {
            int x = e.getX() / window.getZoom();
            int y = e.getY() / window.getZoom();

            endTool(x, y);
            tool = null;
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

        if(tool != null) {
            int x = e.getX() / window.getZoom();
            int y = e.getY() / window.getZoom();

            moveTool(x, y);
        }

        if(middleMouseDown) {
            int deltaX = e.getXOnScreen() - dragMouseX;
            int deltaY = e.getYOnScreen() - dragMouseY;

            if(deltaX != 0) {
                window.getRoomScrollPane().getHorizontalScrollBar().setValue(window.getRoomScrollPane().getHorizontalScrollBar().getValue() - deltaX);
            }
            if(deltaY != 0) {
                window.getRoomScrollPane().getVerticalScrollBar().setValue(window.getRoomScrollPane().getVerticalScrollBar().getValue() - deltaY);
            }

            dragMouseX = e.getXOnScreen();
            dragMouseY = e.getYOnScreen();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
