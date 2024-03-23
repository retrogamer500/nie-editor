package net.loganford.nieEditor.ui.dialog;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

public class EntityDialog implements PropertyChangeListener {
    private boolean newEntity;
    private JLabel imageLabel;

    @Getter private boolean accepted = false;

    @Getter @Setter private String name = "New Entity";
    @Getter @Setter private String className = "";
    @Getter @Setter private String group = "Default";

    @Getter @Setter private File imageFile;
    @Getter @Setter private int width = 32;
    @Getter @Setter private int height = 32;

    public EntityDialog(boolean newEntity) {
        this.newEntity = newEntity;
    }

    public void show() {
        String title = newEntity ? "Create Entity" : "Edit Entity";

        JTextField nameField = new JTextField(name);
        JTextField classField = new JTextField(className);
        JTextField groupField = new JTextField(group);

        imageLabel = new JLabel("Image: ");
        JFileChooser imageChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "png");
        imageChooser.setFileFilter(filter);
        imageChooser.addPropertyChangeListener(this);
        imageChooser.setControlButtonsAreShown(false);

        JSpinner widthSpinner = new JSpinner(new SpinnerNumberModel(width, 1, 1000000, 1));
        JSpinner heightSpinner = new JSpinner(new SpinnerNumberModel(height, 1, 1000000, 1));

        JComponent[] inputs = {
                new JLabel("Name:"),
                nameField,
                new JLabel("Classpath:"),
                classField,
                new JLabel("Group:"),
                groupField,
                new JLabel("Width:"),
                widthSpinner,
                new JLabel("Height:"),
                heightSpinner,
                imageLabel,
                imageChooser
        };

        int result = JOptionPane.showConfirmDialog(null, inputs, title, JOptionPane.OK_CANCEL_OPTION);

        this.name = nameField.getText();
        this.className = classField.getText();
        this.group = groupField.getText();
        this.width = (Integer) widthSpinner.getValue();
        this.height = (Integer) heightSpinner.getValue();

        this.accepted = result == JOptionPane.YES_OPTION;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName() == JFileChooser.SELECTED_FILE_CHANGED_PROPERTY) {
            if(evt.getNewValue() != null) {
                imageLabel.setText("Image: " + evt.getNewValue());
            }
            else {
                imageLabel.setText("Image: ");
            }
        }
    }
}
