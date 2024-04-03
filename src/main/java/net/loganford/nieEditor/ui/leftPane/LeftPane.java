package net.loganford.nieEditor.ui.leftPane;

import lombok.Getter;
import net.loganford.nieEditor.data.Project;
import net.loganford.nieEditor.ui.Window;
import net.loganford.nieEditor.util.ProjectListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class LeftPane extends JPanel implements ProjectListener, ChangeListener {
    private Window window;
    @Getter private JTabbedPane tabbedPane;


    public LeftPane(Window window) {
        this.window = window;

        window.getListeners().add(this);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(400, 200));
        setPreferredSize(new Dimension(400, 200));

        tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener(this);

        tabbedPane.addTab("Rooms", new RoomsTab(window));
        tabbedPane.addTab("Project Properties", new ProjectProperties(window));
        tabbedPane.addTab("Tilesets", new TilesetsTab(window));
        tabbedPane.addTab("Tile Picker", new TilePickerTab(window));
        tabbedPane.addTab("Entities", new EntitiesTab(window));

        add(tabbedPane, BorderLayout.CENTER);

        setVisible(false);
    }

    @Override
    public void projectChanged(Project project) {
        setVisible(project != null);
    }

    public String getSelectedTab() {
        return tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        window.getListeners().forEach(ProjectListener::leftTabChanged);
    }
}
