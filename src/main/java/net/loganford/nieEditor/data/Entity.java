package net.loganford.nieEditor.data;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

public class Entity {
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
}
