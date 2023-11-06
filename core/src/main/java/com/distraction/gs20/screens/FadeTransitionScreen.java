package com.distraction.gs20.screens;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.distraction.gs20.Context;
import com.distraction.gs20.utils.Constants;

public class FadeTransitionScreen extends TransitionScreen {

    public FadeTransitionScreen(Context context, GameScreen nextScreen) {
        this(context, nextScreen, 1);
    }

    public FadeTransitionScreen(Context context, GameScreen nextScreen, int pop) {
        super(context, nextScreen);
        this.pop = pop;
    }

    @Override
    public void update(float dt) {
        time += dt;
        if (!next && time > duration / 2) {
            next = true;
            nextScreen.ignoreInput = true;
            for (int i = 0; i < pop; i++) context.gsm.pop();
            context.gsm.depth -= pop - 1;
            context.gsm.replace(nextScreen);
            context.gsm.push(this);
        }
        if (time > duration) {
            context.gsm.depth--;
            context.gsm.pop();
            nextScreen.ignoreInput = false;
        }
    }

    @Override
    public void render(Batch b) {
        float percent = time / duration;
        float alpha = percent < 0.5f ? 3 * percent : 3 - 3 * percent;
        b.setColor(0, 0, 0, alpha);
        b.setProjectionMatrix(viewport.getCamera().combined);
        b.begin();
        {
            b.draw(pixel, 0, 0, Constants.WIDTH, Constants.HEIGHT);
        }
        b.end();
    }

}
