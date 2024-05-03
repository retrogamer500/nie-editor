package net.loganford.nieEditor.ui;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.loganford.nieEditor.data.*;
import net.loganford.nieEditor.ui.leftPane.LeftPane;
import net.loganford.nieEditor.ui.leftPane.TilePicker;
import net.loganford.nieEditor.ui.rightPane.RightPane;
import net.loganford.nieEditor.util.EntityDefCache;
import net.loganford.nieEditor.util.ImageCache;
import net.loganford.nieEditor.util.ProjectListener;
import net.loganford.nieEditor.util.TilesetCache;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

@Log4j2
public class Window implements ActionListener, ProjectListener, WindowListener, WindowFocusListener {

    public static final String LAST_FILE_LOCATION = "LastFileLocation";
    public static final String DARK_MODE = "darkMode";

    public static boolean darkMode = false;

    @Getter List<ProjectListener> listeners = new ArrayList<ProjectListener>();

    @Getter private File projectFile;
    @Getter private Project project;
    private ProjectPreferences projectPreferences;

    @Getter private ToolPane toolPane;
    @Getter private RoomEditor roomPanel;
    @Getter private LeftPane leftPane;
    @Getter private Room selectedRoom;
    @Getter @Setter EntityDefinition selectedEntity;
    @Getter @Setter Tileset selectedTileset;
    @Getter @Setter TilePicker tilePicker;
    @Getter private int zoom = 1;
    private JFrame frame;
    private boolean projectDirty = false;
    private long projectLastModified;

    @Getter private JScrollPane roomScrollPane;

    public Window() {
        if("0".equals(loadVal("darkMode"))) {
            darkMode = false;
            FlatLightLaf.setup();
        }
        else {
            darkMode = true;
            FlatDarculaLaf.setup();
        }
        JFrame.setDefaultLookAndFeelDecorated(true);
        UIManager.put("Tree.rendererFillBackground", true);

        frame = new JFrame("NIE Editor");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1280, 720);
        frame.setLayout(new BorderLayout());
        frame.addWindowListener(this);
        frame.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width  - frame.getSize().width) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - frame.getSize().height) / 2);
        frame.setIconImage(ImageCache.getInstance().getImage(new File("./editor-data/sloth.png"), 32, 32).getImage());
        frame.addWindowFocusListener(this);

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

            menu.addSeparator();

            jmi = new JMenuItem("Goto Room");
            jmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK ) );
            jmi.addActionListener(this);
            menu.add(jmi);

            menu.addSeparator();

            jmi = new JMenuItem("Run");
            jmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK ) );
            jmi.addActionListener(this);
            menu.add(jmi);

            jmi = new JMenuItem("Build");
            jmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK ) );
            jmi.addActionListener(this);
            menu.add(jmi);

            menu.addSeparator();

            jmi = new JMenuItem("Project Preferences");
            jmi.addActionListener(this);
            menu.add(jmi);

            jmi = new JMenuItem("Preferences");
            jmi.addActionListener(this);
            menu.add(jmi);

            menuBar.add(menu);
        }

        {
            JMenu menu = new JMenu("Tools");

            JMenuItem jmi = new JMenuItem("Clone Room");
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
        leftPane = new LeftPane(this);
        frame.add(leftPane, BorderLayout.WEST);
        frame.add(roomGrid(), BorderLayout.CENTER);
        frame.add(new RightPane(this), BorderLayout.EAST);

        frame.setJMenuBar(menuBar);
        frame.setVisible(true);

        getListeners().add(this);

        //Attempt to reload previously used file
        if(loadVal(LAST_FILE_LOCATION) != null) {
            File lastFile = new File(loadVal(LAST_FILE_LOCATION));
            if(lastFile.exists()) {
               openProject(lastFile);
            }
        }
    }

    private JScrollPane roomGrid() {
        roomScrollPane = new JScrollPane();
        roomScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        roomScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        roomScrollPane.setWheelScrollingEnabled(true);

        roomPanel = new RoomEditor(this, 1000, 1000);

        if(Window.darkMode) {
            roomPanel.setBackground(Color.BLACK);
        }
        else {
            roomPanel.setBackground(Color.WHITE);
        }
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
            saveEditorData();
        }
        if(e.getActionCommand().equals("Quit")) {
            askToClose();
        }
        if(e.getActionCommand().equals("About")) {
            JOptionPane.showMessageDialog(null, "No Idea Engine Editor created by Logan Ford\nGithub: https://github.com/retrogamer500/nie-editor");
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
        if(e.getActionCommand().equals("Project Preferences")) {
            if(project != null) {
                new ProjectPreferencesDialog().show(this);
            }
        }
        if(e.getActionCommand().equals("Preferences")) {
            new net.loganford.nieEditor.ui.Preferences().show(this);;
        }
        if(e.getActionCommand().equals("Goto Room")) {
            openRoom();
        }
        if(e.getActionCommand().equals("Run")) {
            toolPane.launchGame(false);
        }
        if(e.getActionCommand().equals("Build")) {
            toolPane.launchGame(true);
        }
        if(e.getActionCommand().equals("Clone Room")) {
            cloneRoom();
        }
    }

    private void cloneRoom() {
        if(project != null && getSelectedRoom() != null) {
            JTextField textField = new JTextField();
            JCheckBox cloneTiles = new JCheckBox("Clone Tilemap", false);
            JCheckBox cloneEntities = new JCheckBox("Clone Entities", false);

            JComponent[] inputs = {
                    new JLabel("New Room Name:"),
                    textField,
                    cloneTiles,
                    cloneEntities
            };
            int result = JOptionPane.showConfirmDialog(null, inputs, "Clone Room: " + getSelectedRoom().getName(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(result == JOptionPane.OK_OPTION && StringUtils.isNoneBlank(textField.getText())) {
                Room room = getSelectedRoom().duplicate(textField.getText(), cloneTiles.isSelected(), cloneEntities.isSelected());
                getProject().getRooms().add(room);

                setSelectedRoom(room);
                getListeners().forEach(ProjectListener::roomListChanged);
            }
        }
        else {
            JOptionPane.showMessageDialog(null, "Please select a room to clone.");
        }
    }

    private void openRoom() {
        if(project != null) {
            JTextField textField = new JTextField();
            JComponent[] inputs = {
                    new JLabel("Room Name:"),
                    textField
            };
            int result = JOptionPane.showConfirmDialog(null, inputs, "Open Room", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(result == JOptionPane.OK_OPTION && StringUtils.isNoneBlank(textField.getText())) {
                Room room = project.getRooms().stream().filter(r -> r.getName().equals(textField.getText())).findFirst().orElse(null);
                if(room != null) {
                    setSelectedRoom(room);
                    getListeners().forEach(ProjectListener::roomListChanged);
                }
                else {
                    JOptionPane.showMessageDialog(null, "Room does not exist.");
                }
            }
        }
        else {
            JOptionPane.showMessageDialog(null, "A project must be open.");
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
            openProject(file);
        }
    }

    private File editorDataFile() {
        return new File(projectFile.getParentFile().getAbsolutePath() + "/" + projectFile.getName() + "d");
    }

    private void loadEditorData() {
        try {
            File editorDataFile = editorDataFile();
            if (editorDataFile.exists()) {
                String fileContents = FileUtils.readFileToString(editorDataFile, StandardCharsets.UTF_8);
                Gson gson = new Gson();
                projectPreferences = gson.fromJson(fileContents, ProjectPreferences.class);
            }
        }
        catch(IOException e) {
            log.error("Unable to load editor data", e);
        }
    }

    private void saveEditorData() {
        if(projectPreferences != null) {
            try {
                Gson gson = new Gson();
                File editorDataFile = editorDataFile();

                String data = gson.toJson(projectPreferences);
                FileUtils.writeStringToFile(editorDataFile, data, StandardCharsets.UTF_8);
            }
            catch(IOException e) {
                log.error("Unable to write editor data", e);
            }
        }
    }

    private void openProject(File file) {

        if(projectPreferences != null && project != null) {
            saveEditorData();
        }

        try {
            String fileContents = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            project = Project.load(fileContents);
            projectFile = file;
            projectLastModified = new File(projectFile.getAbsolutePath()).lastModified();
            selectedRoom = null;
            frame.setTitle("NIE Editor - " + file.getName());
            loadEditorData();
            setProjectDirty(false);
            getListeners().forEach(l -> l.projectChanged(project));
            getListeners().forEach(ProjectListener::roomListChanged);
            getListeners().forEach(l -> l.selectedRoomChanged(null));

        } catch (IOException ioException) {
            log.error("Unable to load file", ioException);
            JOptionPane.showMessageDialog(null, "Unable to load file!");
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
                projectLastModified = projectFile.lastModified();
                selectedRoom = null;
                FileUtils.writeStringToFile(file, project.save(), StandardCharsets.UTF_8);
                setProjectDirty(false);
                saveVal(LAST_FILE_LOCATION, projectFile.getAbsolutePath());
                loadEditorData();
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
                projectLastModified = projectFile.lastModified();
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
        saveEditorData();
        frame.dispose();
    }

    public static void main(String[] args) {
        Window window = new Window();
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

    public void saveVal(String key, String val) {
        Preferences.userRoot().node(this.getClass().getName()).put(key, val);
    }

    public String loadVal(String key) {
        return Preferences.userRoot().node(this.getClass().getName()).get(key, null);
    }

    public String getRelativeFilePath(File file) {
        Path filePath = Paths.get(file.getAbsolutePath());
        Path projectPath = Paths.get(projectFile.getParentFile().getAbsolutePath());

        return projectPath.relativize(filePath).toString();
    }

    public File getRelativeFile(String path) {
        File file = new File(path);
        if(file.isAbsolute()) {
            return file;
        }
        return new File(projectFile.getParent() + "/" + path);
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

    @Override
    public void projectChanged(Project project) {
        EntityDefCache.getInstance().setProject(project);
        TilesetCache.getInstance().setProject(project);
    }

    @Override
    public void windowGainedFocus(WindowEvent e) {
        Room oldSelectedRoom = selectedRoom;
        if(projectFile != null) {
            File testFile = new File(projectFile.getAbsolutePath());
            if (testFile.lastModified() != projectLastModified) {
                int dialogResult = JOptionPane.showConfirmDialog(null, "Project has been modified externally. Reload? This will undo any unsaved changes.", "Modified Project", JOptionPane.YES_NO_OPTION);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    openProject(projectFile);
                    selectedRoom = oldSelectedRoom;
                    getListeners().forEach(ProjectListener::roomListChanged);
                    getListeners().forEach(l -> l.selectedRoomChanged(selectedRoom));
                }
                else {
                    projectLastModified = testFile.lastModified();
                }
            }
        }
    }

    @Override
    public void windowLostFocus(WindowEvent e) {

    }

    public ProjectPreferences getProjectPreferences() {
        if(project != null && projectPreferences == null) {
            projectPreferences = new ProjectPreferences();
        }
        return projectPreferences;
    }
}
