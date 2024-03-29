package net.loganford.nieEditor.ui.leftPane;

import net.loganford.nieEditor.ui.Window;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class TilePickerTab extends JPanel implements ActionListener, ItemListener {
    private JScrollPane scroller;
    private Window window;

    private JComboBox zoomBox;
    private JCheckBox showGrid;

    public TilePickerTab(Window window) {
        this.window = window;

        this.setLayout(new BorderLayout());
        scroller = new JScrollPane();

        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setWheelScrollingEnabled(true);

        scroller.getViewport().add(new TilePicker(window, scroller));

        add(scroller, BorderLayout.CENTER);
        add(tilePickerTools(), BorderLayout.SOUTH);
    }

    public JPanel tilePickerTools() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Zoom:"));
        zoomBox = new JComboBox<>(new String[]{"1", "2", "4", "8"});
        zoomBox.addActionListener(this);
        panel.add(zoomBox);

        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        separator.setPreferredSize(new Dimension(4, 24));
        panel.add(separator);

        showGrid = new JCheckBox("Show Grid", true);
        showGrid.addItemListener(this);
        panel.add(showGrid);

        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(zoomBox)) {
            window.getListeners().forEach(l -> l.tilePickerSettingsChanged(Integer.parseInt((String) zoomBox.getSelectedItem()), showGrid.isSelected()));
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if(e.getSource().equals(showGrid)) {
            window.getListeners().forEach(l -> l.tilePickerSettingsChanged(Integer.parseInt((String) zoomBox.getSelectedItem()), showGrid.isSelected()));
        }
    }
}
