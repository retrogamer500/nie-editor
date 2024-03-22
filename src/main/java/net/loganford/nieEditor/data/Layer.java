package net.loganford.nieEditor.data;

import lombok.Getter;
import lombok.Setter;

public class Layer {
    @Getter @Setter private transient boolean visible = true;

    @Getter @Setter private String name = "New Layer";

    @Override
    public String toString() {
        if(visible) {
            return name;
        }
        else {
            return name + " (HIDDEN)";
        }
    }
}
