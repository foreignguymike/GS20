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

import de.golfgl.gdxgamesvcs.leaderboard.ILeaderBoardEntry;

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
    private final int lowestScore;

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
            Constants.HEIGHT * 0.2f,
            Constants.HEIGHT * 0.35f,
            Constants.HEIGHT * 0.5f,
            Constants.HEIGHT * 0.65f
        };
        finalScore = calculateFinalScore();
        if (context.entries.isEmpty()) {
            lowestScore = 0;
        } else {
            ILeaderBoardEntry entry = context.entries.get(context.entries.size() - 1);
            lowestScore = Integer.parseInt(entry.getFormattedValue());
        }
    }

    private int calculateFinalScore() {
        int score = 0;
        for (Gem gem : finishData.gems) {
            score += gem.getPoints();
        }
        score -= finishData.miss * Gem.MISS;
        return score;
    }

    private boolean canSubmit() {
        return finalScore > 0 && (context.entries.size() < 10 || finalScore > lowestScore);
    }

    @Override
    protected void handleInput() {
        if (ignoreInput) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            ignoreInput = true;
            context.gsm.push(new FadeTransitionScreen(context, new PlayScreen(context, finishData.difficulty), 2));
        }

        if (stage == Stage.WAIT) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                if (canSubmit()) {
                    ignoreInput = true;
                    context.gsm.push(new SubmitScreen(context, finishData.difficulty, finalScore));
                    context.gsm.depth++;
                }
            }
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
                    b.draw(gemImages[i], textPositions[i] - gemImages[i].getRegionWidth() * 0.2f, Constants.HEIGHT * 0.74f);
                } else {
                    font.draw(b, "MISS", textPositions[i], Constants.HEIGHT * 0.82f);
                }
                font.draw(b, "x " + (int) counts[i], textPositions[i], Constants.HEIGHT * 0.70f);
            }
            font.draw(b, "SCORE", Constants.HEIGHT * 0.3f, Constants.HEIGHT * 0.55f);
            font.draw(b, (int) score + "", Constants.HEIGHT * 0.55f, Constants.HEIGHT * 0.55f);

            if (stage == Stage.WAIT) {
                font.draw(b, "Press R to restart", Constants.HEIGHT * 0.21f, Constants.HEIGHT * 0.42f);
                if (canSubmit()) {
                    font.draw(b, "Press Enter to", Constants.HEIGHT * 0.26f, Constants.HEIGHT * 0.3f);
                    font.draw(b, "submit score", Constants.HEIGHT * 0.28f, Constants.HEIGHT * 0.22f);
                }
            }
        }
        b.end();
    }

}
