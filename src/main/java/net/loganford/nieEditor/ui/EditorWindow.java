package net.loganford.nieEditor.ui;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.loganford.nieEditor.data.EntityDefinition;
import net.loganford.nieEditor.data.Project;
import net.loganford.nieEditor.data.Room;
import net.loganford.nieEditor.ui.leftPane.LeftPane;
import net.loganford.nieEditor.ui.rightPane.RightPane;
import net.loganford.nieEditor.util.ImageCache;
import net.loganford.nieEditor.util.ProjectListener;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

@Log4j2
public class EditorWindow implements ActionListener, ProjectListener, WindowListener {

    public static final String LAST_FILE_LOCATION = "LastFileLocation";

    @Getter List<ProjectListener> listeners = new ArrayList<ProjectListener>();

    @Getter private File projectFile;
    @Getter private Project project;

    @Getter private ToolPane toolPane;
    @Getter private RoomEditor roomPanel;
    @Getter private Room selectedRoom;
    @Getter @Setter EntityDefinition selectedEntity;
    @Getter private int zoom = 1;
    private JFrame frame;
    private boolean projectDirty = false;

    @Getter private JScrollPane roomScrollPane;

    public EditorWindow() {
        try
        {
            javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) { }
        JFrame.setDefaultLookAndFeelDecorated(true);

        frame = new JFrame("NIE Editor");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1280, 720);
        frame.setLayout(new BorderLayout());
        frame.addWindowListener(this);
        frame.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width  - frame.getSize().width) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - frame.getSize().height) / 2);
        frame.setIconImage(ImageCache.getInstance().getImage("./data/sloth.png", 16, 16).getImage());

        JMenuBar menuBar = new JMenuBar();
        {
            JMenu menu = new JMenu("File");

            JMenuItem jmi = new JMenuItem("New Project");
            jmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK ) );
            jmi.addActionListener(this);
            menu.add(jmi);

            jmi = new JMenuItem("Open Project");
            jmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK ) );
            jmi.addActionListener(this);
            menu.add(jmi);

            jmi = new JMenuItem("Save Project");
            jmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK ) );
            jmi.addActionListener(this);
            menu.add(jmi);

            menu.addSeparator();

            jmi = new JMenuItem("Quit");
            jmi.addActionListener(this);
            menu.add(jmi);

            menuBar.add(menu);
        }

        {
            JMenu menu = new JMenu("Edit");

            JMenuItem jmi = new JMenuItem("Undo");
            jmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK ) );
            jmi.addActionListener(this);
            menu.add(jmi);

            jmi = new JMenuItem("Redo");
            jmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK ) );
            jmi.addActionListener(this);
            menu.add(jmi);

            menuBar.add(menu);
        }

        {
            JMenu menu = new JMenu("Help");

            JMenuItem jmi = new JMenuItem("About");
            jmi.addActionListener(this);
            menu.add(jmi);

            menuBar.add(menu);
        }

        toolPane = new ToolPane(this);
        frame.add(toolPane, BorderLayout.NORTH);
        frame.add(new LeftPane(this), BorderLayout.WEST);
        frame.add(roomGrid(), BorderLayout.CENTER);
        frame.add(new RightPane(this), BorderLayout.EAST);

        frame.setJMenuBar(menuBar);
        frame.setVisible(true);

        getListeners().add(this);

        //Attempt to reload previously used file
        if(loadVal(LAST_FILE_LOCATION) != null) {
            File lastFile = new File(loadVal(LAST_FILE_LOCATION));
            if(lastFile.exists()) {
                try {
                    String fileContents = FileUtils.readFileToString(lastFile, StandardCharsets.UTF_8);
                    project = Project.load(fileContents);
                    projectFile = lastFile;
                    frame.setTitle("NIE Editor - " + lastFile.getName());
                    setProjectDirty(false);
                    getListeners().forEach(l -> l.projectChanged(project));
                    getListeners().forEach(ProjectListener::roomListChanged);
                    getListeners().forEach(l -> l.selectedRoomChanged(null));
                }
                catch(IOException ioException) {
                    log.error("Unable to load file", ioException);
                    JOptionPane.showMessageDialog(null, "Unable to load file!");
                }
            }
        }
    }

    private JScrollPane roomGrid() {
        roomScrollPane = new JScrollPane();
        roomScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        roomScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        roomScrollPane.setWheelScrollingEnabled(true);

        roomPanel = new RoomEditor(this, 1000, 1000);

        roomPanel.setBackground(Color.BLACK);
        roomPanel.setVisible(true);

        roomScrollPane.getViewport().add(roomPanel);
        return roomScrollPane;
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("New Project")) {
            newProject();
        }
        if(e.getActionCommand().equals("Open Project")) {
            openProject();
        }
        if(e.getActionCommand().equals("Save Project")) {
            saveProject();
        }
        if(e.getActionCommand().equals("Quit")) {
            askToClose();
        }
        if(e.getActionCommand().equals("About")) {
            JOptionPane.showMessageDialog(null, "No Idea Engine Editor created by Logan Ford");
        }
        if(e.getActionCommand().equals("Undo")) {
            if(getSelectedRoom() != null) {
                getSelectedRoom().getActionPerformer().undo(this);
            }
        }
        if(e.getActionCommand().equals("Redo")) {
            if(getSelectedRoom() != null) {
                getSelectedRoom().getActionPerformer().redo(this);
            }
        }
    }

    private void openProject() {
        if(project != null && projectDirty) {
            int dialogResult = JOptionPane.showConfirmDialog (null, "Would you like to save your current project first?", "Warning", JOptionPane.YES_NO_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION){
                saveProject();
            }
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("NIE Editor Projects", "nep"));
        int returnVal = chooser.showOpenDialog(null);

        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                String fileContents = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                project = Project.load(fileContents);
                projectFile = file;
                frame.setTitle("NIE Editor - " + file.getName());
                setProjectDirty(false);
                getListeners().forEach(l -> l.projectChanged(project));
                getListeners().forEach(ProjectListener::roomListChanged);
                getListeners().forEach(l -> l.selectedRoomChanged(null));

            } catch (IOException ioException) {
                log.error("Unable to load file", ioException);
                JOptionPane.showMessageDialog(null, "Unable to load file!");
            }

        }
    }


    private void newProject() {
        if(project != null && projectDirty) {
            int dialogResult = JOptionPane.showConfirmDialog (null, "Would you like to save your current project first?", "Warning", JOptionPane.YES_NO_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION){
                saveProject();
            }
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Create New Project");
        chooser.setSelectedFile(new File("new_project.nep"));
        chooser.setFileFilter(new FileNameExtensionFilter("NIE Editor Projects", "nep"));
        int returnVal = chooser.showSaveDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile().getAbsoluteFile();

            if(file.exists()) {
                int dialogResult = JOptionPane.showConfirmDialog (null, "Do you want to overwrite this file?", "Warning", JOptionPane.YES_NO_OPTION);
                if(dialogResult != JOptionPane.YES_OPTION){
                    return;
                }
            }

            try {
                project = new Project();
                projectFile = file;
                selectedRoom = null;
                FileUtils.writeStringToFile(file, project.save(), StandardCharsets.UTF_8);
                setProjectDirty(false);
                saveVal(LAST_FILE_LOCATION, projectFile.getAbsolutePath());
                getListeners().forEach(l -> l.projectChanged(project));
                getListeners().forEach(ProjectListener::roomListChanged);
                getListeners().forEach(l -> l.selectedRoomChanged(null));
            } catch (IOException ioException) {
                log.error("Unable to save file", ioException);
                JOptionPane.showMessageDialog(null, "Unable to save file!");
            }
            frame.setTitle("NIE Editor - " + file.getName());
        }
    }

    private void saveProject() {
        if(project != null && projectFile != null) {
            try {
                FileUtils.writeStringToFile(projectFile, project.save(), StandardCharsets.UTF_8);
                setProjectDirty(false);
                saveVal(LAST_FILE_LOCATION, projectFile.getAbsolutePath());
            } catch (IOException ioException) {
                log.error("Unable to save file", ioException);
                JOptionPane.showMessageDialog(null, "Unable to save file!");
            }
        }
        else {
            JOptionPane.showMessageDialog(null, "Try creating a new project first!");
        }
    }

    public void setSelectedRoom(Room room) {
        this.selectedRoom = room;
        getListeners().forEach(l -> l.selectedRoomChanged(room));
    }

    public void askToClose() {
        if(project != null && projectDirty) {
            int dialogResult = JOptionPane.showConfirmDialog(null, "Do you want to save before you quit?", "Warning", JOptionPane.YES_NO_OPTION);
            if (dialogResult == JOptionPane.CLOSED_OPTION) {
                return;
            }
            if (dialogResult == JOptionPane.YES_OPTION) {
                saveProject();
            }
        }
        frame.dispose();
    }

    public static void main(String[] args) {
        EditorWindow window = new EditorWindow();
    }

    public void setProjectDirty(boolean dirty) {
        this.projectDirty = dirty;

        if(dirty) {
            frame.setTitle("*" + projectFile.getName() + " - NIE Editor");
        }
        else {
            frame.setTitle(projectFile.getName() + " - NIE Editor");
        }
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
        getListeners().forEach(l -> l.selectedRoomChanged(getSelectedRoom()));
        roomScrollPane.repaint();
    }

    private void saveVal(String key, String val) {
        Preferences.userRoot().node(this.getClass().getName()).put(key, val);
    }

    private String loadVal(String key) {
        return Preferences.userRoot().node(this.getClass().getName()).get(key, null);
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        askToClose();
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
