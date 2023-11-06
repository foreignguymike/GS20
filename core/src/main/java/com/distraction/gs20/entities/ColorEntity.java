package com.distraction.gs20.entities;

import com.badlogic.gdx.graphics.Color;
import com.distraction.gs20.utils.Constants;

public abstract class ColorEntity extends Entity {

    public enum Type {
        RED("red", Constants.RED),
        GREEN("green", Constants.GREEN),
        BLUE("blue", Constants.BLUE),
        YELLOW("yellow", Constants.YELLOW);

        public final String name;
        public final Color color;

        Type(String name, Color color) {
            this.name = name;
            this.color = color;
        }
    }

    public Type type;

    protected ColorEntity(Type type) {
        this.type = type;
    }

}
