package com.distraction.gs20.screens;

import com.distraction.gs20.Context;

public abstract class TransitionScreen extends GameScreen {

    protected final GameScreen nextScreen;

    protected int pop = 1;
    protected float duration = 0.5f;
    protected float time = 0f;
    protected boolean next = false;

    public TransitionScreen(Context context, GameScreen nextScreen) {
        super(context);
        this.nextScreen = nextScreen;

        context.gsm.depth++;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public void setPop(int pop) {
        this.pop = pop;
    }

    @Override
    protected void handleInput() {

    }

}
