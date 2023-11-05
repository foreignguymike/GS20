package com.distraction.gs20;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.distraction.gs20.screens.PlayScreen;
import com.distraction.gs20.screens.TitleScreen;

public class GemSnag20 extends ApplicationAdapter {

    private Context context;

    @Override
    public void create() {
        context = new Context();
        context.gsm.push(new PlayScreen(context, PlayScreen.Difficulty.EASY));
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        context.gsm.update(Gdx.graphics.getDeltaTime());
        context.gsm.render(context.b);
    }

    @Override
    public void resize(int width, int height) {
        context.gsm.resize(width, height);
    }

    @Override
    public void dispose() {
        context.dispose();
    }
}
