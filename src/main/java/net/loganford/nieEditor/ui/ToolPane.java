package net.loganford.nieEditor.ui;

import lombok.Getter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class ToolPane extends JPanel implements ChangeListener {
    private EditorWindow editorWindow;

    @Getter private JCheckBox snapObjects;
    @Getter private JCheckBox overwriteObjects;
    @Getter private JCheckBox showGrid;
    @Getter private JSpinner gridWidth;
    @Getter private JSpinner gridHeight;

    public ToolPane(EditorWindow editorWindow) {
        this.editorWindow = editorWindow;

        setLayout(new FlowLayout(FlowLayout.LEFT));

        snapObjects = new JCheckBox("Snap Objects", true);
        snapObjects.addChangeListener(this);
        add(snapObjects);

        overwriteObjects = new JCheckBox("Overwrite Objects", true);
        overwriteObjects.addChangeListener(this);
        add(overwriteObjects);

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


        setVisible(true);
    }

    public void stateChanged(ChangeEvent e) {
        if(e.getSource() == gridWidth) {
            editorWindow.getRoomPanel().setGridWidth((Integer)(((JSpinner)e.getSource()).getValue()));
            editorWindow.getRoomPanel().repaint();
        }

        if(e.getSource() == gridHeight) {
            editorWindow.getRoomPanel().setGridHeight((Integer)(((JSpinner)e.getSource()).getValue()));
            editorWindow.getRoomPanel().repaint();
        }

        if(e.getSource() == showGrid) {
            editorWindow.getRoomPanel().setShowGrid(((JCheckBox)e.getSource()).isSelected());
            editorWindow.getRoomPanel().repaint();
        }
    }
}
