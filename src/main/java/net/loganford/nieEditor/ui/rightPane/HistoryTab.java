package net.loganford.nieEditor.ui.rightPane;

import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.Window;
import net.loganford.nieEditor.util.ProjectListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

public class HistoryTab extends JPanel implements ProjectListener, ActionListener {
    private JList jList;
    Window window;

    public HistoryTab(Window window) {
        this.window = window;
        window.getListeners().add(this);
        setLayout(new BorderLayout());

        //Setup history list
        JScrollPane scrollPane = new JScrollPane();
        jList = new JList<>();
        jList.setEnabled(false);
        scrollPane.getViewport().add(jList);
        add(scrollPane, BorderLayout.CENTER);

        //Setup buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(this);
        buttonPanel.add(undoButton);
        JButton redoButton = new JButton("Redo");
        redoButton.addActionListener(this);
        buttonPanel.add(redoButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void selectedRoomChanged(Room room) {
        resetHistory(room);
    }

    @Override
    public void historyChanged(Room room) {
        resetHistory(room);
    }

    private void resetHistory(Room room) {
        if(room != null) {
            ArrayList<String> actionList = new ArrayList<>(room.getActionPerformer().getActionList().stream()
                    .map(Object::toString)
                    .toList());
            int actionIndex = room.getActionPerformer().getLastActionIndex();
            if(actionIndex >= 0) {
                actionList.set(actionIndex, "<- " + actionList.get(actionIndex) + " ->");
            }
            Collections.reverse(actionList);
            jList.setListData(actionList.toArray(new String[0]));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Undo")) {
            if(window.getSelectedRoom() != null) {
                window.getSelectedRoom().getActionPerformer().undo(window);
            }
        }
        if(e.getActionCommand().equals("Redo")) {
            if(window.getSelectedRoom() != null) {
                window.getSelectedRoom().getActionPerformer().redo(window);
            }
        }
    }
}
