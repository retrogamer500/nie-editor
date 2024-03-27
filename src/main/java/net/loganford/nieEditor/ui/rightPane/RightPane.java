package net.loganford.nieEditor.ui.rightPane;

import net.loganford.nieEditor.data.Project;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.EditorWindow;
import net.loganford.nieEditor.util.ProjectListener;

import javax.swing.*;
import java.awt.*;

public class RightPane extends JPanel implements ProjectListener {
    public RightPane(EditorWindow editorWindow) {
        editorWindow.getListeners().add(this);

        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(200, 200));
        setPreferredSize(new Dimension(200, 200));

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Layers", new LayersTab(editorWindow));
        tabbedPane.addTab("History", new HistoryTab(editorWindow));

        add(tabbedPane, BorderLayout.CENTER);

        setVisible(false);
    }

    @Override
    public void projectChanged(Project project) {
        setVisible(project != null);
    }

    @Override
    public void selectedRoomChanged(Room room) {
        setVisible(room != null);
    }
}
