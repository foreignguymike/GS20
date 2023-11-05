package com.distraction.gs20.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class PopupImage extends Entity {

    private TextureRegion image;
    private float alpha = 1f;

    private boolean start;
    private float timer;

    public PopupImage(TextureRegion image) {
        this.image = image;
        setSize(image);
    }

    public void start() {
        start = true;
    }

    @Override
    public void update(float dt) {
        if (!start) return;
        timer += dt;
        if (timer < 0.5f) {
            scale = MathUtils.map(0, 0.5f, 10f, -10f, timer);
            if (scale < 1) scale = 1;
        } else if (timer < 1f) {
            scale = 1f;
            alpha = MathUtils.map(0.5f, 1f, 1f, -1f, timer);
            alpha = MathUtils.clamp(alpha, 0, 1);
        } else {
            alpha = 0f;
        }
    }

    @Override
    public void render(Batch b) {
        if (!start) return;
        b.setColor(1, 1, 1, alpha);
        b.draw(image, sleft(), sbottom(), swidth(), sheight());
    }

}
