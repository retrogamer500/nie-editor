package net.loganford.nieEditor.data;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

public class GlobalPreferences {
    @Getter @Setter private HashMap<String, ProjectPreferences> projectPreferences;
}
