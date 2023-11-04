package com.distraction.gs20.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MyViewport extends FitViewport {

    public MyViewport(float worldWidth, float worldHeight) {
        super(worldWidth, worldHeight);
        update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    public void translate(float dx, float dy) {
        getCamera().position.add(dx, dy, 0);
        getCamera().update();
    }

    public void setPosition(float x, float y) {
        getCamera().position.set(x, y, 0);
        getCamera().update();
    }

}
