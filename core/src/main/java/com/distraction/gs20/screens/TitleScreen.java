package com.distraction.gs20.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.distraction.gs20.Context;

public class TitleScreen extends GameScreen {

    public TitleScreen(Context context) {
        super(context);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    protected void handleInput() {
        if (!active) return;
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(Batch b) {
        b.setProjectionMatrix(viewport.getCamera().combined);
        b.begin();
        b.setColor(Color.GREEN);
        b.draw(pixel, 20, 20, 30, 30);
        b.end();
    }
}
