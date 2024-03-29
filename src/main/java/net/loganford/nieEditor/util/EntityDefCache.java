package net.loganford.nieEditor.util;

import lombok.Getter;
import lombok.Setter;
import net.loganford.nieEditor.data.EntityDefinition;
import net.loganford.nieEditor.data.Project;

import java.util.HashMap;

public class EntityDefCache {
    private static EntityDefCache INSTANCE;

    public static EntityDefCache getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new EntityDefCache();
        }
        return INSTANCE;
    }

    @Getter @Setter private Project project;
    private HashMap<String, EntityDefinition> cache = new HashMap<>();

    public EntityDefinition getEntityDef(String uuid) {
        EntityDefinition ed = cache.get(uuid);

        if(ed == null && project != null) {
            ed = project.getEntityDefinitions().stream().filter(t -> t.getUuid().equals(uuid)).findFirst().orElse(null);
            cache.put(uuid, ed);
        }

        return ed;
    }
}
