package com.distraction.gs20.entities;

public abstract class ColorEntity extends Entity {

    public enum Type {
        RED("red"),
        GREEN("green"),
        BLUE("blue"),
        YELLOW("yellow");

        public String name;

        Type(String name) {
            this.name = name;
        }
    }

    public Type type;

    protected ColorEntity(Type type) {
        this.type = type;
    }

}
