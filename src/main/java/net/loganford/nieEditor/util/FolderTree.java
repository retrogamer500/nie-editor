package net.loganford.nieEditor.util;

import com.sun.source.tree.Tree;
import lombok.Setter;
import net.loganford.nieEditor.data.EntityDefinition;
import net.loganford.nieEditor.ui.Window;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FolderTree<T> extends JTree implements TreeSelectionListener, MouseListener {
    private Window window;
    private Class<T> backingClass;
    private Function<T, String> pathFunction;
    private Function<T, ImageIcon> imageFunction;
    private Consumer<T> onChangeAction;
    @Setter private Consumer<T> onClickAction = null;

    private DefaultMutableTreeNode root;

    public FolderTree(Window window, Class<T> backingClass, Function<T, String> pathFunction, Function<T, ImageIcon> imageFunction, Consumer<T> onChangeAction) {
        super(new DefaultMutableTreeNode("Root"));

        this.window = window;
        this.backingClass = backingClass;
        this.pathFunction = pathFunction;
        this.imageFunction = imageFunction;
        this.onChangeAction = onChangeAction;
        this.root = ((DefaultMutableTreeNode) getModel().getRoot());

        FolderTreeRenderer folderTreeRenderer = new FolderTreeRenderer();
        setUI(new FolderTreeUI(folderTreeRenderer));
        setCellRenderer(folderTreeRenderer);

        setRootVisible(false);

        addTreeSelectionListener(this);
        addMouseListener(this);
    }

    public void render(List<T> backingList) {
        // Save a list of expanded groups
        //TreePath currentSelectionPath = getSelectionPath().getLastPathComponent();
        Set<String> expandedFolders = getExpandedFolders("Root.", root);

        T selectedObject = null;
        if(getSelectionPath() != null) {
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) getSelectionPath().getLastPathComponent();
            if(backingClass.isInstance(treeNode.getUserObject())) {
                selectedObject = (T) treeNode.getUserObject();
            }
        }

        root.removeAllChildren();

        //Populate tree
        for(T t : backingList) {
            String path = pathFunction.apply(t);
            String[] splitPath = path != null ? path.split("\\.") : new String[]{};
            DefaultMutableTreeNode node = root;

            //Traverse the folder path and create folders
            StringBuilder folderPath = new StringBuilder();
            for(String folder : splitPath) {
                folderPath.append(folderPath.length() == 0 ? folder : "." + folder);
                if(StringUtils.isNotBlank(folder)) {
                    DefaultMutableTreeNode foundNode = Collections.list(node.children()).stream()
                            .map((f) -> (DefaultMutableTreeNode) f)
                            .filter(DefaultMutableTreeNode::getAllowsChildren)
                            .filter(f -> f.getUserObject() instanceof String)
                            .filter(f -> ((String)f.getUserObject()).equals(folder))
                            .findFirst().orElse(null);

                    if(foundNode == null) {
                        foundNode = new DefaultMutableTreeNode(folder, true);
                        node.add(foundNode);
                    }

                    node = foundNode;
                }
            }

            DefaultMutableTreeNode entityNode = new DefaultMutableTreeNode(t, false);
            node.add(entityNode);
        }

        ((DefaultTreeModel)getModel()).reload(root);


        for(int i = 0; i < getRowCount(); i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) getPathForRow(i).getLastPathComponent();
            //Expand folders
            if(node.getUserObject() instanceof String) {
                String folderPath = Arrays.stream(getPathForRow(i).getPath())
                        .map(r -> (String)(((DefaultMutableTreeNode) r).getUserObject()))
                        .collect(Collectors.joining("."));
                if(expandedFolders.contains(folderPath)) {
                    expandRow(i);
                }
            }

            if(selectedObject != null && selectedObject.equals(node.getUserObject())) {
                setSelectionPath(getPathForRow(i));
            }
        }

        repaint();
    }

    private Set<String> getExpandedFolders(String prefix, DefaultMutableTreeNode node) {
        Set<String> returnSet = new HashSet<>();

        for(TreeNode childTreeNode : Collections.list(node.children())) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) childTreeNode;
            if(childNode.getUserObject() instanceof String) {
                String nodeName = (String) childNode.getUserObject();
                if(isExpanded(new TreePath(childNode.getPath()))) {
                    returnSet.add(prefix + nodeName);
                    returnSet.addAll(getExpandedFolders(nodeName + ".", childNode));
                }
            }
        }

        return returnSet;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if(e.getNewLeadSelectionPath() != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getNewLeadSelectionPath().getLastPathComponent();
            setSelectionPath(e.getNewLeadSelectionPath());
            if (backingClass.isInstance(node.getUserObject())) {
                T userObject = (T) node.getUserObject();
                onChangeAction.accept(userObject);
            }
            else {
                onChangeAction.accept(null);
            }
        }
        else {
            setSelectionPath(e.getOldLeadSelectionPath());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();
            if(backingClass.isInstance(node.getUserObject())) {
                T t = (T) node.getUserObject();
                if(onClickAction != null) {
                    onClickAction.accept(t);
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    class FolderTreeRenderer extends DefaultTreeCellRenderer {
        @Override
        public void setBackgroundSelectionColor(Color newColor) {
            backgroundSelectionColor = new Color(0, 0, 64);
        }

        @Override
        public void setBorderSelectionColor(Color newColor) {
            borderSelectionColor = new Color(255, 255, 255);
        }

        @Override
        public void setTextSelectionColor(Color newColor) {
            textSelectionColor = Color.white;
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

            if (!leaf) {
                if (expanded) {
                    ImageIcon icon = ImageCache.getInstance().getImage(new File("./editor-data/minus.png"), 14, 14);
                    setIcon(icon);
                } else {
                    ImageIcon icon = ImageCache.getInstance().getImage(new File("./editor-data/plus.png"), 14, 14);
                    setIcon(icon);
                }
            }

            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            if(backingClass.isInstance(userObject)) {
                T t = (T) userObject;
                ImageIcon icon = imageFunction.apply(t);
                if(icon != null) {
                    setIcon(icon);
                }
            }

            return this;
        }
    }

    class FolderTreeUI extends BasicTreeUI {
        private TreeCellRenderer myCellRenderer;

        public FolderTreeUI(TreeCellRenderer tcr) {
            super();
            cellRenderer = tcr;
        }

        @Override
        protected boolean shouldPaintExpandControl(TreePath path, int row, boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf) {
            super.shouldPaintExpandControl(path, row, isExpanded, hasBeenExpanded, isLeaf);
            return false;
        }
    }
}
