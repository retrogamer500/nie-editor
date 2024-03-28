package net.loganford.nieEditor.data;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Log4j2
public class Project {
    @Getter private transient HashMap<String, EntityDefinition> entityInfo = new HashMap<>();

    @Getter @Setter private String projectName;
    @Getter @Setter private List<Tileset> tilesets = new ArrayList<>();
    @Getter @Setter private List<EntityDefinition> entityDefinitions = new ArrayList<>();
    @Getter @Setter private List<Room> rooms = new ArrayList<>();

    public static Project load(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Project.class);
    }

    public String save() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public EntityDefinition getEntityInfo(Entity entity) {
        EntityDefinition def = entityInfo.get(entity.getEntityDefinitionUUID());

        if(def == null) {
            def = entityDefinitions.stream().filter(e -> e.getUuid().equals(entity.getEntityDefinitionUUID())).findFirst().orElse(null);

            if(def != null) {
                entityInfo.put(entity.getEntityDefinitionUUID(), def);
            }
            else {
                log.warn("Entity definition with UUID " + entity.getEntityDefinitionUUID() + " does not exist. Ignoring. ");
            }
        }

        return def;
    }
}
