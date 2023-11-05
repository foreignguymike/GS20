package com.distraction.gs20.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.distraction.gs20.Context;
import com.distraction.gs20.entities.ColorEntity;
import com.distraction.gs20.entities.Gem;
import com.distraction.gs20.entities.Hand;
import com.distraction.gs20.entities.Pad;
import com.distraction.gs20.entities.Tile;
import com.distraction.gs20.utils.Constants;
import com.distraction.gs20.utils.GemSpawner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PlayScreen extends GameScreen {

    private enum Stage {
        COUNTDOWN,
        PLAYING
    }

    public enum Difficulty {
        EASY,
        NORMAL,
        WEIRD
    }

    private final Map<ColorEntity.Type, Vector2> PAD_POSITIONS = new HashMap<>() {{
        put(ColorEntity.Type.RED, new Vector2(Constants.WIDTH * 0.5f, Constants.HEIGHT * 0.9296f));
        put(ColorEntity.Type.GREEN, new Vector2(Constants.WIDTH * 0.9296f, Constants.HEIGHT * 0.5f));
        put(ColorEntity.Type.BLUE, new Vector2(Constants.WIDTH * 0.5f, Constants.HEIGHT * 0.0703f));
        put(ColorEntity.Type.YELLOW, new Vector2(Constants.WIDTH * 0.0703f, Constants.HEIGHT * 0.5f));
    }};

    private final Map<Integer, ColorEntity.Type> keyHandMap;

    private final Difficulty difficulty;
    private Stage stage = Stage.COUNTDOWN;

    private final GemSpawner gemSpawner;
    private final Random random;

    private final Pad[] pads;
    private final Hand[] hands;
    private final Tile[][] tiles;
    private final List<Tile> availableTiles;

    public PlayScreen(Context context, Difficulty difficulty) {
        super(context);

        this.difficulty = difficulty;

        gemSpawner = new GemSpawner(context, difficulty);
        random = new Random();

        keyHandMap = new HashMap<>();
        List<ColorEntity.Type> types = new ArrayList<>();
        types.add(ColorEntity.Type.GREEN);
        keyHandMap.put(Input.Keys.D, ColorEntity.Type.GREEN);
        types.add(ColorEntity.Type.YELLOW);
        keyHandMap.put(Input.Keys.A, ColorEntity.Type.YELLOW);
        if (difficulty == Difficulty.NORMAL) {
            types.add(ColorEntity.Type.RED);
            keyHandMap.put(Input.Keys.W, ColorEntity.Type.RED);
        }
        if (difficulty == Difficulty.WEIRD) {
            types.add(ColorEntity.Type.RED);
            keyHandMap.put(Input.Keys.W, ColorEntity.Type.RED);
            types.add(ColorEntity.Type.BLUE);
            keyHandMap.put(Input.Keys.S, ColorEntity.Type.BLUE);
        }
        pads = new Pad[types.size()];
        hands = new Hand[types.size()];
        for (int i = 0; i < types.size(); i++) {
            ColorEntity.Type type = types.get(i);
            pads[i] = new Pad(context, type);
            pads[i].p.set(PAD_POSITIONS.get(type));
            hands[i] = new Hand(context, type);
            hands[i].setBasePosition(PAD_POSITIONS.get(type));
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

        Gem[] gems = gemSpawner.init();
        for (Gem gem : gems) placeGem(gem);
    }

    private void placeGem(Gem gem) {
        if (availableTiles.isEmpty()) return;
        int index = random.nextInt(availableTiles.size());
        Tile tile = availableTiles.remove(index);
        tile.setGem(gem);
    }

    @Override
    protected void handleInput() {
        if (ignoreInput) return;

        unproject();
        keyHandMap.forEach((k, v) -> {
            if (Gdx.input.isKeyJustPressed(k)) {
                for (Hand hand : hands) {
                    if (hand.type == v) {
                        hand.grab(m);
                    }
                }
            }
        });
    }

    @Override
    public void update(float dt) {
        handleInput();

        for (int i = 0; i < pads.length; i++) {
            pads[i].update(dt);
            hands[i].update(dt);
        }

        if (stage == Stage.COUNTDOWN) {
            stage = Stage.PLAYING;
        } else if (stage == Stage.PLAYING) {
            Gem gem = gemSpawner.take(dt);
            if (gem != null) {
                placeGem(gem);
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
            for (int i = 0; i < pads.length; i++) {
                pads[i].render(b);
                hands[i].render(b);
            }
            for (int row = 0; row < tiles.length; row++) {
                for (int col = 0; col < tiles[row].length; col++) {
                    tiles[row][col].render(b);
                }
            }
        }
        b.end();
    }

}
