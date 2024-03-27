package net.loganford.nieEditor.ui.leftPane;

import net.loganford.nieEditor.actions.actionImpl.EditRoom;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.EditorWindow;
import net.loganford.nieEditor.util.ProjectListener;
import net.loganford.nieEditor.ui.dialog.RoomDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RoomsTab extends JPanel implements ActionListener, ProjectListener, ListSelectionListener {
    private EditorWindow editorWindow;
    private JList<Room> roomList;

    public RoomsTab(EditorWindow editorWindow) {
        this.editorWindow = editorWindow;

        editorWindow.getListeners().add(this);

        setLayout(new BorderLayout());

        //Setup history list
        ScrollPane scrollPane = new ScrollPane();
        roomList = new JList<>();
        roomList.addListSelectionListener(this);
        roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.add(roomList);
        add(scrollPane, BorderLayout.CENTER);

        //Setup buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3));

        JButton addButton = new JButton("Add");
        addButton.addActionListener(this);
        buttonPanel.add(addButton);

        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(this);
        buttonPanel.add(removeButton);

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(this);
        buttonPanel.add(editButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void roomListChanged() {
        roomList.setListData(editorWindow.getProject().getRooms().toArray(new Room[]{}));

        if(editorWindow.getSelectedRoom() != null) {
            roomList.setSelectedIndex(editorWindow.getProject().getRooms().indexOf(editorWindow.getSelectedRoom()));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Add")) {
            RoomDialog rd = new RoomDialog(true);
            rd.show();

            if(rd.isAccepted()) {
                Room room = new Room();
                room.setName(rd.getRoomName());
                room.setWidth(rd.getRoomWidth());
                room.setHeight(rd.getRoomHeight());

                editorWindow.getProject().getRooms().add(room);
                editorWindow.getListeners().forEach(ProjectListener::roomListChanged);
                editorWindow.setProjectDirty(true);
            }
        }

        if(e.getActionCommand().equals("Edit")) {
            if(editorWindow.getSelectedRoom() != null) {
                RoomDialog rd = new RoomDialog(false);
                rd.setRoomName(editorWindow.getSelectedRoom().getName());
                rd.setRoomWidth(editorWindow.getSelectedRoom().getWidth());
                rd.setRoomHeight(editorWindow.getSelectedRoom().getHeight());
                rd.show();

                if(rd.isAccepted()) {
                    EditRoom action = new EditRoom(editorWindow, editorWindow.getSelectedRoom(), rd.getRoomName(), rd.getRoomWidth(), rd.getRoomHeight());
                    editorWindow.getSelectedRoom().getActionPerformer().perform(editorWindow, action);
                }
            }
        }

        if(e.getActionCommand().equals("Remove") &&  editorWindow.getSelectedRoom() != null) {
            int dialogResult = JOptionPane.showConfirmDialog(null, "Do you want to delete this room? This cannot be undone.", "Warning", JOptionPane.YES_NO_OPTION);
            if (dialogResult == JOptionPane.YES_OPTION) {
                editorWindow.getProject().getRooms().remove(editorWindow.getSelectedRoom());
                editorWindow.setSelectedRoom(null);
                editorWindow.getListeners().forEach(ProjectListener::roomListChanged);
                editorWindow.setProjectDirty(true);
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(((JList)e.getSource()).getSelectedIndices().length > 0) {
            int selectedPos = ((JList) e.getSource()).getSelectedIndices()[0];
            Room room = editorWindow.getProject().getRooms().get(selectedPos);
            editorWindow.setSelectedRoom(room);
        }
    }
}
