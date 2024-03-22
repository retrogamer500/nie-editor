package net.loganford.nieEditor.ui.leftPane;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class EntitiesTab extends JPanel {
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
        buttonPanel.add(new JButton("Add"));
        buttonPanel.add(new JButton("Remove"));
        buttonPanel.add(new JButton("Edit"));
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
