package com.distraction.gs20.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ImageEntity extends Entity {

    private final TextureRegion image;

    public ImageEntity(TextureRegion image) {
        this.image = image;
        setSize(image);
    }

    @Override
    public void render(Batch b) {
        basicRender(b, image);
    }

}
