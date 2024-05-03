package net.loganford.nieEditor.ui;

import javax.swing.*;

public class Preferences {
    public void show(Window window) {

        JCheckBox darkModeBox = new JCheckBox("Dark Mode (Requires Restart)", "1".equals(window.loadVal(Window.DARK_MODE)));
        JTextArea launchCommand = new JTextArea(window.loadVal(Window.LAUNCH_COMMAND), 8, 48);
        launchCommand.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().add(launchCommand);

        JTextField workingDirectory = new JTextField(window.loadVal(Window.WORKING_DIRECTORY));

        JComponent[] inputs = {
                darkModeBox,
                new JLabel("Launch Command:"),
                scrollPane,
                new JLabel("Working Directory:"),
                workingDirectory
        };

        int result = JOptionPane.showConfirmDialog(null, inputs, "Preferences", JOptionPane.OK_CANCEL_OPTION);

        if(result == JOptionPane.OK_OPTION) {
            window.saveVal(Window.DARK_MODE, darkModeBox.isSelected() ? "1" : "0");
            window.saveVal(Window.LAUNCH_COMMAND, launchCommand.getText());
            window.saveVal(Window.WORKING_DIRECTORY, workingDirectory.getText());
        }
    }
}
