package net.loganford.nieEditor.ui;

import lombok.Getter;
import lombok.Setter;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.tools.Pen;
import net.loganford.nieEditor.tools.Rectangle;
import net.loganford.nieEditor.tools.Tool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class RoomEditor extends JPanel implements ProjectListener, MouseListener, MouseMotionListener {

    private Tool tool;
    private EditorWindow editorWindow;
    private int width, height;

    @Getter @Setter private int gridWidth = 16;
    @Getter @Setter private int gridHeight = 16;
    @Getter @Setter private boolean showGrid = true;

    public RoomEditor(EditorWindow editorWindow, int width, int height) {
        this.editorWindow = editorWindow;
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        editorWindow.getListeners().add(this);
        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

        resizeRoom(width, height);
    }

    public void resizeRoom(int width, int height) {
        this.width = width;
        this.height = height;

        setPreferredSize(new Dimension(width * editorWindow.getZoom(), height * editorWindow.getZoom()));
        setMinimumSize(new Dimension(width * editorWindow.getZoom(), height * editorWindow.getZoom()));
        setMaximumSize(new Dimension(width * editorWindow.getZoom(), height * editorWindow.getZoom()));
        revalidate();
    }

    @Override
    public void selectedRoomChanged(Room room) {
        if(room != null) {
            resizeRoom(room.getWidth(), room.getHeight());
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = ((Graphics2D) g);
        g2d.scale(editorWindow.getZoom(), editorWindow.getZoom());

        if(editorWindow.getSelectedRoom() != null) {
            g.setColor(Color.lightGray);
            g.fillRect(0, 0, width, height);

            if (showGrid) {
                g.setColor(Color.black);
                for (int i = gridWidth - 1; i < width; i += gridWidth) {
                    g.drawLine(i, 0, i, height);
                }

                for (int j = gridHeight - 1; j < height; j += gridHeight) {
                    g.drawLine(0, j, width, j);
                }
            }
            if(tool != null) {
                tool.render(g);
            }
        }
    }

    private void startTool(int x, int y, boolean isLeftClick) {
        if(editorWindow.getSelectedRoom() == null || editorWindow.getSelectedRoom().getSelectedLayer() == null) {
            return;
        }
        if(editorWindow.getSelectedEntity() == null) {
            return;
        }

        if(editorWindow.getToolPane().getPenTool().isSelected()) {
            tool = new Pen(editorWindow, editorWindow.getSelectedRoom(), editorWindow.getSelectedRoom().getSelectedLayer(), editorWindow.getSelectedEntity(), true, isLeftClick);
        }
        else {
            tool = new Rectangle(editorWindow, editorWindow.getSelectedRoom(), editorWindow.getSelectedRoom().getSelectedLayer(), editorWindow.getSelectedEntity(), true, isLeftClick);
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
        int x = e.getX()/editorWindow.getZoom();
        int y = e.getY()/editorWindow.getZoom();

        startTool(x, y, true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(tool != null) {
            int x = e.getX() / editorWindow.getZoom();
            int y = e.getY() / editorWindow.getZoom();

            endTool(x, y);
            tool = null;
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
            int x = e.getX() / editorWindow.getZoom();
            int y = e.getY() / editorWindow.getZoom();

            moveTool(x, y);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
