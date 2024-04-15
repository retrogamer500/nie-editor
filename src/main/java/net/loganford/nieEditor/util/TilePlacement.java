package net.loganford.nieEditor.util;

import lombok.Getter;
import lombok.Setter;
import net.loganford.nieEditor.data.Tileset;
import net.loganford.nieEditor.ui.Window;

import java.awt.*;


public class TilePlacement {
    @Getter @Setter private int x;
    @Getter @Setter private int y;

    @Getter @Setter private int tileX;
    @Getter @Setter private int tileY;

    public TilePlacement(int x, int y, int tileX, int tileY) {
        this.x = x;
        this.y = y;
        this.tileX = tileX;
        this.tileY = tileY;
    }

    public void render(Window window, Graphics g, Tileset ts) {
        Color clearColor = new Color(0, 0, 0, 0);

        g.drawImage(ts.getImage(window).getImage(),
                x * ts.getTileWidth(), y * ts.getTileHeight(),
                x * ts.getTileWidth() + ts.getTileWidth(), y * ts.getTileHeight() + ts.getTileHeight(),
                tileX * ts.getTileWidth(), tileY * ts.getTileHeight(),
                tileX * ts.getTileWidth() + ts.getTileWidth(), tileY * ts.getTileHeight() + ts.getTileHeight(),
                clearColor,
                null
        );
    }
}
