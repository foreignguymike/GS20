package com.distraction.gs20.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.distraction.gs20.Context;
import com.distraction.gs20.entities.ColorEntity;
import com.distraction.gs20.entities.Gem;
import com.distraction.gs20.entities.Pad;
import com.distraction.gs20.entities.Tile;
import com.distraction.gs20.utils.Constants;
import com.distraction.gs20.utils.GemSpawner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PlayScreen extends GameScreen implements Gem.GemListener {

    private enum Stage {
        COUNTDOWN,
        PLAYING,
        FINISH
    }

    public enum Difficulty {
        EASY(0),
        NORMAL(50),
        CHALLENGE(100);

        public final int points;

        Difficulty(int points) {
            this.points = points;
        }
    }

    private final Map<ColorEntity.Type, Vector2> PAD_POSITIONS = new HashMap<>() {{
        put(ColorEntity.Type.RED, new Vector2(Constants.WIDTH * 0.5f, Constants.HEIGHT * 0.9296f));
        put(ColorEntity.Type.GREEN, new Vector2(Constants.WIDTH * 0.9296f, Constants.HEIGHT * 0.5f));
        put(ColorEntity.Type.BLUE, new Vector2(Constants.WIDTH * 0.5f, Constants.HEIGHT * 0.0703f));
        put(ColorEntity.Type.YELLOW, new Vector2(Constants.WIDTH * 0.0703f, Constants.HEIGHT * 0.5f));
    }};

    private final Map<Integer, ColorEntity.Type> keyPadMap;

    private final Difficulty difficulty;
    private Stage stage = Stage.COUNTDOWN;

    private final GemSpawner gemSpawner;
    private final Random random;

    private final Pad[] pads;
    private final Tile[][] tiles;
    private final List<Tile> availableTiles;
    private final List<Gem> gems;

    private float time;
    private Tile currentTile;

    private int score;
    private BitmapFont font;

    public PlayScreen(Context context, Difficulty difficulty) {
        super(context);

        this.difficulty = difficulty;

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
        pads = new Pad[types.size()];
        for (int i = 0; i < types.size(); i++) {
            ColorEntity.Type type = types.get(i);
            pads[i] = new Pad(context, type);
            pads[i].p.set(PAD_POSITIONS.get(type));
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
                float x = Constants.WIDTH * (0.5f + (row - 2) * Constants.TILE_SIZE);
                float y = Constants.HEIGHT * (0.5f + (col - 2) * Constants.TILE_SIZE);
                tile.p.set(x, y);
                tiles[row][col] = tile;
                availableTiles.add(tile);
            }
        }

        gems = new ArrayList<>();

        Gem[] startingGems = gemSpawner.init();
        for (Gem gem : startingGems) placeGem(gem);

        font = new BitmapFont();
    }

    private void placeGem(Gem gem) {
        if (availableTiles.isEmpty()) return;
        int index = random.nextInt(availableTiles.size());
        Tile tile = availableTiles.remove(index);
        tile.setGem(gem);
        gems.add(gem);
    }

    private void updateCurrentTile() {
        for (int row = 0; row < tiles.length; row++) {
            for (int col = 0; col < tiles[row].length; col++) {
                if (tiles[row][col].contains(m.x, m.y)) {
                    Tile newTile = tiles[row][col];
                    if (currentTile != newTile && currentTile != null) {
                        currentTile.highlight = false;
                    }
                    currentTile = newTile;
                    newTile.highlight = true;
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

    @Override
    public void onScored(int points) {
        score += points;
    }

    @Override
    protected void handleInput() {
        unproject();
        updateCurrentTile();

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            TransitionScreen screen = new FadeTransitionScreen(context, new PlayScreen(context, difficulty));
            screen.duration = 1f;
            context.gsm.push(screen);
        }

        if (stage == Stage.PLAYING) {
            keyPadMap.forEach((k, v) -> {
                if (Gdx.input.isKeyJustPressed(k)) {
                    if (currentTile != null) {
                        Gem gem = currentTile.takeGem();
                        System.out.println("takegem: " + gem);
                        if (gem != null) {
                            Pad pad = getPad(v);
                            if (pad != null) {
                                System.out.println("taking gem to pad: " + pad.type);
                                System.out.println("gem is at " + gem.p + ", pad is at: " + pad.p);
                                gem.collect(pad.p);
                                System.out.println("gem d is at " + gem.d);
                                availableTiles.add(gem.tile);
                            }
                        }
                    }
                }
            });
        } else if (stage == Stage.FINISH) {

        }
    }

    @Override
    public void update(float dt) {
        handleInput();

        time += dt;

        for (Gem gem : gems) {
            gem.update(dt);
        }
        gems.removeIf(it -> it.remove);

        if (stage == Stage.COUNTDOWN) {
            if (time >= 3f) {
                time = 0f;
                stage = Stage.PLAYING;
            }
        } else if (stage == Stage.PLAYING) {
            while (true) {
                Gem gem = gemSpawner.take(dt);
                if (gem != null) {
                    placeGem(gem);
                } else {
                    break;
                }
            }
            if (time >= 20) {
                stage = Stage.FINISH;
            }
        }
    }

    @Override
    public void render(Batch b) {
        b.setProjectionMatrix(viewport.getCamera().combined);
        b.begin();
        {
            b.setColor(Constants.BG_COLOR);
            b.draw(pixel, 0, 0, Constants.WIDTH, Constants.HEIGHT);

            b.setColor(1, 1, 1, 1);
            for (Pad pad : pads) {
                pad.render(b);
            }
            for (int row = 0; row < tiles.length; row++) {
                for (int col = 0; col < tiles[row].length; col++) {
                    tiles[row][col].render(b);
                }
            }
            for (Gem gem : gems) {
                gem.render(b);
            }

            b.setColor(1, 1, 1, 1);
            font.draw(b, score + "", Constants.WIDTH * 0.05f, Constants.HEIGHT * 0.95f);

            if (stage == Stage.PLAYING) {
                font.draw(b, (20 - time) + "", Constants.WIDTH * 0.05f, Constants.HEIGHT * 0.05f);
            }
        }
        b.end();
    }

}
