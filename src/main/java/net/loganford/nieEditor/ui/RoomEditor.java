package net.loganford.nieEditor.ui;

import lombok.Getter;
import lombok.Setter;
import net.loganford.nieEditor.data.Room;

import javax.swing.*;
import java.awt.*;

public class RoomEditor extends JPanel implements ProjectListener {

    private EditorWindow editorWindow;
    private int width, height;

    @Getter @Setter private int gridWidth = 16;
    @Getter @Setter private int gridHeight = 16;
    @Getter @Setter private boolean showGrid = true;

    public RoomEditor(EditorWindow editorWindow, int width, int height) {
        this.editorWindow = editorWindow;
        editorWindow.getListeners().add(this);

        resizeRoom(width, height);
    }

    public void resizeRoom(int width, int height) {
        this.width = width;
        this.height = height;

        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
    }

    @Override
    public void roomSelectionChanged(Room room) {
        if(room != null) {
            resizeRoom(room.getWidth(), room.getHeight());
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

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
        }
    }
}
