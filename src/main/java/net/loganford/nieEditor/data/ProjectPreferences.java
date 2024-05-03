package net.loganford.nieEditor.data;

import lombok.Getter;
import lombok.Setter;

public class ProjectPreferences {
    @Getter @Setter private String launchCommand;
    @Getter @Setter private String compileCommand;
    @Getter @Setter private String workingDirectory;

    @Getter @Setter private int defaultZoom = 1;
    @Getter @Setter private int defaultTileZoom = 1;
}
