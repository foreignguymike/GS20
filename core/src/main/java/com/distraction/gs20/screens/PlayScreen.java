package com.distraction.gs20.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.distraction.gs20.Context;
import com.distraction.gs20.entities.ColorEntity;
import com.distraction.gs20.entities.Hand;
import com.distraction.gs20.entities.Pad;
import com.distraction.gs20.utils.Constants;

import java.util.HashMap;
import java.util.Map;

public class PlayScreen extends GameScreen {

    private final Vector2[] PAD_POSITIONS = new Vector2[] {
        new Vector2(Constants.WIDTH * 0.5f, Constants.HEIGHT * 0.9296f),
        new Vector2(Constants.WIDTH * 0.9296f, Constants.HEIGHT * 0.5f),
        new Vector2(Constants.WIDTH * 0.5f, Constants.HEIGHT * 0.0703f),
        new Vector2(Constants.WIDTH * 0.0703f, Constants.HEIGHT * 0.5f),
    };

    private final Map<Integer, ColorEntity.Type> keyHandMap = new HashMap<>() {{
        put(Input.Keys.W, ColorEntity.Type.RED);
        put(Input.Keys.D, ColorEntity.Type.GREEN);
        put(Input.Keys.S, ColorEntity.Type.BLUE);
        put(Input.Keys.A, ColorEntity.Type.YELLOW);
    }};

    private final Pad[] pads;
    private final Hand[] hands;

    public PlayScreen(Context context) {
        super(context);

        pads = new Pad[4];
        hands = new Hand[4];

        ColorEntity.Type[] types = ColorEntity.Type.values();
        for (int i = 0; i < types.length; i++) {
            pads[i] = new Pad(context, types[i]);
            pads[i].p.set(PAD_POSITIONS[i]);
            hands[i] = new Hand(context, types[i]);
            hands[i].setBasePosition(PAD_POSITIONS[i]);
        }
    }

    @Override
    protected void handleInput() {
        if (ignoreInput) return;

        unproject();
        keyHandMap.forEach((k, v) -> {
            if (Gdx.input.isKeyJustPressed(k)) {
                hands[v.ordinal()].grab(m);
            }
        });
    }

    @Override
    public void update(float dt) {
        handleInput();

        for (int i = 0; i < pads.length; i++) {
            pads[i].update(dt);
            hands[i].update(dt);
        }
    }

    @Override
    public void render(Batch b) {
        b.setProjectionMatrix(viewport.getCamera().combined);
        b.begin();
        {
            b.setColor(Constants.bgColor);
            b.draw(pixel, 0, 0, Constants.WIDTH, Constants.HEIGHT);

            b.setColor(1, 1, 1, 1);
            for (int i = 0; i < pads.length; i++) {
                pads[i].render(b);
                hands[i].render(b);
            }
        }
        b.end();
    }

}
