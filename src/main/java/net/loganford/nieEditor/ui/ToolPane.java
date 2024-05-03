package net.loganford.nieEditor.ui;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.loganford.nieEditor.util.IconRadioButton;
import net.loganford.nieEditor.util.ImageCache;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Log4j2
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

        penTool = new IconRadioButton(new ImageIcon("./editor-data/pen.png"), true);
        rectangleTool = new IconRadioButton(new ImageIcon("./editor-data/rectangle.png"));
        instanceEditorTool = new IconRadioButton(new ImageIcon("./editor-data/instance_editor.png"));
        ButtonGroup toolButtonGroup = new ButtonGroup();
        toolButtonGroup.add(penTool);
        toolButtonGroup.add(rectangleTool);
        toolButtonGroup.add(instanceEditorTool);
        JLabel toolLabel = new JLabel("Tools:");
        add(toolLabel);
        add(penTool);
        add(rectangleTool);
        add(instanceEditorTool);

        //Run
        separator = new JSeparator(JSeparator.VERTICAL);
        separator.setPreferredSize(new Dimension(4, 24));
        add(separator);

        JButton compileRunButton = new JButton("Build", ImageCache.getInstance().getImage(new File("./editor-data/compile_run.png"), 14, 14));
        compileRunButton.addActionListener(this);
        add(compileRunButton);

        JButton runButton = new JButton("Run", ImageCache.getInstance().getImage(new File("./editor-data/run.png"), 14, 14));
        runButton.addActionListener(this);
        add(runButton);


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

        if(e.getActionCommand().equals("Run")) {
            launchGame(false);
        }

        if(e.getActionCommand().equals("Build")) {
            launchGame(true);
        }
    }

    public void launchGame(boolean compile) {
        try {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        super.run();

                        String compileCommand = window.loadVal(Window.COMPILE_COMMAND);
                        String launchCommand = window.loadVal(Window.LAUNCH_COMMAND);
                        if(window.getSelectedRoom() != null) {
                            launchCommand += " " + window.getSelectedRoom().getName();
                        }
                        String workingDirectory = window.loadVal(Window.WORKING_DIRECTORY);

                        if(launchCommand != null) {
                            if (compile && compileCommand != null) {
                                Process p = runCommand(compileCommand, workingDirectory);
                                p.waitFor();
                            }
                            if (launchCommand != null) {
                                runCommand(launchCommand, workingDirectory);
                            }
                        }
                    }
                    catch(Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            };

            thread.start();
        }
        catch(Exception e) {
            log.warn(e);
        }
    }

    private Process runCommand(String command, String workingDirectory) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(command.split(" "));
        builder = builder.directory(new File(workingDirectory));

        Process p = builder.start();
        InputStream inStream = p.getInputStream();
        InputStream errStream = p.getErrorStream();
        inStream.close();
        errStream.close();

        return p;
    }
}
