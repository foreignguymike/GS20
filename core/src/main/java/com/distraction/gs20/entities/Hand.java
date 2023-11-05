package com.distraction.gs20.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.distraction.gs20.Context;
import com.distraction.gs20.utils.Constants;

public class Hand extends ColorEntity {

    private final TextureRegion image;
    private final float speed = Constants.WIDTH;

    private final Vector2 basePosition = new Vector2();
    public boolean boost = false;

    public Hand(Context context, Type type) {
        super(type);

        image = context.getImage(type.name + "hand");
        setSize(image);
    }

    public void setBasePosition(Vector2 basePosition) {
        this.basePosition.set(basePosition);
        p.set(basePosition);
        d.set(basePosition);
    }

    public void grab(Vector2 g) {
        if (!grabbing()) {
            d.set(g);
        }
    }

    public boolean grabbing() {
        return p.x != basePosition.x || p.y != basePosition.y;
    }

    @Override
    public void update(float dt) {
        if (!atDestination()) {
            setVectorFromDist(boost ? speed * 3f : speed);
            move(dt);
            if (atDestination()) {
                d.set(basePosition);
            }
        }
    }

    @Override
    public void render(Batch b) {
        b.setColor(1, 1, 1, 1);
        b.draw(image, left(), bottom());
    }
}
