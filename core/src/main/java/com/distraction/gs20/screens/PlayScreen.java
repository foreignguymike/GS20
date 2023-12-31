package com.distraction.gs20.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.distraction.gs20.Context;
import com.distraction.gs20.entities.ColorEntity;
import com.distraction.gs20.entities.FontEntity;
import com.distraction.gs20.entities.Gem;
import com.distraction.gs20.entities.ImageEntity;
import com.distraction.gs20.entities.Pad;
import com.distraction.gs20.entities.PopupImage;
import com.distraction.gs20.entities.Tile;
import com.distraction.gs20.utils.Constants;
import com.distraction.gs20.utils.GemSpawner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.golfgl.gdxgamesvcs.leaderboard.ILeaderBoardEntry;

public class PlayScreen extends GameScreen implements Gem.GemListener {

    private enum Stage {
        START,
        COUNTDOWN,
        PLAYING,
        FINISH,
        LAST
    }

    public enum Difficulty {
        EASY(0),
        NORMAL(100),
        CHALLENGE(200);

        public final int points;

        Difficulty(int points) {
            this.points = points;
        }
    }

    private final TextureRegion bg;
    private final TextureRegion lbg;

    private final Map<Integer, ColorEntity.Type> keyPadMap;

    private final Difficulty difficulty;
    private Stage stage = Stage.START;

    private final GemSpawner gemSpawner;
    private final Random random;

    private final Pad[] pads;
    private final Tile[][] tiles;
    private final List<Tile> availableTiles;
    private final List<Gem> gems;

    private float time;
    private Tile currentTile;

    private final FontEntity font1;
    private final FontEntity[][] scoreFonts;
    private final FontEntity startFont;

    private final PopupImage[] countdownImages;
    private final List<PopupImage> pops;

    private final FinishScreen.PlayData playData;

    private final ImageEntity restartButton;

    public PlayScreen(Context context, Difficulty difficulty) {
        super(context);
        this.difficulty = difficulty;

        bg = context.getImage("bg");
        lbg = context.getImage("leaderboardbg");

        gemSpawner = new GemSpawner(context, difficulty, this);
        random = new Random();

        List<ColorEntity.Type> types = new ArrayList<>();
        types.add(ColorEntity.Type.GREEN);
        types.add(ColorEntity.Type.YELLOW);
        if (difficulty == Difficulty.NORMAL) {
            types.add(ColorEntity.Type.RED);
        }
        if (difficulty == Difficulty.CHALLENGE) {
            types.add(ColorEntity.Type.RED);
            types.add(ColorEntity.Type.BLUE);
        }

        Map<ColorEntity.Type, Vector2> padPositions = new HashMap<>() {{
            put(ColorEntity.Type.RED, new Vector2(Constants.HEIGHT * 0.5f, Constants.HEIGHT * 0.9296f));
            put(ColorEntity.Type.GREEN, new Vector2(Constants.HEIGHT * 0.9296f, Constants.HEIGHT * 0.5f));
            put(ColorEntity.Type.BLUE, new Vector2(Constants.HEIGHT * 0.5f, Constants.HEIGHT * 0.0703f));
            put(ColorEntity.Type.YELLOW, new Vector2(Constants.HEIGHT * 0.0703f, Constants.HEIGHT * 0.5f));
        }};
        pads = new Pad[types.size()];
        for (int i = 0; i < types.size(); i++) {
            ColorEntity.Type type = types.get(i);
            pads[i] = new Pad(context, type);
            pads[i].p.set(padPositions.get(type));
        }

        keyPadMap = new HashMap<>();
        keyPadMap.put(Input.Keys.D, ColorEntity.Type.GREEN);
        keyPadMap.put(Input.Keys.RIGHT, ColorEntity.Type.GREEN);
        keyPadMap.put(Input.Keys.A, ColorEntity.Type.YELLOW);
        keyPadMap.put(Input.Keys.LEFT, ColorEntity.Type.YELLOW);
        if (difficulty == Difficulty.NORMAL) {
            keyPadMap.put(Input.Keys.W, ColorEntity.Type.RED);
            keyPadMap.put(Input.Keys.UP, ColorEntity.Type.RED);
        }
        if (difficulty == Difficulty.CHALLENGE) {
            keyPadMap.put(Input.Keys.W, ColorEntity.Type.RED);
            keyPadMap.put(Input.Keys.UP, ColorEntity.Type.RED);
            keyPadMap.put(Input.Keys.S, ColorEntity.Type.BLUE);
            keyPadMap.put(Input.Keys.DOWN, ColorEntity.Type.BLUE);
        }

        availableTiles = new ArrayList<>();
        tiles = new Tile[Constants.TILEMAP_SIZE][Constants.TILEMAP_SIZE];
        for (int row = 0; row < tiles.length; row++) {
            for (int col = 0; col < tiles[row].length; col++) {
                Tile tile = new Tile(context);
                float x = Constants.HEIGHT * (0.5f + (row - 2) * Constants.TILE_SIZE);
                float y = Constants.HEIGHT * (0.5f + (col - 2) * Constants.TILE_SIZE);
                tile.p.set(x, y);
                tiles[row][col] = tile;
                availableTiles.add(tile);
            }
        }

        gems = new ArrayList<>();

        Gem[] startingGems = gemSpawner.init();
        for (Gem gem : startingGems) placeGem(gem);

        BitmapFont impactFont = context.getFont(Context.FONT_NAME_VCR20);
        font1 = new FontEntity(impactFont);
        font1.setText("LEADERBOARD");
        font1.p.set(Constants.WIDTH - (Constants.WIDTH - Constants.HEIGHT) / 2f, Constants.HEIGHT * 0.864f);

        scoreFonts = new FontEntity[10][3];
        for (int row = 0; row < scoreFonts.length; row++) {
            scoreFonts[row][0] = new FontEntity(impactFont);
            scoreFonts[row][1] = new FontEntity(impactFont);
            scoreFonts[row][2] = new FontEntity(impactFont);

            scoreFonts[row][0].p.set(Constants.WIDTH * 0.62f, Constants.HEIGHT * 0.748f - row * Constants.HEIGHT * 0.074f);
            scoreFonts[row][1].p.set(Constants.WIDTH * 0.7f, Constants.HEIGHT * 0.748f - row * Constants.HEIGHT * 0.074f);
            scoreFonts[row][2].p.set(Constants.WIDTH * 0.82f, Constants.HEIGHT * 0.748f - row * Constants.HEIGHT * 0.074f);

            scoreFonts[row][0].setText((row + 1) + "");
            scoreFonts[row][1].setText("-");
            scoreFonts[row][2].setText("-");
        }

        startFont = new FontEntity(impactFont);
        startFont.setText("Tap to Start");
        startFont.p.set(Constants.HEIGHT * 0.5f, Constants.HEIGHT * 0.50f);

        countdownImages = new PopupImage[]{
            new PopupImage(context.getImage("3")),
            new PopupImage(context.getImage("2")),
            new PopupImage(context.getImage("1")),
            new PopupImage(context.getImage("go")),
            new PopupImage(context.getImage("time"))
        };
        for (PopupImage image : countdownImages) {
            image.p.set(Constants.HEIGHT * 0.5f, Constants.HEIGHT * 0.5f);
        }

        playData = new FinishScreen.PlayData(difficulty);

        pops = new ArrayList<>();

        restartButton = new ImageEntity(context.getImage("restart"));
        restartButton.p.set(Constants.HEIGHT * 0.155f, Constants.HEIGHT * 0.928f);

        requestLeaderboards();
    }

    private void requestLeaderboards() {
        if (!context.leaderboardsInitialized) {
            context.leaderboardsInitialized = true;
            context.fetchLeaderboard(() -> {
                updateLeaderboards();
            });
        }
        updateLeaderboards();
    }

    private void updateLeaderboards() {
        for (int i = 0; i < 10; i++) {
            if (i < context.entries.size()) {
                ILeaderBoardEntry entry = context.entries.get(i);
                scoreFonts[i][1].setText(entry.getUserDisplayName());
                scoreFonts[i][2].setText(entry.getFormattedValue());
            } else {
                scoreFonts[i][1].setText("-");
                scoreFonts[i][2].setText("-");
            }
        }
    }

    private void placeGem(Gem gem) {
        if (availableTiles.isEmpty()) return;
        int index = random.nextInt(availableTiles.size());
        Tile tile = availableTiles.remove(index);
        tile.setGem(gem);
        gems.add(gem);

        // auto play
//        Pad pad = getPad(gem.type);
//        if (pad != null) {
//            gem.collect(pad);
//            availableTiles.add(gem.tile);
//            gem.tile.flash(pad.type.color);
//        }
    }

    private void updateCurrentTile() {
        for (Tile[] tile : tiles) {
            for (Tile value : tile) {
                if (value.contains(m.x, m.y)) {
                    if (currentTile != value && currentTile != null) {
                        currentTile.highlight = false;
                    }
                    currentTile = value;
                    value.highlight = true;
                    return;
                }
            }
        }
        if (currentTile != null) currentTile.highlight = false;
        currentTile = null;
    }

    private Pad getPad(ColorEntity.Type type) {
        for (Pad pad : pads) {
            if (pad.type == type) return pad;
        }
        return null;
    }

    private void createPop(Pad pad) {
        PopupImage pop = new PopupImage(pad.image);
        pop.p.set(pad.p);
        pop.setup((t) -> 1f, (t) -> 1 + 0.3f * MathUtils.sin(t * 3.1415f * 4), 0.25f);
        pop.start();
        pops.add(pop);
    }

    @Override
    public void onScored(Gem gem) {
        if (gem.pad.type == gem.type) {
            playData.addGem(gem);
            createPop(gem.pad);
            playData.currentCombo++;
            context.audioHandler.playSound("hit", 0.2f);
        } else {
            playData.miss++;
            if (playData.currentCombo > playData.bestCombo) {
                playData.bestCombo = playData.currentCombo;
                playData.currentCombo = 0;
            }
            context.audioHandler.playSound("miss");
        }
    }

    @Override
    protected void handleInput() {
        unproject();
        updateCurrentTile();

        if (ignoreInput) return;

        if (Gdx.input.justTouched()) {
            if (restartButton.contains(m.x, m.y)) {
                ignoreInput = true;
                TransitionScreen screen = new FadeTransitionScreen(context, new PlayScreen(context, difficulty));
                screen.duration = 1f;
                context.gsm.push(screen);
                return;
            }
        }
        if (stage == Stage.START) {
            if (Gdx.input.justTouched()) {
                if (m.x < Constants.HEIGHT && m.y < Constants.HEIGHT) {
                    stage = Stage.COUNTDOWN;
                }
            }
        } else if (stage == Stage.PLAYING) {
            keyPadMap.forEach((k, v) -> {
                if (Gdx.input.isKeyJustPressed(k)) {
                    if (currentTile != null) {
                        Gem gem = currentTile.takeGem();
                        if (gem != null) {
                            Pad pad = getPad(v);
                            if (pad != null) {
                                gem.collect(pad);
                                availableTiles.add(gem.tile);
                                gem.tile.flash(pad.type.color);
                            }
                        }
                    }
                }
            });
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                time = 0f;
                stage = Stage.FINISH;
                countdownImages[4].start();
            }
        }
    }

    @Override
    public void update(float dt) {
        handleInput();

        for (Gem gem : gems) gem.update(dt);
        gems.removeIf(it -> it.remove);
        for (PopupImage pop : pops) pop.update(dt);
        pops.removeIf(it -> it.remove);
        for (Tile[] tile : tiles) {
            for (Tile value : tile) {
                value.update(dt);
            }
        }

        if (stage == Stage.COUNTDOWN) {
            if (time > 3.75f) countdownImages[3].start();
            else if (time > 2.75f) countdownImages[2].start();
            else if (time > 1.75f) countdownImages[1].start();
            else if (time > 0.75f) countdownImages[0].start();
            if (time < 1f && time + dt > 1f ||
                time < 2f && time + dt > 2f ||
                time < 3f && time + dt > 3f) {
                context.audioHandler.playSound("cdlow", 0.7f);
            } else if (time < 4f && time + dt > 4f) {
                context.audioHandler.playSound("cdhigh", 0.7f);
            }
            time += dt;
            if (time >= 4f) {
                time = 0f;
                stage = Stage.PLAYING;
            }
            for (PopupImage image : countdownImages) image.update(dt);
        } else if (stage == Stage.PLAYING) {
            time += dt;
            while (true) {
                Gem gem = gemSpawner.take(dt);
                if (gem != null) {
                    placeGem(gem);
                } else {
                    break;
                }
            }
            for (PopupImage image : countdownImages) image.update(dt);
            if (time > 20) {
                time = 0f;
                stage = Stage.FINISH;
                countdownImages[4].start();
            }
        } else if (stage == Stage.FINISH) {
            time += dt;
            for (PopupImage image : countdownImages) image.update(dt);
            if (time >= 1f) {
                ignoreInput = true;
                if (playData.currentCombo > playData.bestCombo) {
                    playData.bestCombo = playData.currentCombo;
                }
                context.gsm.push(new FinishScreen(context, playData));
                context.gsm.depth++;
                stage = Stage.LAST;
            }
        }
    }

    @Override
    public void render(Batch b) {
        b.setProjectionMatrix(viewport.getCamera().combined);
        b.begin();
        {
            b.setColor(Constants.DARK);
            b.draw(pixel, 0, 0, Constants.WIDTH, Constants.HEIGHT);

            b.setColor(1, 1, 1, 1);
            b.draw(lbg, Constants.WIDTH - lbg.getRegionWidth(), 0);

            b.setColor(1, 1, 1, 1);
            b.draw(bg, 0, 0);

            b.setColor(1, 1, 1, 1);
            for (Tile[] tile : tiles) {
                for (Tile value : tile) {
                    value.render(b);
                }
            }
            for (Pad pad : pads) pad.render(b);
            for (Gem gem : gems) gem.render(b);
            for (PopupImage pop : pops) pop.render(b);

            b.setColor(1, 1, 1, 1);
            restartButton.render(b);

            for (PopupImage image : countdownImages) image.render(b);

            if (stage == Stage.START) {
                b.setColor(0, 0, 0, 0.6f);
                b.draw(pixel, 0, 0, Constants.HEIGHT, Constants.HEIGHT);
                startFont.render(b);
            }

            font1.render(b);
            for (FontEntity[] scoreFont : scoreFonts) {
                for (int i = 0; i < scoreFonts[0].length; i++) {
                    scoreFont[i].renderLeft(b);
                }
            }
        }
        b.end();
    }

}
