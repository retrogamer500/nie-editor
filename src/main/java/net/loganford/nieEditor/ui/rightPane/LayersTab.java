package net.loganford.nieEditor.ui.rightPane;

import net.loganford.nieEditor.actions.actionImpl.AddLayer;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.data.Project;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.EditorWindow;
import net.loganford.nieEditor.ui.ProjectListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LayersTab extends JPanel implements ActionListener, ProjectListener {

    private JList jList;
    private EditorWindow editorWindow;

    public LayersTab(EditorWindow editorWindow) {
        this.editorWindow = editorWindow;
        editorWindow.getListeners().add(this);
        setLayout(new BorderLayout());

        //Setup layer list
        ScrollPane scrollPane = new ScrollPane();
        jList = new JList<>();
        jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.add(jList);
        add(scrollPane, BorderLayout.CENTER);

        //Setup buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 2));

        addButton(buttonPanel, "Add Above");
        addButton(buttonPanel, "Add Below");
        addButton(buttonPanel, "Move Up");
        addButton(buttonPanel, "Move Down");
        addButton(buttonPanel, "Remove");
        addButton(buttonPanel, "Edit");
        addButton(buttonPanel, "Show");
        addButton(buttonPanel, "Hide");

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addButton(JPanel panel, String text) {
        JButton button = new JButton(text);
        button.addActionListener(this);
        panel.add(button);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Add Above")) {
            int insertPosition = 0;
            AddLayer addLayer = new AddLayer(editorWindow, editorWindow.getSelectedRoom(), insertPosition);
            editorWindow.getSelectedRoom().getActionPerformer().perform(editorWindow, addLayer);
        }
        if(e.getActionCommand().equals("Add Below")) {

        }
        if(e.getActionCommand().equals("Remove")) {

        }
    }

    @Override
    public void layersChanged(Room room) {
        jList.setListData(room.getLayerList().toArray(new Layer[0]));
    }

    @Override
    public void roomSelectionChanged(Room room) {
        if(room != null) {
            jList.setListData(room.getLayerList().toArray(new Layer[0]));
        }
    }
}
