package com.distraction.gs20.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.distraction.gs20.utils.Constants;
import com.distraction.gs20.Context;
import com.distraction.gs20.utils.MyViewport;

public abstract class GameScreen {

    /**
     * Flag for if this screen is at the top of the stack.
     */
    protected boolean active;

    protected Context context;

    /**
     * Camera.
     */
    protected MyViewport viewport;

    /**
     * Current mouse position.
     */
    protected Vector2 m = new Vector2();

    protected TextureRegion pixel;

    protected GameScreen(Context context) {
        this.context = context;
        viewport = new MyViewport(Constants.WIDTH, Constants.HEIGHT);

        pixel = context.getImage("pixel");
    }

    public abstract void onResume();

    public abstract void onPause();

    protected abstract void handleInput();

    public abstract void update(float dt);

    public abstract void render(Batch b);

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    /**
     * Projects mouse position from screen space to world space.
     */
    protected void unproject() {
        m.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(m);
    }

}
