package net.loganford.nieEditor.data;

import lombok.Getter;
import lombok.Setter;
import net.loganford.nieEditor.ui.Window;
import net.loganford.nieEditor.util.TilePlacement;
import net.loganford.nieEditor.util.TilesetCache;

import java.awt.*;


public class TileMap {
    @Getter @Setter private String tilesetUuid;

    @Getter @Setter private int width = 1;
    @Getter @Setter private int height = 1;

    @Getter @Setter private short[] tileData = new short[2 * width * height];

    public TileMap() {

    }

    public TileMap(String tilesetUuid) {
        this.tilesetUuid = tilesetUuid;
    }

    public Tileset getTileset() {
        return TilesetCache.getInstance().getTileset(tilesetUuid);
    }

    public void renderTileMap(Window window, Graphics g) {
        Image image = getTileset().getImage(window).getImage();
        Tileset ts = getTileset();
        Color clearColor = new Color(0, 0, 0, 0);

        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                int pos = (x + y * width) * 2;
                int tileX = tileData[pos] - 1;
                int tileY = tileData[pos + 1] - 1;
                if(tileX >= 0 && tileY >= 0) {
                    g.drawImage(image,
                            x * ts.getTileWidth(), y * ts.getTileHeight(),
                            x * ts.getTileWidth() + ts.getTileWidth(), y * ts.getTileHeight() + ts.getTileHeight(),
                            tileX * ts.getTileWidth(), tileY * ts.getTileHeight(),
                            tileX * ts.getTileWidth() + ts.getTileWidth(), tileY * ts.getTileHeight() + ts.getTileHeight(),
                            clearColor,
                            null
                    );
                }
            }
        }
    }

    public void removeTile(int x, int y) {
        if(x >= width || y >= height) {
            return;
        }

        int pos = (x + y * width) * 2;

        tileData[pos] = 0;
        tileData[pos + 1] = 0;
    }

    public TilePlacement getTilePlacement(int x, int y) {
        if(x >= width || y >= height) {
            return null;
        }

        int pos = (x + y * width) * 2;

        if(tileData[pos] == 0 || tileData[pos+1] == 0) {
            return null;
        }

        return new TilePlacement(x, y, tileData[pos] - 1, tileData[pos+1] - 1);
    }

    public void placeTile(int x, int y, int tileX, int tileY) {
        if(x >= width || y >= height) {
            resize(Math.max(width, x + 1), Math.max(height, y + 1));
        }

        int pos = (x + y * width) * 2;

        tileData[pos] = (short) (tileX + 1);
        tileData[pos + 1] = (short) (tileY + 1);
    }

    private void resize(int newWidth, int newHeight) {
        short[] newTileData = new short[2 * newWidth * newHeight];

        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                int oldPos = (x + y * width) * 2;
                int newPos = (x + newWidth * y) * 2;

                newTileData[newPos] = tileData[oldPos];
                newTileData[newPos + 1] = tileData[oldPos + 1];
            }
        }

        this.width = newWidth;
        this.height = newHeight;
        tileData = newTileData;
    }
}
