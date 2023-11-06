package com.distraction.gs20.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.distraction.gs20.Context;
import com.distraction.gs20.utils.Constants;

public class FinishScreen extends GameScreen {

    private final PlayScreen.Difficulty difficulty;

    private final TextureRegion bg;

    private float time;
    private float fade;
    private float camY;

    public FinishScreen(Context context, PlayScreen.Difficulty difficulty) {
        super(context);
        this.difficulty = difficulty;

        bg = context.getImage("finishbg");
        camY = -Constants.HEIGHT;
        viewport.setY(camY);
    }

    @Override
    protected void handleInput() {
        if (ignoreInput) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            ignoreInput = true;
            context.gsm.push(new FadeTransitionScreen(context, new PlayScreen(context, difficulty), 2));
        }
    }

    @Override
    public void update(float dt) {
        handleInput();

        fade += 2 * dt;
        if (fade > 0.7f) fade = 0.7f;

        camY += Constants.HEIGHT * 4 * dt;
        if (camY > Constants.HEIGHT / 2f) camY = Constants.HEIGHT / 2f;
        viewport.setY(camY);
    }

    @Override
    public void render(Batch b) {
        b.begin();
        {
            b.setProjectionMatrix(uiViewport.getCamera().combined);
            b.setColor(0, 0, 0, fade);
            b.draw(pixel, 0, 0, Constants.WIDTH, Constants.HEIGHT);

            b.setProjectionMatrix(viewport.getCamera().combined);
            b.setColor(1, 1, 1, 1);
            b.draw(bg, (Constants.WIDTH - bg.getRegionWidth()) / 2f, (Constants.HEIGHT - bg.getRegionHeight()) / 2f);
        }
        b.end();
    }

}
