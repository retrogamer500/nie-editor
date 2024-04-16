package net.loganford.nieEditor.ui.leftPane;

import net.loganford.nieEditor.actions.actionImpl.EditRoom;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.data.Tileset;
import net.loganford.nieEditor.ui.Window;
import net.loganford.nieEditor.util.ProjectListener;
import net.loganford.nieEditor.ui.dialog.RoomDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class RoomsTab extends JPanel implements ActionListener, ProjectListener, ListSelectionListener, MouseListener {
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
        roomList.addMouseListener(this);
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

                room.setBackgroundColor(rd.getBackgroundColor());

                window.getProject().getRooms().add(room);
                window.getListeners().forEach(ProjectListener::roomListChanged);
                window.setProjectDirty(true);
            }
        }

        if(e.getActionCommand().equals("Edit")) {
            if(window.getSelectedRoom() != null) {
                editRoom(window.getSelectedRoom());
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

    private void editRoom(Room room) {
        RoomDialog rd = new RoomDialog(false);
        rd.setRoomName(room.getName());
        rd.setRoomWidth(room.getWidth());
        rd.setRoomHeight(room.getHeight());
        rd.setBackgroundColor(room.getBackgroundColor());
        rd.show();

        if(rd.isAccepted()) {
            EditRoom action = new EditRoom(window, room, rd.getRoomName(), rd.getRoomWidth(), rd.getRoomHeight(), rd.getBackgroundColor());
            room.getActionPerformer().perform(window, action);
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

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            if(window.getSelectedRoom() != null) {
                editRoom(window.getSelectedRoom());
            }
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
