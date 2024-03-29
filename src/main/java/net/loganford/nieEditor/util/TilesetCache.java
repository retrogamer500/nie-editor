package net.loganford.nieEditor.util;

import lombok.Getter;
import lombok.Setter;
import net.loganford.nieEditor.data.Project;
import net.loganford.nieEditor.data.Tileset;

import java.util.HashMap;

public class TilesetCache {
    private static TilesetCache INSTANCE;

    public static TilesetCache getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new TilesetCache();
        }
        return INSTANCE;
    }

    @Getter @Setter private Project project;
    private HashMap<String, Tileset> cache = new HashMap<>();

    public Tileset getTileset(String uuid) {
        Tileset ts = cache.get(uuid);

        if(ts == null && project != null) {
            ts = project.getTilesets().stream().filter(t -> t.getUuid().equals(uuid)).findFirst().orElse(null);
            cache.put(uuid, ts);
        }

        return ts;
    }
}
