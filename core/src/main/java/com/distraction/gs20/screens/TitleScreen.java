package com.distraction.gs20.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.distraction.gs20.Context;
import com.distraction.gs20.utils.Constants;

public class TitleScreen extends GameScreen {

    public TitleScreen(Context context) {
        super(context);
    }

    @Override
    protected void handleInput() {
        if (ignoreInput) return;
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            TransitionScreen screen = new FadeTransitionScreen(context, new PlayScreen(context));
            screen.duration = 2f;
            context.gsm.push(screen);
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(Batch b) {
        b.setProjectionMatrix(viewport.getCamera().combined);
        b.begin();
        b.setColor(Color.GREEN);
        b.draw(pixel, 0, 0, Constants.WIDTH, Constants.HEIGHT);
        b.end();
    }
}
