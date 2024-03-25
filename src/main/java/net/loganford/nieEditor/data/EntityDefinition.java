package net.loganford.nieEditor.data;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

public class EntityDefinition {
    @Getter @Setter private String name;
    @Getter @Setter private String classPath;
    @Getter @Setter private String uuid;
    @Getter @Setter private String group;
    @Getter @Setter private String imagePath;

    @Getter @Setter private int width;
    @Getter @Setter private int height;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        if(StringUtils.isNotBlank(classPath)) {
            sb.append(" (").append(classPath).append(")");
        }
        return sb.toString();
    }
}
