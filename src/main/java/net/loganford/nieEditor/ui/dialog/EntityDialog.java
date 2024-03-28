package net.loganford.nieEditor.ui.dialog;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.loganford.nieEditor.util.ImageCache;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Log4j2
public class EntityDialog implements ActionListener {
    public static final int IMG_WIDTH = 128;
    public static final int IMG_HEIGHT = 128;


    private boolean newEntity;

    @Getter private boolean accepted = false;

    @Getter @Setter private String name = "New Entity";
    @Getter @Setter private String classPath = "";
    @Getter @Setter private String group = "";

    @Getter @Setter private int width = 32;
    @Getter @Setter private int height = 32;

    @Getter @Setter private File imageFile;

    private JLabel fileLocationLabel;
    private JLabel imageLabel;
    private JSpinner widthSpinner;
    private JSpinner heightSpinner;

    public EntityDialog(boolean newEntity) {
        this.newEntity = newEntity;
    }

    public void show() {
        String title = newEntity ? "Create Entity" : "Edit Entity";

        JTextField nameField = new JTextField(name);
        JTextField classField = new JTextField(classPath);
        JTextField groupField = new JTextField(group);

        widthSpinner = new JSpinner(new SpinnerNumberModel(width, 1, 1000000, 1));
        heightSpinner = new JSpinner(new SpinnerNumberModel(height, 1, 1000000, 1));

        JComponent[] inputs = {
                new JLabel("Name:"),
                nameField,
                new JLabel("Classpath:"),
                classField,
                new JLabel("Group:"),
                groupField,
                filePanel(),
                new JLabel("Width:"),
                widthSpinner,
                new JLabel("Height:"),
                heightSpinner
        };

        int result = JOptionPane.showConfirmDialog(null, inputs, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        this.name = nameField.getText();
        this.classPath = classField.getText();
        this.group = groupField.getText();
        this.width = (Integer) widthSpinner.getValue();
        this.height = (Integer) heightSpinner.getValue();

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
            chooser.setFileFilter(new FileNameExtensionFilter("PNG Images", "png"));
            int returnVal = chooser.showOpenDialog(null);

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    imageFile = chooser.getSelectedFile();
                    fileLocationLabel.setText(imageFile.getAbsolutePath());

                    //Display icon
                    imageLabel.setIcon(ImageCache.getInstance().getImage(imageFile, IMG_WIDTH, IMG_HEIGHT));

                    //Set width and height sliders on image load
                    BufferedImage image = ImageIO.read(imageFile);
                    widthSpinner.setValue(image.getWidth());
                    heightSpinner.setValue(image.getHeight());
                }
                catch(IOException ioException) {
                    log.error(ioException);
                }
            }
        }
    }
}
