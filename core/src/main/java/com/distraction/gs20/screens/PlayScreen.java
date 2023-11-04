package com.distraction.gs20.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.distraction.gs20.Context;
import com.distraction.gs20.utils.Constants;

public class PlayScreen extends GameScreen {

    public PlayScreen(Context context) {
        super(context);
    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(Batch b) {
        b.setProjectionMatrix(viewport.getCamera().combined);
        b.begin();
        b.setColor(Color.RED);
        b.draw(pixel, 0, 0, Constants.WIDTH, Constants.HEIGHT);
        b.end();
    }

}
