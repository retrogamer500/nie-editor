package net.loganford.nieEditor.ui.leftPane;

import net.loganford.nieEditor.data.Project;
import net.loganford.nieEditor.ui.EditorWindow;
import net.loganford.nieEditor.ui.ProjectListener;

import javax.swing.*;
import java.awt.*;

public class LeftPane extends JPanel implements ProjectListener {
    public LeftPane(EditorWindow editorWindow) {
        editorWindow.getListeners().add(this);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(400, 200));
        setPreferredSize(new Dimension(400, 200));

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Rooms", new RoomsTab(editorWindow));
        tabbedPane.addTab("Tile Sets", new JPanel());
        tabbedPane.addTab("Tile Picker", new JPanel());
        tabbedPane.addTab("Entities", new EntitiesTab(editorWindow));

        add(tabbedPane, BorderLayout.CENTER);

        setVisible(false);
    }

    @Override
    public void projectChanged(Project project) {
        setVisible(project != null);
    }
}
