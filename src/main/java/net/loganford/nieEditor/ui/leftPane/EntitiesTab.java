package net.loganford.nieEditor.ui.leftPane;

import net.loganford.nieEditor.ui.dialog.EntityDialog;
import net.loganford.nieEditor.ui.dialog.RoomDialog;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EntitiesTab extends JPanel implements ActionListener {
    public EntitiesTab() {
        setLayout(new BorderLayout());

        //Setup history list
        ScrollPane scrollPane = new ScrollPane();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Entities");

        JTree tree = new JTree(root);
        tree.setRootVisible(false);
        scrollPane.add(tree);
        add(scrollPane, BorderLayout.CENTER);

        //Setup buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3));

        JButton addButton = new JButton("Add");
        addButton.addActionListener(this);
        buttonPanel.add(addButton);

        buttonPanel.add(new JButton("Remove"));
        buttonPanel.add(new JButton("Edit"));

        add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Add")) {
            EntityDialog ed = new EntityDialog(true);
            ed.show();
        }
    }
}
