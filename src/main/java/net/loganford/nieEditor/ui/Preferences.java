package net.loganford.nieEditor.ui;

import javax.swing.*;

public class Preferences {
    public void show(Window window) {

        JCheckBox darkModeBox = new JCheckBox("Dark Mode (Requires Restart)", "1".equals(window.loadVal(Window.DARK_MODE)));

        JComponent[] inputs = {
                darkModeBox
        };

        int result = JOptionPane.showConfirmDialog(null, inputs, "Preferences", JOptionPane.OK_CANCEL_OPTION);

        if(result == JOptionPane.OK_OPTION) {
            window.saveVal(Window.DARK_MODE, darkModeBox.isSelected() ? "1" : "0");
        }
    }
}
