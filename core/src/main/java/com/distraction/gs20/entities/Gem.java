package com.distraction.gs20.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.distraction.gs20.Context;
import com.distraction.gs20.screens.PlayScreen;
import com.distraction.gs20.utils.Constants;

public class Gem extends ColorEntity {

    public interface GemListener {
        void onScored(int points);
    }

    public enum Size {
        SMALL("1", 100),
        MEDIUM("2", 250),
        LARGE("3", 500);

        public final String name;
        public final int points;

        Size(String name, int points) {
            this.name = name;
            this.points = points;
        }
    }

    private static final float SPEED = Constants.WIDTH * 3f;

    private final Context context;
    private final TextureRegion image;

    public final Size size;
    public final PlayScreen.Difficulty difficulty;
    public final GemListener listener;
    public Tile tile;
    public boolean collecting;

    public Gem(Context context, Type type, Size size, PlayScreen.Difficulty difficulty, GemListener listener) {
        super(type);
        this.context = context;
        this.size = size;
        this.difficulty = difficulty;
        this.listener = listener;

        String name = type.name + size.name;
        if (context.babyMode) name += "a";

        image = context.getImage(name);
    }

    public void collect(Vector2 dest) {
        d.set(dest);
        collecting = true;
    }

    public int getPoints() {
        if (context.babyMode) {
            return 100;
        } else {
            return size.points + difficulty.points;
        }
    }

    @Override
    public void update(float dt) {
        if (!atDestination()) {
            setVectorFromDist(SPEED);
            move(dt);
            if (atDestination() && collecting) {
                listener.onScored(getPoints());
                remove = true;
            }
        }
    }

    @Override
    public void render(Batch b) {
        b.setColor(1, 1, 1, 1);
        basicRender(b, image);
    }
}
