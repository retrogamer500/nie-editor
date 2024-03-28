package net.loganford.nieEditor.data;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

public class Tileset {
    @Getter @Setter private String name;
    @Getter @Setter private String engineResourceKey;
    @Getter @Setter private String imagePath;
    @Getter @Setter private int tileWidth;
    @Getter @Setter private int tileHeight;

    @Override
    public String toString() {
        if(StringUtils.isNotBlank(engineResourceKey)) {
            return name + " (" + engineResourceKey + ")";
        }
        return name;
    }
}
