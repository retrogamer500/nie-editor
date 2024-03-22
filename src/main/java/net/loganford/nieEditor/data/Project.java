package net.loganford.nieEditor.data;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Project {
    @Getter @Setter private String projectName;
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
