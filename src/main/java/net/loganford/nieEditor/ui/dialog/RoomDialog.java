package net.loganford.nieEditor.ui.dialog;

import lombok.Getter;
import lombok.Setter;
import net.loganford.nieEditor.util.ColorPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RoomDialog implements ActionListener {
    private boolean newRoom;

    @Getter private boolean accepted = false;
    @Getter @Setter private String roomName = "test_room";
    @Getter @Setter private int roomWidth = 640;
    @Getter @Setter private int roomHeight = 480;
    @Getter @Setter private Color backgroundColor = new Color(128, 128, 128);

    private ColorPanel colorPanel;

    public RoomDialog(boolean newRoom) {
        this.newRoom = newRoom;
    }

    public void show() {
        String title = newRoom ? "Create Room" : "Edit Room";

        JTextField roomNameField = new JTextField(roomName);
        JSpinner roomWidthSpinner = new JSpinner(new SpinnerNumberModel(roomWidth, 1, 1000000, 1));
        JSpinner roomHeightSpinner = new JSpinner(new SpinnerNumberModel(roomHeight, 1, 1000000, 1));
        JButton colorButton = new JButton("Background Color");
        colorButton.addActionListener(this);
        colorPanel = new ColorPanel(backgroundColor);


        JComponent[] inputs = {
                new JLabel("Room Name:"),
                roomNameField,
                new JLabel("Room Width:"),
                roomWidthSpinner,
                new JLabel("Room Height:"),
                roomHeightSpinner,
                colorButton,
                colorPanel
        };

        int result = JOptionPane.showConfirmDialog(null, inputs, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        this.roomName = roomNameField.getText();
        this.roomWidth = (Integer) roomWidthSpinner.getValue();
        this.roomHeight = (Integer) roomHeightSpinner.getValue();
        this.accepted = result == JOptionPane.YES_OPTION;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Background Color")) {
            Color selectedColor = JColorChooser.showDialog(null, "Choose Background Color", backgroundColor);
            if(selectedColor != null) {
                backgroundColor = selectedColor;
                colorPanel.setColor(backgroundColor);
            }
        }
    }
}
