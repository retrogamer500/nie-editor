package net.loganford.nieEditor.ui.leftPane;

import net.loganford.nieEditor.data.EntityDefinition;
import net.loganford.nieEditor.ui.Window;
import net.loganford.nieEditor.util.ImageCache;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.io.File;

public class EntityTreeRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        if (!leaf) {
            if (expanded) {
                ImageIcon icon = ImageCache.getInstance().getImage(new File("./data/minus.png"), 14, 14);
                setIcon(icon);
            } else {
                ImageIcon icon = ImageCache.getInstance().getImage(new File("./data/plus.png"), 14, 14);
                setIcon(icon);
            }
        }

        if(((DefaultMutableTreeNode) value).getUserObject() instanceof EntityDefinition) {
            EntityDefinition ed = (EntityDefinition)((DefaultMutableTreeNode) value).getUserObject();
            if(ed.getImagePath() != null) {
                ImageIcon icon = ImageCache.getInstance().getImage(new File(ed.getImagePath()), 14, 14);
                setIcon(icon);
            }
            else {
                ImageIcon icon = ImageCache.getInstance().getImage(new File("./data/obj.png"), 14, 14);
                setIcon(icon);
            }
        }

        return this;
    }
}
