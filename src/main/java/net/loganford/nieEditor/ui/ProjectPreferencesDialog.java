package net.loganford.nieEditor.ui;

import javax.swing.*;

public class ProjectPreferencesDialog {
    public void show(Window window) {
        JTextArea compileCommand = new JTextArea(window.getProjectPreferences().getCompileCommand(), 8, 48);
        compileCommand.setLineWrap(true);
        JScrollPane compileCommandScroll = new JScrollPane();
        compileCommandScroll.getViewport().add(compileCommand);

        JTextArea launchCommand = new JTextArea(window.getProjectPreferences().getLaunchCommand(), 8, 48);
        launchCommand.setLineWrap(true);
        JScrollPane launchCommandScroll = new JScrollPane();
        launchCommandScroll.getViewport().add(launchCommand);

        JTextField workingDirectory = new JTextField(window.getProjectPreferences().getWorkingDirectory());

        JComponent[] inputs = {
                new JLabel("Working Directory:"),
                workingDirectory,
                new JLabel("Compile Command:"),
                compileCommandScroll,
                new JLabel("Run Command:"),
                launchCommandScroll
        };

        int result = JOptionPane.showConfirmDialog(null, inputs, "Preferences", JOptionPane.OK_CANCEL_OPTION);

        if(result == JOptionPane.OK_OPTION) {
            window.getProjectPreferences().setCompileCommand(compileCommand.getText());
            window.getProjectPreferences().setLaunchCommand(launchCommand.getText());
            window.getProjectPreferences().setWorkingDirectory(workingDirectory.getText());
        }
    }
}
