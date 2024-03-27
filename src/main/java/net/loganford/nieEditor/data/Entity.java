package net.loganford.nieEditor.data;

import lombok.Getter;
import lombok.Setter;
import net.loganford.nieEditor.ui.EditorWindow;
import net.loganford.nieEditor.ui.ImageCache;

import javax.swing.*;
import java.awt.*;

public class Entity {
    @Getter @Setter private transient boolean hidden = false;
    @Getter @Setter private int x;
    @Getter @Setter private int y;
    @Getter @Setter private String entityDefinitionUUID;

    public Entity() {

    }

    public Entity(EntityDefinition ed, int x, int y) {
        entityDefinitionUUID = ed.getUuid();
        this.x = x;
        this.y = y;
    }

    public boolean collidesWith(EntityDefinition ed, Rectangle rectangle) {
        if(this.x + ed.getWidth() <= rectangle.getX()) {
            return false;
        }
        if(this.y + ed.getHeight() <= rectangle.getY()) {
            return false;
        }
        if(this.x >= rectangle.getX() + rectangle.getWidth()) {
            return false;
        }
        if(this.y >= rectangle.getY() + rectangle.getHeight()) {
            return false;
        }

        return true;
    }

    public boolean collidesWith(EntityDefinition ed, int x, int y) {
        if(x < this.x) {
            return false;
        }
        if(y < this.y) {
            return false;
        }
        if(x > this.x + ed.getWidth()) {
            return false;
        }
        if(y > this.y + ed.getHeight()) {
            return false;
        }

        return true;
    }

    public void render(EditorWindow editorWindow, Graphics g) {
        EntityDefinition ed = editorWindow.getProject().getEntityInfo(this);

        if (ed.getImagePath() != null) {
            ImageIcon ic = ImageCache.getInstance().getImage(ed.getImagePath(), ed.getWidth(), ed.getHeight());
            g.drawImage(ic.getImage(), getX(), getY(), ed.getWidth(), ed.getHeight(), new Color(0, 0, 0, 0), null);
        } else {
            g.setColor(Color.CYAN);
            g.fillRect(getX(), getY(), ed.getWidth(), ed.getHeight());
            g.setColor(Color.BLUE);
            g.drawRect(getX(), getY(), ed.getWidth(), ed.getHeight());
            g.setColor(Color.BLACK);
            g.drawString(ed.getName(), getX(), getY() - 3);
        }
    }
}