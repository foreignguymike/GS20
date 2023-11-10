package com.distraction.gs20.screens;


import static com.badlogic.gdx.Input.Keys.A;
import static com.badlogic.gdx.Input.Keys.B;
import static com.badlogic.gdx.Input.Keys.BACKSPACE;
import static com.badlogic.gdx.Input.Keys.C;
import static com.badlogic.gdx.Input.Keys.D;
import static com.badlogic.gdx.Input.Keys.E;
import static com.badlogic.gdx.Input.Keys.ESCAPE;
import static com.badlogic.gdx.Input.Keys.F;
import static com.badlogic.gdx.Input.Keys.G;
import static com.badlogic.gdx.Input.Keys.H;
import static com.badlogic.gdx.Input.Keys.I;
import static com.badlogic.gdx.Input.Keys.J;
import static com.badlogic.gdx.Input.Keys.K;
import static com.badlogic.gdx.Input.Keys.L;
import static com.badlogic.gdx.Input.Keys.M;
import static com.badlogic.gdx.Input.Keys.N;
import static com.badlogic.gdx.Input.Keys.NUM_0;
import static com.badlogic.gdx.Input.Keys.NUM_1;
import static com.badlogic.gdx.Input.Keys.NUM_2;
import static com.badlogic.gdx.Input.Keys.NUM_3;
import static com.badlogic.gdx.Input.Keys.NUM_4;
import static com.badlogic.gdx.Input.Keys.NUM_5;
import static com.badlogic.gdx.Input.Keys.NUM_6;
import static com.badlogic.gdx.Input.Keys.NUM_7;
import static com.badlogic.gdx.Input.Keys.NUM_8;
import static com.badlogic.gdx.Input.Keys.NUM_9;
import static com.badlogic.gdx.Input.Keys.O;
import static com.badlogic.gdx.Input.Keys.P;
import static com.badlogic.gdx.Input.Keys.Q;
import static com.badlogic.gdx.Input.Keys.R;
import static com.badlogic.gdx.Input.Keys.S;
import static com.badlogic.gdx.Input.Keys.SHIFT_LEFT;
import static com.badlogic.gdx.Input.Keys.SHIFT_RIGHT;
import static com.badlogic.gdx.Input.Keys.T;
import static com.badlogic.gdx.Input.Keys.U;
import static com.badlogic.gdx.Input.Keys.V;
import static com.badlogic.gdx.Input.Keys.W;
import static com.badlogic.gdx.Input.Keys.X;
import static com.badlogic.gdx.Input.Keys.Y;
import static com.badlogic.gdx.Input.Keys.Z;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Json;
import com.distraction.gs20.Context;
import com.distraction.gs20.entities.ImageEntity;
import com.distraction.gs20.utils.Constants;

import java.util.HashMap;
import java.util.Map;

public class SubmitScreen extends GameScreen {

    private static final Map<Integer, String> INPUT_MAP = new HashMap<>() {{
        put(NUM_0, "0");
        put(NUM_1, "1");
        put(NUM_2, "2");
        put(NUM_3, "3");
        put(NUM_4, "4");
        put(NUM_5, "5");
        put(NUM_6, "6");
        put(NUM_7, "7");
        put(NUM_8, "8");
        put(NUM_9, "9");
        put(A, "a");
        put(B, "b");
        put(C, "c");
        put(D, "d");
        put(E, "e");
        put(F, "f");
        put(G, "g");
        put(H, "h");
        put(I, "i");
        put(J, "j");
        put(K, "k");
        put(L, "l");
        put(M, "m");
        put(N, "n");
        put(O, "o");
        put(P, "p");
        put(Q, "q");
        put(R, "r");
        put(S, "s");
        put(T, "t");
        put(U, "u");
        put(V, "v");
        put(W, "w");
        put(X, "x");
        put(Y, "y");
        put(Z, "z");
    }};

    private final TextureRegion pixel;

    private final PlayScreen.Difficulty difficulty;
    private final int score;

    private final BitmapFont font;

    private String name;

    private boolean success = false;
    private boolean failed = false;
    private boolean submitted = false;
    private boolean shift = false;
    private boolean loading = false;

    private float time;

    private final ImageEntity submitButton;
    private final ImageEntity backButton;

    public SubmitScreen(Context context, PlayScreen.Difficulty difficulty, int score) {
        super(context);
        this.difficulty = difficulty;
        this.score = score;

        pixel = context.getImage("pixel");

        font = context.getFont();

        name = "";

        submitButton = new ImageEntity(context.getImage("submit"));
        submitButton.p.set(Constants.WIDTH * 0.5f, Constants.HEIGHT * 0.35f);

        backButton = new ImageEntity(context.getImage("back"));
        backButton.p.set(Constants.HEIGHT * 0.155f, Constants.HEIGHT * 0.928f);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyUp(int keycode) {
                if (keycode == SHIFT_LEFT || keycode == SHIFT_RIGHT) {
                    shift = false;
                }
                return true;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == SHIFT_LEFT || keycode == SHIFT_RIGHT) {
                    shift = true;
                }

                if (ignoreInput) return true;
                String letter = INPUT_MAP.get(keycode);
                if (letter != null) {
                    failed = false;
                    if (name.length() < 4) {
                        if (shift) name += letter.toUpperCase();
                        else name += letter;
                    }
                }
                if (keycode == BACKSPACE) {
                    failed = false;
                    if (name.length() > 0) {
                        name = name.substring(0, name.length() - 1);
                    }
                }
                if (keycode == ESCAPE) {
                    ignoreInput = true;
                    context.gsm.push(new FadeTransitionScreen(context, new PlayScreen(context, difficulty)));
                }
                return true;
            }
        });
    }

    private void submit() {
        if (!valid()) return;
        ignoreInput = true;
        if (submitted) return;
        loading = true;
        context.submitScore(name, score, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String res = httpResponse.getResultAsString();
                // throwing an exception with SubmitScoreResponse here for some reason
                // just doing a sus true check instead
                if (res.contains("true")) {
                    submitted = true;
                    context.fetchLeaderboard(() -> success = true);
                } else {
                    failed(null);
                }
                loading = false;
            }

            @Override
            public void failed(Throwable t) {
                ignoreInput = false;
                failed = true;
                loading = false;
            }

            @Override
            public void cancelled() {
                failed(null);
            }
        });
    }

    private boolean valid() {
        return name.length() > 0;
    }

    private void back() {
        success = false;
        ignoreInput = true;
        context.gsm.push(new FadeTransitionScreen(context, new PlayScreen(context, difficulty)));
        context.audioHandler.playSound("success");
    }

    @Override
    protected void handleInput() {
        if (ignoreInput) return;

        if (Gdx.input.justTouched()) {
            unproject();
            if (submitButton.contains(m.x, m.y)) {
                context.audioHandler.playSound("click", 0.4f);
                submit();
            } else if (backButton.contains(m.x, m.y)) {
                context.audioHandler.playSound("click", 0.4f);
                back();
            }
        }
    }

    @Override
    public void update(float dt) {
        time += dt;
        handleInput();
        if (success) {
            back();
        }
    }

    @Override
    public void render(Batch b) {
        b.begin();
        {
            b.setProjectionMatrix(viewport.getCamera().combined);

            b.setColor(Constants.DARK);
            b.draw(pixel, 0, 0, Constants.WIDTH, Constants.HEIGHT);

            font.draw(b, "Enter Name: " + name, Constants.WIDTH * 0.27f, Constants.HEIGHT * 0.55f);
            b.setColor(1, 1, 1, 1);
            b.draw(pixel, Constants.WIDTH * 0.55f, Constants.HEIGHT * 0.49f, Constants.WIDTH * 0.17f, 1f);
            if (failed) {
                font.draw(b, "Error", Constants.WIDTH * 0.43f, Constants.HEIGHT * 0.24f);
            }

            b.setColor(1, 1, 1, 1);
            submitButton.render(b);
            backButton.render(b);

            if (loading) {
                for (int i = 0; i < 5; i++) {
                    float x = Constants.HEIGHT * 0.02f * MathUtils.cos(-6f * time + i * 0.1f);
                    float y = Constants.HEIGHT * 0.02f * MathUtils.sin(-6f * time + i * 0.1f);
                    b.draw(pixel, Constants.WIDTH * 0.6f + x, Constants.HEIGHT * 0.35f + y, 2, 2);
                }
            }
        }
        b.end();
    }

    private static class SubmitScoreResponse {
        Response response;

        static class Response {
            public boolean success;
        }
    }

}
