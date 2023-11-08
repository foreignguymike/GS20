package com.distraction.gs20.screens;


import static com.badlogic.gdx.Input.Keys.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Json;
import com.distraction.gs20.Context;
import com.distraction.gs20.entities.PopupImage;
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
        put(A, "A");
        put(B, "B");
        put(C, "C");
        put(D, "D");
        put(E, "E");
        put(F, "F");
        put(G, "G");
        put(H, "H");
        put(I, "I");
        put(J, "J");
        put(K, "K");
        put(L, "L");
        put(M, "M");
        put(N, "N");
        put(O, "O");
        put(P, "P");
        put(Q, "Q");
        put(R, "R");
        put(S, "S");
        put(T, "T");
        put(U, "U");
        put(V, "V");
        put(W, "W");
        put(X, "X");
        put(Y, "Y");
        put(Z, "Z");
    }};

    private final PlayScreen.Difficulty difficulty;
    private final int score;

    private final BitmapFont font;

    private String name;

    private boolean success = false;
    private boolean failed = false;

    public SubmitScreen(Context context, PlayScreen.Difficulty difficulty, int score) {
        super(context);
        this.difficulty = difficulty;
        this.score = score;

        font = context.getFont();

        name = "";

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (ignoreInput) return true;
                String letter = INPUT_MAP.get(keycode);
                if (letter != null) {
                    failed = false;
                    if (name.length() < 4) {
                        name += letter;
                    }
                }
                if (keycode == BACKSPACE) {
                    failed = false;
                    if (name.length() > 0) {
                        name = name.substring(0, name.length() - 1);
                    }
                }
                if (keycode == ENTER) {
                    ignoreInput = true;
                    context.submitScore(name, score, new Net.HttpResponseListener() {
                        @Override
                        public void handleHttpResponse(Net.HttpResponse httpResponse) {
                            String res = httpResponse.getResultAsString();
                            Json json = new Json();
                            json.setIgnoreUnknownFields(true);
                            SubmitScoreResponse response = json.fromJson(SubmitScoreResponse.class, res);
                            if (response.response.success) {
                                success = true;
                            } else {
                                failed(null);
                            }
                        }

                        @Override
                        public void failed(Throwable t) {
                            ignoreInput = false;
                            failed = true;
                        }

                        @Override
                        public void cancelled() {
                            failed(null);
                        }
                    });
                }
                if (keycode == ESCAPE) {
                    ignoreInput = true;
                    context.gsm.push(new FadeTransitionScreen(context, new PlayScreen(context, difficulty), 3));
                }
                return true;
            }
        });
    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {
        if (success) {
            success = false;
            ignoreInput = true;
            context.gsm.push(new FadeTransitionScreen(context, new PlayScreen(context, difficulty), 3));
        }
    }

    @Override
    public void render(Batch b) {
        b.begin();
        {
            b.setProjectionMatrix(viewport.getCamera().combined);

            b.setColor(Constants.DARK);
            b.draw(pixel, 0, 0, Constants.WIDTH, Constants.HEIGHT);

            font.draw(b, "Name can be 4 characters", Constants.WIDTH * 0.27f, Constants.HEIGHT * 0.65f);
            font.draw(b, "Enter Name:  " + name, Constants.WIDTH * 0.27f, Constants.HEIGHT * 0.55f);
            if (failed) {
                font.draw(b, "Error submitting score", Constants.WIDTH * 0.27f, Constants.HEIGHT * 0.4f);
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
