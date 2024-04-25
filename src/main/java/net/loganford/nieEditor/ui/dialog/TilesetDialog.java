package net.loganford.nieEditor.ui.dialog;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.loganford.nieEditor.ui.Window;
import net.loganford.nieEditor.util.ImageCache;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

@Log4j2
public class TilesetDialog implements ActionListener {
    public static final int IMG_WIDTH = 256;
    public static final int IMG_HEIGHT = 256;



    @Getter
    private boolean accepted = false;
    @Getter @Setter private String tilesetName = "New Tileset";
    @Getter @Setter private String group;
    @Getter @Setter private String engineResourceKey ="";
    @Getter @Setter private int tileWidth = 16;
    @Getter @Setter private int tileHeight = 16;
    @Getter @Setter private File imageFile;

    private boolean newTileset;
    private JLabel fileLocationLabel;
    private JLabel imageLabel;

    private Window window;

    public TilesetDialog(Window window, boolean newTileset) {
        this.newTileset = newTileset;
        this.window = window;
    }

    public void show() {
        String title = newTileset ? "Create Tileset" : "Edit Tileset";

        JTextField layerNameField = new JTextField(tilesetName);
        JTextField engineResourceNameField = new JTextField(engineResourceKey);
        JTextField groupField = new JTextField(group);

        JSpinner tileWidthSpinner = new JSpinner(new SpinnerNumberModel(tileWidth, 1, 1000000, 1));
        JSpinner tileHeightSpinner = new JSpinner(new SpinnerNumberModel(tileHeight, 1, 1000000, 1));


        JComponent[] inputs = {
                new JLabel("Tileset Name:"),
                layerNameField,
                new JLabel("Engine Resource Key:"),
                engineResourceNameField,
                new JLabel("Editor Folder (Separated By \".\")"),
                groupField,
                new JLabel("Tile Width:"),
                tileWidthSpinner,
                new JLabel("Tile Height:"),
                tileHeightSpinner,
                filePanel()
        };

        int result = JOptionPane.showConfirmDialog(null, inputs, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        this.tilesetName = layerNameField.getText();
        this.engineResourceKey = engineResourceNameField.getText();
        this.group = groupField.getText();

        this.accepted = result == JOptionPane.YES_OPTION;
    }

    private JPanel filePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        fileLocationLabel = new JLabel(imageFile == null ? "No image loaded" : imageFile.getAbsolutePath());
        panel.add(fileLocationLabel);

        ImageIcon imageIcon = imageFile != null ?  ImageCache.getInstance().getImage(imageFile, IMG_WIDTH, IMG_WIDTH) : new ImageIcon();
        imageLabel = new JLabel(imageIcon);
        imageLabel.setMaximumSize(new Dimension(IMG_WIDTH, IMG_HEIGHT));
        imageLabel.setPreferredSize(new Dimension(IMG_WIDTH, IMG_HEIGHT));
        panel.add(imageLabel);

        JButton button = new JButton("Load Image");
        button.addActionListener(this);
        panel.add(button);

        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Load Image")) {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(window.getProjectFile());
            chooser.setFileFilter(new FileNameExtensionFilter("PNG Images", "png"));

            if(imageFile != null) {
                chooser.setSelectedFile(imageFile);
            }

            int returnVal = chooser.showOpenDialog(null);

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                imageFile = chooser.getSelectedFile();
                fileLocationLabel.setText(imageFile.getAbsolutePath());

                //Display icon
                ImageCache.getInstance().clearCache(imageFile);
                imageLabel.setIcon(ImageCache.getInstance().getImage(imageFile, IMG_WIDTH, IMG_HEIGHT));
            }
        }
    }
}
