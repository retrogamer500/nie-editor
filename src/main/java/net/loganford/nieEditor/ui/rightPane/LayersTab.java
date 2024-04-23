package net.loganford.nieEditor.ui.rightPane;

import net.loganford.nieEditor.actions.actionImpl.*;
import net.loganford.nieEditor.data.Layer;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.data.Tileset;
import net.loganford.nieEditor.ui.Window;
import net.loganford.nieEditor.util.ProjectListener;
import net.loganford.nieEditor.ui.dialog.LayerDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class LayersTab extends JPanel implements ActionListener, ProjectListener, ListSelectionListener, MouseListener {

    private JList jList;
    private Window window;

    public LayersTab(Window window) {
        this.window = window;
        window.getListeners().add(this);
        setLayout(new BorderLayout());

        //Setup layer list
        JScrollPane scrollPane = new JScrollPane();
        jList = new JList<>();
        jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jList.addListSelectionListener(this);
        jList.addMouseListener(this);
        scrollPane.getViewport().add(jList);
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
            if(window.getSelectedRoom().getSelectedLayer() != null) {
                insertPosition = window.getSelectedRoom().getLayerList().indexOf(window.getSelectedRoom().getSelectedLayer());
            }

            addLayerAtIndex(insertPosition);
        }
        if(e.getActionCommand().equals("Add Below")) {
            int insertPosition = window.getSelectedRoom().getLayerList().size();
            if(window.getSelectedRoom().getSelectedLayer() != null) {
                insertPosition = window.getSelectedRoom().getLayerList().indexOf(window.getSelectedRoom().getSelectedLayer()) + 1;
            }

            addLayerAtIndex(insertPosition);
        }
        if(e.getActionCommand().equals("Show")) {
            Layer selectedLayer = window.getSelectedRoom().getSelectedLayer();
            if(selectedLayer != null) {
                selectedLayer.setVisible(true);
                window.getListeners().forEach(l -> l.layersChanged(window.getSelectedRoom()));
                window.getListeners().forEach(l -> l.selectedRoomChanged(window.getSelectedRoom()));
            }
        }
        if(e.getActionCommand().equals("Hide")) {
            Layer selectedLayer = window.getSelectedRoom().getSelectedLayer();
            if(selectedLayer != null) {
                selectedLayer.setVisible(false);
                window.getListeners().forEach(l -> l.layersChanged(window.getSelectedRoom()));
                window.getListeners().forEach(l -> l.selectedRoomChanged(window.getSelectedRoom()));
            }
        }
        if(e.getActionCommand().equals("Remove")) {
            Layer selectedLayer = window.getSelectedRoom().getSelectedLayer();
            if(selectedLayer != null) {
                RemoveLayer removeLayer = new RemoveLayer(window, window.getSelectedRoom(), selectedLayer);
                window.getSelectedRoom().getActionPerformer().perform(window, removeLayer);
            }
        }
        if(e.getActionCommand().equals("Edit")) {
            Layer selectedLayer = window.getSelectedRoom().getSelectedLayer();
            if(selectedLayer != null) {
                editLayer(selectedLayer);
            }
        }
        if(e.getActionCommand().equals("Move Up")) {
            Layer selectedLayer = window.getSelectedRoom().getSelectedLayer();
            if(selectedLayer != null) {
                int layerPosition = window.getSelectedRoom().getLayerList().indexOf(selectedLayer);
                if(layerPosition > 0) {
                    MoveLayerUp moveLayerUp = new MoveLayerUp(window, window.getSelectedRoom(), selectedLayer);
                    window.getSelectedRoom().getActionPerformer().perform(window, moveLayerUp);
                }
            }
        }
        if(e.getActionCommand().equals("Move Down")) {
            Layer selectedLayer = window.getSelectedRoom().getSelectedLayer();
            if(selectedLayer != null) {
                int layerPosition = window.getSelectedRoom().getLayerList().indexOf(selectedLayer);
                if(layerPosition < window.getSelectedRoom().getLayerList().size() - 1) {
                    MoveLayerDown moveLayerDown = new MoveLayerDown(window, window.getSelectedRoom(), selectedLayer);
                    window.getSelectedRoom().getActionPerformer().perform(window, moveLayerDown);
                }
            }
        }
    }

    private void editLayer(Layer layer) {
        LayerDialog ld = new LayerDialog(window, false);
        ld.setLayerName(layer.getName());
        if(layer.getTileMap() != null) {
            ld.setTilesetUuid(layer.getTileMap().getTilesetUuid());
        }
        ld.show();
        if(ld.isAccepted()) {
            EditLayer editLayer = new EditLayer(window, layer, ld.getLayerName(), ld.getTilesetUuid());
            window.getSelectedRoom().getActionPerformer().perform(window, editLayer);
        }
    }

    private void addLayerAtIndex(int index) {
        LayerDialog ld = new LayerDialog(window, true);
        ld.show();
        if(ld.isAccepted()) {
            AddLayer addLayer = new AddLayer(window, window.getSelectedRoom(), ld.getLayerName(), ld.getTilesetUuid(), index);
            window.getSelectedRoom().getActionPerformer().perform(window, addLayer);
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
            Room room = window.getSelectedRoom();
            Layer layer = room.getLayerList().get(selectedPos);
            room.setSelectedLayer(layer);
            window.getListeners().forEach(ProjectListener::layerSelectionChanged);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            Layer layer = window.getSelectedRoom().getSelectedLayer();
            editLayer(layer);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
