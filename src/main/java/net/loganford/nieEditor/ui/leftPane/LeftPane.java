package net.loganford.nieEditor.ui.leftPane;

import net.loganford.nieEditor.data.Project;
import net.loganford.nieEditor.ui.Window;
import net.loganford.nieEditor.util.ProjectListener;

import javax.swing.*;
import java.awt.*;

public class LeftPane extends JPanel implements ProjectListener {
    public LeftPane(Window window) {
        window.getListeners().add(this);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(400, 200));
        setPreferredSize(new Dimension(400, 200));

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Rooms", new RoomsTab(window));
        tabbedPane.addTab("Tilesets", new TilesetsTab(window));
        tabbedPane.addTab("Tile Picker", new JPanel());
        tabbedPane.addTab("Entities", new EntitiesTab(window));

        add(tabbedPane, BorderLayout.CENTER);

        setVisible(false);
    }

    @Override
    public void projectChanged(Project project) {
        setVisible(project != null);
    }
}
