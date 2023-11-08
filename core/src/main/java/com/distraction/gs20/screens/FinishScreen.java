package com.distraction.gs20.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.distraction.gs20.Context;
import com.distraction.gs20.entities.ColorEntity;
import com.distraction.gs20.entities.Gem;
import com.distraction.gs20.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FinishScreen extends GameScreen {

    private enum Stage {
        INTRO,
        TALLY,
        COUNT_SCORE,
        WAIT
    }

    public static class FinishData {

        public final PlayScreen.Difficulty difficulty;
        public final List<Gem> gems;
        private final int[] counts;
        public int miss = 0;

        public boolean flawless = true;

        public FinishData(PlayScreen.Difficulty difficulty) {
            this.difficulty = difficulty;
            gems = new ArrayList<>();
            counts = new int[ColorEntity.Type.values().length];
            Arrays.fill(counts, 0);
        }

        public void addGem(Gem gem) {
            counts[gem.size.ordinal()]++;
            gems.add(gem);
        }
    }

    private Stage stage = Stage.INTRO;

    private final FinishData finishData;

    private final TextureRegion bg;

    private final BitmapFont font;

    private float time;
    private float fade;
    private float camY;

    private final float[] textPositions;
    private final TextureRegion[] gemImages;
    private final float[] counts;
    private final int[] finalCounts;

    private float score;
    private final int finalScore;

    public FinishScreen(Context context, FinishData finishData) {
        super(context);
        this.finishData = finishData;
        bg = context.getImage("finishbg");
        font = context.getFont();

        camY = -Constants.HEIGHT;
        viewport.setY(camY);

        gemImages = new TextureRegion[]{
            context.getImage("gems3"),
            context.getImage("gems2"),
            context.getImage("gems1")
        };
        finalCounts = new int[]{
            finishData.counts[Gem.Size.LARGE.ordinal()],
            finishData.counts[Gem.Size.MEDIUM.ordinal()],
            finishData.counts[Gem.Size.SMALL.ordinal()],
            finishData.miss
        };
        counts = new float[finalCounts.length];
        textPositions = new float[]{
            Constants.HEIGHT * 0.8f,
            Constants.HEIGHT * 0.7f,
            Constants.HEIGHT * 0.6f,
            Constants.HEIGHT * 0.5f,
            Constants.HEIGHT * 0.3f
        };
        finalScore = calculateFinalScore();
    }

    private int calculateFinalScore() {
        System.out.println("calculating final score");
        int score = 0;
        for (Gem gem : finishData.gems) {
            score += gem.getPoints();
        }
        System.out.println("add points: " + score);
        System.out.println("subtract points: " + (finishData.miss * Gem.MISS));
        score -= finishData.miss * Gem.MISS;
        System.out.println("FINAL: " + score);
        return score;
    }

    @Override
    protected void handleInput() {
        if (ignoreInput) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            ignoreInput = true;
            context.gsm.push(new FadeTransitionScreen(context, new PlayScreen(context, finishData.difficulty), 2));
        }
    }

    @Override
    public void update(float dt) {
        handleInput();

        if (stage == Stage.INTRO) {
            fade += 2 * dt;
            if (fade > 0.7f) fade = 0.7f;

            camY += Constants.HEIGHT * 4 * dt;
            if (camY >= Constants.HEIGHT / 2f) {
                camY = Constants.HEIGHT / 2f;
                stage = Stage.TALLY;
            }
            viewport.setY(camY);
        } else if (stage == Stage.TALLY) {
            time += dt;
            if (time > 0.75f) {
                for (int i = 0; i < counts.length; i++) {
                    counts[i] += 25f * dt;
                    if (counts[i] >= finalCounts[i]) {
                        counts[i] = finalCounts[i];
                    }
                }
                boolean done = true;
                for (int i = 0; i < counts.length; i++) {
                    if (counts[i] != finalCounts[i]) {
                        done = false;
                        break;
                    }
                }
                if (done) {
                    stage = Stage.COUNT_SCORE;
                    time = 0;
                }
            }
        } else if (stage == Stage.COUNT_SCORE) {
            time += dt;
            if (time >= 0.75f) {
                score += 10000 * dt;
                if (score >= finalScore) {
                    score = finalScore;
                    stage = Stage.WAIT;
                }
            }
        }
    }

    @Override
    public void render(Batch b) {
        b.begin();
        {
            b.setProjectionMatrix(uiViewport.getCamera().combined);
            b.setColor(0, 0, 0, fade);
            b.draw(pixel, 0, 0, Constants.HEIGHT, Constants.HEIGHT);

            b.setProjectionMatrix(viewport.getCamera().combined);
            b.setColor(1, 1, 1, 1);
            b.draw(bg, (Constants.HEIGHT - bg.getRegionWidth()) / 2f, (Constants.HEIGHT - bg.getRegionHeight()) / 2f);
            b.setColor(1, 1, 1, 1);
            font.setColor(1, 1, 1, 1);
            for (int i = 0; i < counts.length; i++) {
                if (gemImages.length > i) {
                    b.draw(gemImages[i], Constants.HEIGHT * 0.32f, textPositions[i] - gemImages[i].getRegionHeight() * 0.8f);
                } else {
                    font.draw(b, "MISS", Constants.HEIGHT * 0.3f, textPositions[i]);
                }
                font.draw(b, "x " + (int) counts[i], Constants.HEIGHT * 0.55f, textPositions[i]);
            }
            font.draw(b, "SCORE", Constants.HEIGHT * 0.3f, textPositions[4]);
            font.draw(b, (int) score + "", Constants.HEIGHT * 0.55f, textPositions[4]);
        }
        b.end();
    }

}
