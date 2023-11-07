package com.distraction.gs20.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.distraction.gs20.utils.Tween;

public class PopupImage extends Entity {

    private final TextureRegion image;
    private float alpha = 1f;

    private boolean start;
    private float time;

    public Tween alphaTween;
    public Tween scaleTween;
    public float duration;

    public PopupImage(TextureRegion image) {
        this.image = image;
        setSize(image);

        // default values
        alphaTween = (t) -> {
            float v = 0;
            if (t < 0.5f) return t * 4f;
            else if (t < 1f) return MathUtils.map(0.5f, 1f, 1f, -1f, t);
            v = MathUtils.clamp(v, 0, 1);
            return v;
        };
        scaleTween = (t) -> {
            float v = 0;
            if (t < 0.5f) {
                v = MathUtils.map(0, 0.5f, 10f, -10f, t);
                if (v < 1) v = 1;
            } else if (t < 1f) {
                v = 1f;
            }
            return v;
        };
        duration = 1f;
    }

    public void setup(Tween alphaTween, Tween scaleTween, float duration) {
        this.alphaTween = alphaTween;
        this.scaleTween = scaleTween;
        this.duration = duration;
    }

    public void start() {
        start = true;
    }

    @Override
    public void update(float dt) {
        if (!start) return;
        time += dt;
        alpha = alphaTween.update(time);
        scale = scaleTween.update(time);
        if (time >= duration) remove = true;
    }

    @Override
    public void render(Batch b) {
        if (!start) return;
        b.setColor(1, 1, 1, alpha);
        b.draw(image, sleft(), sbottom(), swidth(), sheight());
    }

}
