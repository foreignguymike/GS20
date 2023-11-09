package com.distraction.gs20.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.distraction.gs20.Context;
import com.distraction.gs20.entities.ColorEntity;
import com.distraction.gs20.entities.Gem;
import com.distraction.gs20.entities.ImageEntity;
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

    public static class PlayData {

        public final PlayScreen.Difficulty difficulty;
        public final List<Gem> gems;
        private final int[] counts;
        public int miss = 0;
        public int bestCombo;
        public int currentCombo;

        public PlayData(PlayScreen.Difficulty difficulty) {
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

    private final PlayData playData;

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

    private final ImageEntity restartButton;
    private final ImageEntity submitButton;

    private float tickTime = 1;

    public FinishScreen(Context context, PlayData playData) {
        super(context);
        this.playData = playData;
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
            playData.counts[Gem.Size.LARGE.ordinal()],
            playData.counts[Gem.Size.MEDIUM.ordinal()],
            playData.counts[Gem.Size.SMALL.ordinal()],
            playData.miss,
            playData.bestCombo
        };
        counts = new float[finalCounts.length];
        textPositions = new float[]{
            Constants.HEIGHT * 0.76f,
            Constants.HEIGHT * 0.64f,
            Constants.HEIGHT * 0.52f
        };
        finalScore = calculateFinalScore();
        if (context.entries.isEmpty()) {
            lowestScore = 0;
        } else {
            ILeaderBoardEntry entry = context.entries.get(context.entries.size() - 1);
            lowestScore = Integer.parseInt(entry.getFormattedValue());
        }

        restartButton = new ImageEntity(context.getImage("restart"));
        submitButton = new ImageEntity(context.getImage("submit"));

        restartButton.p.set(Constants.HEIGHT * 0.336f, Constants.HEIGHT * 0.265f);
        submitButton.p.set(Constants.HEIGHT * 0.664f, Constants.HEIGHT * 0.265f);
    }

    private int calculateFinalScore() {
        int score = 0;
        for (Gem gem : playData.gems) {
            score += gem.getPoints();
        }
        score -= playData.miss * Gem.MISS;
        int combo = playData.bestCombo * 100;
        if (combo > 4000) combo = 4000;
        score += combo;
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
            context.gsm.push(new FadeTransitionScreen(context, new PlayScreen(context, playData.difficulty), 2));
        }

        if (stage == Stage.WAIT) {
            if (Gdx.input.justTouched()) {
                unproject();
                if (restartButton.contains(m.x, m.y)) {
                    context.audioHandler.playSound("click", 0.4f);
                    ignoreInput = true;
                    context.gsm.push(new FadeTransitionScreen(context, new PlayScreen(context, playData.difficulty), 2));
                } else if (submitButton.contains(m.x, m.y)) {
                    if (canSubmit()) {
                        context.audioHandler.playSound("click", 0.4f);
                        ignoreInput = true;
                        TransitionScreen screen = new FadeTransitionScreen(context, new SubmitScreen(context, playData.difficulty, finalScore), 2);
                        screen.duration = 1f;
                        context.gsm.push(screen);
                    }
                }
            }
        } else if (stage == Stage.TALLY || stage == Stage.COUNT_SCORE) {
            if (Gdx.input.justTouched()) {
                for (int i = 0; i < finalCounts.length; i++) {
                    counts[i] = finalCounts[i];
                }
                score = finalScore;
                stage = Stage.WAIT;
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
                    counts[i] += 50f * dt;
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
                    tickTime = 1f;
                } else {
                    tickTime += dt;
                    if (tickTime > 0.1f) {
                        tickTime = 0;
                        context.audioHandler.playSound("tick", 0.2f);
                    }
                }
            }
        } else if (stage == Stage.COUNT_SCORE) {
            time += dt;
            if (time >= 0.75f) {
                score += 10000 * dt;
                if (score >= finalScore) {
                    score = finalScore;
                    stage = Stage.WAIT;
                } else {
                    tickTime += dt;
                    if (tickTime > 0.1f) {
                        tickTime = 0;
                        context.audioHandler.playSound("tick", 0.2f);
                    }
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
            //noinspection SuspiciousNameCombination
            b.draw(pixel, 0, 0, Constants.HEIGHT, Constants.HEIGHT);

            b.setProjectionMatrix(viewport.getCamera().combined);
            b.setColor(1, 1, 1, 1);
            b.draw(bg, (Constants.HEIGHT - bg.getRegionWidth()) / 2f, (Constants.HEIGHT - bg.getRegionHeight()) / 2f);
            b.setColor(1, 1, 1, 1);
            font.setColor(1, 1, 1, 1);
            for (int i = 0; i < gemImages.length; i++) {
                b.draw(gemImages[i], Constants.HEIGHT * 0.246f - gemImages[i].getRegionWidth() * 0.5f, textPositions[i] - gemImages[i].getRegionHeight() * 0.5f);
                font.draw(b, "x" + (int) counts[i], Constants.HEIGHT * 0.38f, textPositions[i] + Constants.HEIGHT * 0.02f);
            }
            font.draw(b, "Miss", Constants.HEIGHT * 0.59f, Constants.HEIGHT * 0.78f);
            font.draw(b, "x" + (int) counts[3], Constants.HEIGHT * 0.59f, Constants.HEIGHT * 0.71f);
            font.draw(b, "Combo", Constants.HEIGHT * 0.59f, Constants.HEIGHT * 0.61f);
            font.draw(b, "x" + (int) counts[4], Constants.HEIGHT * 0.59f, Constants.HEIGHT * 0.54f);

            font.draw(b, "Score", Constants.HEIGHT * 0.26f, Constants.HEIGHT * 0.43f);
            font.draw(b, (int) score + "", Constants.HEIGHT * 0.55f, Constants.HEIGHT * 0.43f);

            if (stage == Stage.WAIT) {
                if (canSubmit()) {
                    restartButton.p.set(Constants.HEIGHT * 0.336f, Constants.HEIGHT * 0.265f);
                    submitButton.p.set(Constants.HEIGHT * 0.664f, Constants.HEIGHT * 0.265f);
                    restartButton.render(b);
                    submitButton.render(b);
                } else {
                    restartButton.p.set(Constants.HEIGHT * 0.5f, Constants.HEIGHT * 0.265f);
                    restartButton.render(b);
                }
            }
        }
        b.end();
    }

}
