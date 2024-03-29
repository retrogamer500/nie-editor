package net.loganford.nieEditor.data;

import lombok.Getter;
import lombok.Setter;
import net.loganford.nieEditor.ui.Window;
import net.loganford.nieEditor.util.ImageCache;

import javax.swing.*;
import java.awt.*;
import java.io.File;

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

    public void render(Window window, Graphics g) {
        EntityDefinition ed = window.getProject().getEntityCache(this);

        if (ed.getImagePath() != null) {
            ImageIcon ic = ImageCache.getInstance().getImage(new File(ed.getImagePath()), ed.getWidth(), ed.getHeight());
            g.drawImage(ic.getImage(), getX(), getY(), ed.getWidth(), ed.getHeight(), new Color(0, 0, 0, 0), null);
        } else {
            g.setColor(Color.BLUE);
            g.drawRect(getX(), getY(), ed.getWidth(), ed.getHeight());
            g.setColor(Color.BLACK);
            g.drawString(ed.getName(), getX(), getY() + 10);
        }
    }
}
