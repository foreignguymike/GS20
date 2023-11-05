package com.distraction.gs20.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.distraction.gs20.Context;
import com.distraction.gs20.utils.Constants;

public class Hand extends ColorEntity {

    public interface HandListener {
        public void onGrabbedGem(Tile tile);
        public void onScored(int score);
    }

    private HandListener listener;

    private final TextureRegion image;
    private final float speed = Constants.WIDTH;

    private final Vector2 basePosition = new Vector2();

    public boolean boost = false;
    private Tile tile;
    private Gem gem;

    public Hand(Context context, Type type, HandListener listener) {
        super(type);
        this.listener = listener;

        image = context.getImage(type.name + "hand");
        setSize(image);
    }

    public void setBasePosition(Vector2 basePosition) {
        this.basePosition.set(basePosition);
        p.set(basePosition);
        d.set(basePosition);
    }

    public boolean atBasePosition() {
        return p.x == basePosition.x && p.y == basePosition.y;
    }

    public void grab(Tile tile) {
        if (!grabbing()) {
            this.tile = tile;
            d.set(tile.p);
        }
    }

    public boolean grabbing() {
        return p.x != basePosition.x || p.y != basePosition.y;
    }

    @Override
    public void update(float dt) {
        if (!atDestination()) {
            setVectorFromDist(speed * 3);
            move(dt);
            if (gem != null) {
                gem.p.set(p);
            }
            if (atDestination()) {
                if (tile != null) {
                    gem = tile.takeGem();
                    listener.onGrabbedGem(tile);
                    tile = null;
                }
                if (!atBasePosition()) {
                    d.set(basePosition);
                } else {
                    if (gem != null && gem.type == type) {
                        listener.onScored(gem.getPoints());
                    }
                    gem = null;
                }
            }
        }
    }

    @Override
    public void render(Batch b) {
        b.setColor(1, 1, 1, 1);
        b.draw(image, left(), bottom());
        if (gem != null) {
            gem.render(b);
        }
    }
}
