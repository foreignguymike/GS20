package com.distraction.gs20;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.distraction.gs20.screens.GameScreen;

import java.util.Stack;

/**
 * Manages all game screens. Screens will stay in the screen stack.
 */
public class GameScreenManager extends Stack<GameScreen> {

    public int depth = 1;

    public void replace(GameScreen s) {
        pop();
        push(s);
    }

    public void update(float dt) {
        for (int i = size() - depth; i < size(); i++) {
            get(i).update(dt);
        }
    }

    public void render(Batch b) {
        for (int i = size() - depth; i < size(); i++) {
            get(i).render(b);
        }
    }

    public void resize(int w, int h) {
        for (int i = size() - depth; i < size(); i++) {
            get(i).resize(w, h);
        }
    }

}
