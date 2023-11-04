package com.distraction.gs20;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.distraction.gs20.screens.GameScreen;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Manages all game screens. Screens will stay in the screen stack.
 */
public class GameScreenManager {

    private final Deque<GameScreen> screens;

    public GameScreenManager() {
        screens = new ArrayDeque<>();
    }

    public void push(GameScreen screen) {
        GameScreen currentScreen = screens.peek();
        if (currentScreen != null) currentScreen.onPause();

        screens.push(screen);
        screen.onResume();
    }

    public void pop() {
        screens.pop();
    }

    public void update(float dt) {
        screens.forEach(it -> it.update(dt));
    }

    public void render(Batch b) {
        screens.forEach(it -> it.render(b));
    }

    public void resize(int width, int height) {
        screens.forEach(it -> it.resize(width, height));
    }

}
