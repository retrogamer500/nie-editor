package net.loganford.nieEditor.ui;

import lombok.Getter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToolPane extends JPanel implements ChangeListener, ActionListener {
    private Window window;

    @Getter private JCheckBox snapEntities;
    @Getter private JCheckBox overwriteEntities;
    @Getter private JCheckBox showGrid;
    @Getter private JSpinner gridWidth;
    @Getter private JSpinner gridHeight;
    @Getter private JRadioButton penTool;
    @Getter private JRadioButton rectangleTool;
    @Getter private JRadioButton instanceEditorTool;

    @Getter private JComboBox<String> zoomBox;

    public ToolPane(Window window) {
        this.window = window;

        setLayout(new FlowLayout(FlowLayout.LEFT));

        snapEntities = new JCheckBox("Snap Entities", true);
        snapEntities.addChangeListener(this);
        add(snapEntities);

        overwriteEntities = new JCheckBox("Overwrite Entities", true);
        overwriteEntities.addChangeListener(this);
        add(overwriteEntities);

        showGrid = new JCheckBox("Show Grid", true);
        showGrid.addChangeListener(this);
        add(showGrid);

        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        separator.setPreferredSize(new Dimension(4, 24));
        add(separator);

        JLabel gridWidthLabel = new JLabel("Grid Width:");
        add(gridWidthLabel);
        gridWidth = new JSpinner(new SpinnerNumberModel(16, 4, 128, 1));
        gridWidth.addChangeListener(this);
        add(gridWidth);

        JLabel gridHeightLabel = new JLabel("Grid Height:");
        add(gridHeightLabel);
        gridHeight = new JSpinner(new SpinnerNumberModel(16, 4, 128, 1));
        gridHeight.addChangeListener(this);
        add(gridHeight);

        //Zoom
        separator = new JSeparator(JSeparator.VERTICAL);
        separator.setPreferredSize(new Dimension(4, 24));
        add(separator);

        JLabel zoomLabel = new JLabel("Zoom:");
        add(zoomLabel);
        String[] zoomOptions = {"1", "2", "4", "8"};
        zoomBox = new JComboBox<>(zoomOptions);
        zoomBox.addActionListener(this);
        add(zoomBox);

        //Tools
        separator = new JSeparator(JSeparator.VERTICAL);
        separator.setPreferredSize(new Dimension(4, 24));
        add(separator);

        penTool = new JRadioButton("Pen", true);
        rectangleTool = new JRadioButton("Rectangle");
        instanceEditorTool = new JRadioButton("Instance Editor");
        ButtonGroup toolButtonGroup = new ButtonGroup();
        toolButtonGroup.add(penTool);
        toolButtonGroup.add(rectangleTool);
        toolButtonGroup.add(instanceEditorTool);
        JLabel toolLabel = new JLabel("Tools:");
        add(toolLabel);
        add(penTool);
        add(rectangleTool);
        add(instanceEditorTool);



        setVisible(true);
    }

    public void stateChanged(ChangeEvent e) {
        if(e.getSource() == gridWidth) {
            window.getRoomPanel().setGridWidth((Integer)(((JSpinner)e.getSource()).getValue()));
            window.getRoomPanel().repaint();
        }

        if(e.getSource() == gridHeight) {
            window.getRoomPanel().setGridHeight((Integer)(((JSpinner)e.getSource()).getValue()));
            window.getRoomPanel().repaint();
        }

        if(e.getSource() == showGrid) {
            window.getRoomPanel().setShowGrid(((JCheckBox)e.getSource()).isSelected());
            window.getRoomPanel().repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == zoomBox) {
            int zoom = Integer.parseInt((String) zoomBox.getSelectedItem());
            window.setZoom(zoom);
        }
    }
}
