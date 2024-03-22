package net.loganford.nieEditor.ui.dialog;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;

public class RoomDialog {
    private boolean newRoom;

    @Getter private boolean accepted = false;
    @Getter @Setter private String roomName = "test_room";
    @Getter @Setter private int roomWidth = 640;
    @Getter @Setter private int roomHeight = 480;

    public RoomDialog(boolean newRoom) {
        this.newRoom = newRoom;
    }

    public void show() {
        String title = newRoom ? "Create Room" : "Edit Room";

        JTextField roomNameField = new JTextField(roomName);
        JSpinner roomWidthSpinner = new JSpinner(new SpinnerNumberModel(roomWidth, 1, 1000000, 1));
        JSpinner roomHeightSpinner = new JSpinner(new SpinnerNumberModel(roomHeight, 1, 1000000, 1));

        JComponent[] inputs = {
                new JLabel("Room Name:"),
                roomNameField,
                new JLabel("Room Width:"),
                roomWidthSpinner,
                new JLabel("Room Height:"),
                roomHeightSpinner
        };

        int result = JOptionPane.showConfirmDialog(null, inputs, title, JOptionPane.OK_CANCEL_OPTION);

        this.roomName = roomNameField.getText();
        this.roomWidth = (Integer) roomWidthSpinner.getValue();
        this.roomHeight = (Integer) roomHeightSpinner.getValue();
        this.accepted = result == JOptionPane.YES_OPTION;
    }
}
