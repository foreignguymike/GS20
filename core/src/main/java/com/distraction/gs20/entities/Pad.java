package com.distraction.gs20.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.distraction.gs20.Context;

public class Pad extends ColorEntity {

    public final TextureRegion image;

    public Pad(Context context, Type type) {
        super(type);

        image = context.getImage(type.name + "pad");
        setSize(image);
    }

    @Override
    public void render(Batch b) {
        b.setColor(1, 1, 1, 1);
        b.draw(image, left(), bottom());
    }
}
