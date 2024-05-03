package net.loganford.nieEditor.ui;

import javax.swing.*;

public class Preferences {
    public void show(Window window) {

        JCheckBox darkModeBox = new JCheckBox("Dark Mode (Requires Restart)", "1".equals(window.loadVal(Window.DARK_MODE)));

        JTextArea compileCommand = new JTextArea(window.loadVal(Window.COMPILE_COMMAND), 8, 48);
        compileCommand.setLineWrap(true);
        JScrollPane compileCommandScroll = new JScrollPane();
        compileCommandScroll.getViewport().add(compileCommand);

        JTextArea launchCommand = new JTextArea(window.loadVal(Window.LAUNCH_COMMAND), 8, 48);
        launchCommand.setLineWrap(true);
        JScrollPane launchCommandScroll = new JScrollPane();
        launchCommandScroll.getViewport().add(launchCommand);

        JTextField workingDirectory = new JTextField(window.loadVal(Window.WORKING_DIRECTORY));

        JComponent[] inputs = {
                darkModeBox,
                new JLabel("Working Directory:"),
                workingDirectory,
                new JLabel("Compile Command:"),
                compileCommandScroll,
                new JLabel("Run Command:"),
                launchCommandScroll
        };

        int result = JOptionPane.showConfirmDialog(null, inputs, "Preferences", JOptionPane.OK_CANCEL_OPTION);

        if(result == JOptionPane.OK_OPTION) {
            window.saveVal(Window.DARK_MODE, darkModeBox.isSelected() ? "1" : "0");
            window.saveVal(Window.COMPILE_COMMAND, compileCommand.getText());
            window.saveVal(Window.LAUNCH_COMMAND, launchCommand.getText());
            window.saveVal(Window.WORKING_DIRECTORY, workingDirectory.getText());
        }
    }
}
