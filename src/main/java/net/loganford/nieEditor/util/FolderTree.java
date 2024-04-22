package net.loganford.nieEditor.util;

import lombok.Setter;
import net.loganford.nieEditor.ui.Window;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FolderTree<T> extends JTree implements TreeSelectionListener, MouseListener {
    private Window window;
    private Class<T> backingClass;
    Supplier<List<T>> backingList;
    private Function<T, String> pathFunction;
    private PathSetter<T> pathSetter;
    private Function<T, ImageIcon> imageFunction;
    private Consumer<T> onChangeAction;
    @Setter private Consumer<T> onClickAction = null;

    private DefaultMutableTreeNode root;
    private DefaultMutableTreeNode sourceNode;

    public FolderTree(Window window, Class<T> backingClass, Supplier<List<T>> backingList, Function<T, String> pathFunction, PathSetter<T> pathSetter, Function<T, ImageIcon> imageFunction, Consumer<T> onChangeAction) {
        super(new DefaultMutableTreeNode("Root"));

        this.window = window;
        this.backingClass = backingClass;
        this.backingList = backingList;
        this.pathFunction = pathFunction;
        this.pathSetter = pathSetter;
        this.imageFunction = imageFunction;
        this.onChangeAction = onChangeAction;
        this.root = ((DefaultMutableTreeNode) getModel().getRoot());

        FolderTreeRenderer folderTreeRenderer = new FolderTreeRenderer();
        setUI(new FolderTreeUI(folderTreeRenderer));
        setCellRenderer(folderTreeRenderer);

        setRootVisible(false);

        addTreeSelectionListener(this);
        addMouseListener(this);

        setDragEnabled(true);
        setDropMode(DropMode.INSERT);

        setDropTarget(new DropTarget(this, TransferHandler.COPY, new FolderDropTargetAdapter()));
    }

    public void render(List<T> backingList) {
        backingList.sort(Comparator.comparing(o -> pathFunction.apply(o)));

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

    class FolderDropTargetAdapter extends DropTargetAdapter {
        @Override
        public void dragEnter(DropTargetDragEvent dtde) {
            TreePath path = getPathForLocation((int) dtde.getLocation().getX(), (int) dtde.getLocation().getY()); // Source
            if(path != null && path.getLastPathComponent() instanceof DefaultMutableTreeNode) {
                sourceNode = (DefaultMutableTreeNode) path.getLastPathComponent();
            }
        }

        @Override
        public void drop(DropTargetDropEvent dtde) {
            TreePath path = getPathForLocation((int) dtde.getLocation().getX(), (int) dtde.getLocation().getY()); // Destination
            DefaultMutableTreeNode destNode = path != null && path.getLastPathComponent() != null ? (DefaultMutableTreeNode) path.getLastPathComponent() : null;

            List<T> obtainedBackingList = backingList.get();
            if(sourceNode.getUserObject() instanceof String) {
                //Drag folder
                T tSource = (T) sourceNode.getUserObject();
                if(destNode != null && !(destNode.getUserObject() instanceof  String)) {
                    destNode = (DefaultMutableTreeNode) destNode.getParent();
                    if(destNode.isRoot()) {
                        destNode = null;
                    }
                }

                if(destNode == null || destNode.getUserObject() instanceof String) {
                    //Dragging a folder onto a folder
                    String sourceFolderPath = Arrays.stream(sourceNode.getPath())
                            .skip(1)
                            .map(r -> (String)(((DefaultMutableTreeNode) r).getUserObject()))
                            .collect(Collectors.joining("."));

                    String destFolderPath;
                    if(destNode != null) {
                        destFolderPath = Arrays.stream(destNode.getPath())
                                .skip(1)
                                .map(r -> (String) (((DefaultMutableTreeNode) r).getUserObject()))
                                .collect(Collectors.joining("."));
                        destFolderPath += "." + sourceNode.getUserObject();
                    }
                    else {
                        destFolderPath = (String) sourceNode.getUserObject();
                    }

                    for(T t : obtainedBackingList) {
                        if(pathFunction.apply(t).equals(sourceFolderPath)) {
                            pathSetter.setPath(t, destFolderPath);
                        }
                    }

                    window.setProjectDirty(true);
                    render(obtainedBackingList);
                }
            }
            else {
                if(destNode == null) {
                    return;
                }

                T tSource = (T) sourceNode.getUserObject();

                if(destNode.getUserObject() instanceof String) {
                    //Dragging a file onto a folder
                    if(destNode != sourceNode.getParent()) {
                        String folderPath = Arrays.stream(destNode.getPath())
                                .skip(1)
                                .map(r -> (String) (((DefaultMutableTreeNode) r).getUserObject()))
                                .collect(Collectors.joining("."));
                        obtainedBackingList.remove(tSource);
                        obtainedBackingList.add(0, tSource);
                        pathSetter.setPath(tSource, folderPath);
                        window.setProjectDirty(true);
                        render(obtainedBackingList);
                    }
                }
                else {
                    //Dragging a file onto a file
                    T tDest = (T) destNode.getUserObject();

                    if(tDest != tSource) {
                        Rectangle destRectangle = getPathBounds(new TreePath(destNode.getPath()));
                        boolean insertBefore = dtde.getLocation().getY() <= destRectangle.getY() + (destRectangle.getHeight() / 2);
                        obtainedBackingList.remove(tSource);
                        obtainedBackingList.add(obtainedBackingList.indexOf(tDest) + (insertBefore ? 0 : 1), tSource);
                        window.setProjectDirty(true);
                        render(obtainedBackingList);
                    }
                }
            }
        }
    }

    public interface PathSetter<T> {
        public void setPath(T t, String path);
    }
}
