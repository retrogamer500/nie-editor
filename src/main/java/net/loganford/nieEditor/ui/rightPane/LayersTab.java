package net.loganford.nieEditor.ui.rightPane;

import net.loganford.nieEditor.actions.actionImpl.*;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.EditorWindow;
import net.loganford.nieEditor.ui.ProjectListener;
import net.loganford.nieEditor.ui.dialog.LayerDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LayersTab extends JPanel implements ActionListener, ProjectListener, ListSelectionListener {

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
        jList.addListSelectionListener(this);
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
            if(editorWindow.getSelectedRoom().getSelectedLayer() != null) {
                insertPosition = editorWindow.getSelectedRoom().getLayerList().indexOf(editorWindow.getSelectedRoom().getSelectedLayer());
            }

            addLayerAtIndex(insertPosition);
        }
        if(e.getActionCommand().equals("Add Below")) {
            int insertPosition = editorWindow.getSelectedRoom().getLayerList().size();
            if(editorWindow.getSelectedRoom().getSelectedLayer() != null) {
                insertPosition = editorWindow.getSelectedRoom().getLayerList().indexOf(editorWindow.getSelectedRoom().getSelectedLayer()) + 1;
            }

            addLayerAtIndex(insertPosition);
        }
        if(e.getActionCommand().equals("Show")) {
            Layer selectedLayer = editorWindow.getSelectedRoom().getSelectedLayer();
            if(selectedLayer != null) {
                selectedLayer.setVisible(true);
                editorWindow.getListeners().forEach(l -> l.layersChanged(editorWindow.getSelectedRoom()));
                editorWindow.getListeners().forEach(l -> l.selectedRoomChanged(editorWindow.getSelectedRoom()));
            }
        }
        if(e.getActionCommand().equals("Hide")) {
            Layer selectedLayer = editorWindow.getSelectedRoom().getSelectedLayer();
            if(selectedLayer != null) {
                selectedLayer.setVisible(false);
                editorWindow.getListeners().forEach(l -> l.layersChanged(editorWindow.getSelectedRoom()));
                editorWindow.getListeners().forEach(l -> l.selectedRoomChanged(editorWindow.getSelectedRoom()));
            }
        }
        if(e.getActionCommand().equals("Remove")) {
            Layer selectedLayer = editorWindow.getSelectedRoom().getSelectedLayer();
            if(selectedLayer != null) {
                RemoveLayer removeLayer = new RemoveLayer(editorWindow, editorWindow.getSelectedRoom(), selectedLayer);
                editorWindow.getSelectedRoom().getActionPerformer().perform(editorWindow, removeLayer);
            }
        }
        if(e.getActionCommand().equals("Edit")) {
            Layer selectedLayer = editorWindow.getSelectedRoom().getSelectedLayer();
            if(selectedLayer != null) {
                LayerDialog ld = new LayerDialog(false);
                ld.setLayerName(selectedLayer.getName());
                ld.show();
                if(ld.isAccepted()) {
                    EditLayer editLayer = new EditLayer(editorWindow, selectedLayer, ld.getLayerName());
                    editorWindow.getSelectedRoom().getActionPerformer().perform(editorWindow, editLayer);
                }
            }
        }
        if(e.getActionCommand().equals("Move Up")) {
            Layer selectedLayer = editorWindow.getSelectedRoom().getSelectedLayer();
            if(selectedLayer != null) {
                int layerPosition = editorWindow.getSelectedRoom().getLayerList().indexOf(selectedLayer);
                if(layerPosition > 0) {
                    MoveLayerUp moveLayerUp = new MoveLayerUp(editorWindow, editorWindow.getSelectedRoom(), selectedLayer);
                    editorWindow.getSelectedRoom().getActionPerformer().perform(editorWindow, moveLayerUp);
                }
            }
        }
        if(e.getActionCommand().equals("Move Down")) {
            Layer selectedLayer = editorWindow.getSelectedRoom().getSelectedLayer();
            if(selectedLayer != null) {
                int layerPosition = editorWindow.getSelectedRoom().getLayerList().indexOf(selectedLayer);
                if(layerPosition < editorWindow.getSelectedRoom().getLayerList().size() - 1) {
                    MoveLayerDown moveLayerDown = new MoveLayerDown(editorWindow, editorWindow.getSelectedRoom(), selectedLayer);
                    editorWindow.getSelectedRoom().getActionPerformer().perform(editorWindow, moveLayerDown);
                }
            }
        }
    }

    private void addLayerAtIndex(int index) {
        LayerDialog ld = new LayerDialog(true);
        ld.show();
        if(ld.isAccepted()) {
            AddLayer addLayer = new AddLayer(editorWindow, editorWindow.getSelectedRoom(), ld.getLayerName(), index);
            editorWindow.getSelectedRoom().getActionPerformer().perform(editorWindow, addLayer);
        }
    }

    @Override
    public void layersChanged(Room room) {
        updateLayers(room);
    }

    @Override
    public void selectedRoomChanged(Room room) {
        updateLayers(room);
    }

    private void updateLayers(Room room) {
        if(room != null) {
            jList.setListData(room.getLayerList().toArray(new Layer[0]));

            if(room.getSelectedLayer() != null) {
                jList.setSelectedIndex(room.getLayerList().indexOf(room.getSelectedLayer()));
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(((JList)e.getSource()).getSelectedIndices().length > 0) {
            int selectedPos = ((JList) e.getSource()).getSelectedIndices()[0];
            Room room = editorWindow.getSelectedRoom();
            Layer layer = room.getLayerList().get(selectedPos);
            room.setSelectedLayer(layer);
        }
    }
}
