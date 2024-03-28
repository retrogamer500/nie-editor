package net.loganford.nieEditor.ui.leftPane;

import net.loganford.nieEditor.ui.Window;

import javax.swing.*;
import java.awt.*;

public class TilePickerTab extends JPanel {
    private JScrollPane scroller;
    private Window window;

    public TilePickerTab(Window window) {
        this.window = window;

        this.setLayout(new BorderLayout());
        scroller = new JScrollPane();

        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setWheelScrollingEnabled(true);

        scroller.getViewport().add(new TilePicker(window));
        add(scroller, BorderLayout.CENTER);
    }
}
