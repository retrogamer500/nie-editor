package net.loganford.nieEditor.data;

import lombok.Getter;
import lombok.Setter;
import net.loganford.nieEditor.ui.Window;
import net.loganford.nieEditor.util.EntityDefCache;
import net.loganford.nieEditor.util.ImageCache;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;

public class Entity {
    @Getter @Setter private transient boolean hidden = false;
    @Getter @Setter private int x;
    @Getter @Setter private int y;
    @Getter @Setter private String entityDefinitionUUID;
    @Getter @Setter private HashMap<String, String> properties = new HashMap<>();

    public Entity() {

    }

    public Entity(EntityDefinition ed, int x, int y) {
        entityDefinitionUUID = ed.getUuid();
        this.x = x;
        this.y = y;
    }

    public EntityDefinition getDefinition() {
        return EntityDefCache.getInstance().getEntityDef(entityDefinitionUUID);
    }

    public boolean collidesWith(Rectangle rectangle) {
        EntityDefinition ed = getDefinition();

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

    public boolean collidesWith(int x, int y) {
        EntityDefinition ed = getDefinition();

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
        EntityDefinition ed = getDefinition();

        if (ed.getImagePath() != null) {
            ImageIcon ic = ImageCache.getInstance().getImage(new File(ed.getImagePath()), ed.getWidth(), ed.getHeight());
            g.drawImage(ic.getImage(), getX(), getY(), ed.getWidth(), ed.getHeight(), new Color(0, 0, 0, 0), null);
        } else {
            g.setColor(Color.BLUE);
            g.drawRect(getX(), getY(), ed.getWidth(), ed.getHeight());
            g.setColor(Color.BLACK);
            g.drawString(ed.getName(), getX(), getY() + 10);
        }

        if(getProperties() != null && getProperties().size() > 0) {
            g.setColor(Color.RED);
            g.drawString("*", getX(), getY() + 2);
        }
    }
}
