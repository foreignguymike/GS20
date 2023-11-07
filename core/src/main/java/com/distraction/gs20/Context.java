package com.distraction.gs20;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Holds all important game stuff.
 */
public class Context {

    private final static String ATLAS_NAME = "gs20.atlas";
    private static final String IMPACT_NAME = "impact20.fnt";

    public final AssetManager assetManager;
    public final GameScreenManager gsm;
    public final Batch b;

    public boolean babyMode = false;

    public Context() {
        assetManager = new AssetManager();
        assetManager.load(ATLAS_NAME, TextureAtlas.class);
        assetManager.load(IMPACT_NAME, BitmapFont.class);
        assetManager.finishLoading();

        gsm = new GameScreenManager();
        b = new SpriteBatch();
    }

    public TextureRegion getImage(String key) {
        return assetManager.get(ATLAS_NAME, TextureAtlas.class).findRegion(key);
    }

    public BitmapFont getFont() {
        return assetManager.get(IMPACT_NAME, BitmapFont.class);
    }

    public void dispose() {
        b.dispose();
    }

}
