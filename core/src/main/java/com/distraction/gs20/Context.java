package com.distraction.gs20;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Holds all important game stuff.
 */
public class Context {

    private final static String ATLAS_NAME = "gs20.atlas";

    public final AssetManager assetManager;
    public final GameScreenManager gsm;
    public final Batch b;

    public Context() {
        assetManager = new AssetManager();
        assetManager.load(ATLAS_NAME, TextureAtlas.class);
        assetManager.finishLoading();

        gsm = new GameScreenManager();
        b = new SpriteBatch();
    }

    public TextureRegion getImage(String key) {
        return assetManager.get(ATLAS_NAME, TextureAtlas.class).findRegion(key);
    }

    public TextureRegion getImage(String key, int index) {
        return assetManager.get(ATLAS_NAME, TextureAtlas.class).findRegion(key, index);
    }

    public void dispose() {
        b.dispose();
    }

}
