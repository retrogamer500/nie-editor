package net.loganford.nieEditor.ui.leftPane;

import net.loganford.nieEditor.actions.actionImpl.EditRoom;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.data.Tileset;
import net.loganford.nieEditor.ui.Window;
import net.loganford.nieEditor.util.FolderTree;
import net.loganford.nieEditor.util.ImageCache;
import net.loganford.nieEditor.util.ProjectListener;
import net.loganford.nieEditor.ui.dialog.RoomDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class RoomsTab extends JPanel implements ActionListener, ProjectListener {
    private Window window;
    private FolderTree<Room> roomTree;

    public RoomsTab(Window window) {
        this.window = window;

        window.getListeners().add(this);

        setLayout(new BorderLayout());

        //Setup history list
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setWheelScrollingEnabled(true);

        roomTree = new FolderTree<>(
                window,
                Room.class,
                () -> window.getProject() != null ? window.getProject().getRooms() : new ArrayList<>(),
                Room::getGroup,
                Room::setGroup,
                (e) -> ImageCache.getInstance().getImage(new File("./editor-data/rm.png"), 14, 14),
                window::setSelectedRoom
        );
        roomTree.setOnClickAction(this::editRoom);
        roomTree.setOnCreateAction(this::createRoom);
        roomTree.setOnDeleteAction(this::deleteRoom);

        scrollPane.getViewport().add(roomTree);

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
        roomTree.render(window.getProject() != null ? window.getProject().getRooms() : new ArrayList<>());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Add")) {
            createRoom("");
        }

        if(e.getActionCommand().equals("Edit")) {
            if(window.getSelectedRoom() != null) {
                editRoom(window.getSelectedRoom());
            }
        }

        if(e.getActionCommand().equals("Remove") &&  window.getSelectedRoom() != null) {
            int dialogResult = JOptionPane.showConfirmDialog(null, "Do you want to delete this room? This cannot be undone.", "Warning", JOptionPane.YES_NO_OPTION);
            if (dialogResult == JOptionPane.YES_OPTION) {
                deleteRoom(window.getSelectedRoom());
            }
        }
    }

    private void createRoom(String group) {
        RoomDialog rd = new RoomDialog(true);
        rd.setGroup(group);
        rd.show();

        if(rd.isAccepted()) {
            Room room = new Room();
            room.setName(rd.getRoomName());
            room.setGroup(rd.getGroup());
            room.setWidth(rd.getRoomWidth());
            room.setHeight(rd.getRoomHeight());

            room.setBackgroundColor(rd.getBackgroundColor());

            window.getProject().getRooms().add(room);
            window.getListeners().forEach(ProjectListener::roomListChanged);
            window.setProjectDirty(true);
        }
    }

    private void deleteRoom(Room room) {
        window.getProject().getRooms().remove(room);
        if(window.getSelectedRoom() == room) {
            window.setSelectedRoom(null);
        }
        window.getListeners().forEach(ProjectListener::roomListChanged);
        window.setProjectDirty(true);
    }

    private void editRoom(Room room) {
        RoomDialog rd = new RoomDialog(false);
        rd.setRoomName(room.getName());
        rd.setGroup(room.getGroup());
        rd.setRoomWidth(room.getWidth());
        rd.setRoomHeight(room.getHeight());
        rd.setBackgroundColor(room.getBackgroundColor());
        rd.show();

        if(rd.isAccepted()) {
            room.setGroup(rd.getGroup());

            EditRoom action = new EditRoom(window, room, rd.getRoomName(), rd.getRoomWidth(), rd.getRoomHeight(), rd.getBackgroundColor());
            room.getActionPerformer().perform(window, action);
        }
    }
}
