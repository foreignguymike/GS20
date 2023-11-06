package com.distraction.gs20.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    public Pad pad;

    private float time = 0f;

    public Gem(Context context, Type type, Size size, PlayScreen.Difficulty difficulty, GemListener listener) {
        super(type);
        this.context = context;
        this.size = size;
        this.difficulty = difficulty;
        this.listener = listener;

        String name = type.name + size.name;
        if (context.babyMode) name += "a";

        image = context.getImage(name);
        scale = 0;
    }

    public void collect(Pad pad) {
        this.pad = pad;
        d.set(pad.p);
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
        time += dt;
        scale = time / 0.1f;
        if (scale > 1) scale = 1;
        if (!atDestination()) {
            setVectorFromDist(SPEED);
            move(dt);
            if (atDestination() && pad != null) {
                int points = getPoints();
                if (pad.type != type) {
                    points = -points;
                }
                listener.onScored(points);
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
