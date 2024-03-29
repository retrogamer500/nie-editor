package net.loganford.nieEditor.data;

import lombok.Getter;
import lombok.Setter;
import net.loganford.nieEditor.util.ImageCache;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.File;

public class Tileset {
    @Getter @Setter private String name;
    @Getter @Setter private String engineResourceKey;
    @Getter @Setter private String imagePath;
    @Getter @Setter private int tileWidth;
    @Getter @Setter private int tileHeight;

    @Getter @Setter private String uuid;

    @Override
    public String toString() {
        if(StringUtils.isNotBlank(engineResourceKey)) {
            return name + " (" + engineResourceKey + ")";
        }
        return name;
    }

    public ImageIcon getImage() {
        return ImageCache.getInstance().getImage(new File(imagePath));
    }
}
