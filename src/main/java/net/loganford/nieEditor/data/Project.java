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
}
