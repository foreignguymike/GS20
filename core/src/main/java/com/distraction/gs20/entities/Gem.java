package com.distraction.gs20.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.distraction.gs20.Context;

public class Gem extends ColorEntity {

    public enum Size {
        SMALL("1", 100),
        MEDIUM("2", 200),
        LARGE("3", 400);

        public final String name;
        public final int score;

        Size(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    private final TextureRegion image;

    public final Size size;

    public Gem(Context context, Type type, Size size) {
        super(type);
        this.size = size;

        image = context.getImage(type.name + size.name);
    }

    @Override
    public void render(Batch b) {
        b.setColor(1, 1, 1, 1);
        basicRender(b, image);
    }
}
