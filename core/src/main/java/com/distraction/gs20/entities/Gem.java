package com.distraction.gs20.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.distraction.gs20.Context;
import com.distraction.gs20.screens.PlayScreen;

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
    public final PlayScreen.Difficulty difficulty;

    public Gem(Context context, Type type, Size size, PlayScreen.Difficulty difficulty) {
        super(type);
        this.size = size;
        this.difficulty = difficulty;

        String name = type.name + size.name;

        image = context.getImage(name);
    }

    public int getPoints() {
        if (difficulty == PlayScreen.Difficulty.CHALLENGE) {
            return size.score;
        } else {
            return 100;
        }
    }

    @Override
    public void render(Batch b) {
        b.setColor(1, 1, 1, 1);
        basicRender(b, image);
    }
}
