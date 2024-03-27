package net.loganford.nieEditor.ui.leftPane;

import net.loganford.nieEditor.actions.actionImpl.EditRoom;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.Window;
import net.loganford.nieEditor.util.ProjectListener;
import net.loganford.nieEditor.ui.dialog.RoomDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RoomsTab extends JPanel implements ActionListener, ProjectListener, ListSelectionListener {
    private Window window;
    private JList<Room> roomList;

    public RoomsTab(Window window) {
        this.window = window;

        window.getListeners().add(this);

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
        roomList.setListData(window.getProject().getRooms().toArray(new Room[]{}));

        if(window.getSelectedRoom() != null) {
            roomList.setSelectedIndex(window.getProject().getRooms().indexOf(window.getSelectedRoom()));
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

                window.getProject().getRooms().add(room);
                window.getListeners().forEach(ProjectListener::roomListChanged);
                window.setProjectDirty(true);
            }
        }

        if(e.getActionCommand().equals("Edit")) {
            if(window.getSelectedRoom() != null) {
                RoomDialog rd = new RoomDialog(false);
                rd.setRoomName(window.getSelectedRoom().getName());
                rd.setRoomWidth(window.getSelectedRoom().getWidth());
                rd.setRoomHeight(window.getSelectedRoom().getHeight());
                rd.show();

                if(rd.isAccepted()) {
                    EditRoom action = new EditRoom(window, window.getSelectedRoom(), rd.getRoomName(), rd.getRoomWidth(), rd.getRoomHeight());
                    window.getSelectedRoom().getActionPerformer().perform(window, action);
                }
            }
        }

        if(e.getActionCommand().equals("Remove") &&  window.getSelectedRoom() != null) {
            int dialogResult = JOptionPane.showConfirmDialog(null, "Do you want to delete this room? This cannot be undone.", "Warning", JOptionPane.YES_NO_OPTION);
            if (dialogResult == JOptionPane.YES_OPTION) {
                window.getProject().getRooms().remove(window.getSelectedRoom());
                window.setSelectedRoom(null);
                window.getListeners().forEach(ProjectListener::roomListChanged);
                window.setProjectDirty(true);
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(((JList)e.getSource()).getSelectedIndices().length > 0) {
            int selectedPos = ((JList) e.getSource()).getSelectedIndices()[0];
            Room room = window.getProject().getRooms().get(selectedPos);
            window.setSelectedRoom(room);
        }
    }
}
