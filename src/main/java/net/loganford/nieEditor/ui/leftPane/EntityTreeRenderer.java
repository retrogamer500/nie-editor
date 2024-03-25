package net.loganford.nieEditor.ui.leftPane;

import net.loganford.nieEditor.data.EntityDefinition;
import net.loganford.nieEditor.ui.ImageCache;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class EntityTreeRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        if(((DefaultMutableTreeNode) value).getUserObject() instanceof EntityDefinition) {
            EntityDefinition ed = (EntityDefinition)((DefaultMutableTreeNode) value).getUserObject();
            if(ed.getImagePath() != null) {
                ImageIcon icon = ImageCache.getInstance().getImage(ed.getImagePath(), 16, 16);
                setIcon(icon);
            }
        }

        return this;
    }
}
