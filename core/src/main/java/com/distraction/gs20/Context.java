package com.distraction.gs20;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.distraction.gs20.gj.GameJoltClient;
import com.distraction.gs20.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import de.golfgl.gdxgamesvcs.leaderboard.ILeaderBoardEntry;

/**
 * Holds all important game stuff.
 */
public class Context {

    private final static String ATLAS_NAME = "gs20.atlas";
    private static final String IMPACT_NAME = "impact20.fnt";

    public final AssetManager assetManager;
    public final GameScreenManager gsm;
    public final Batch b;

    public GameJoltClient client;
    public List<ILeaderBoardEntry> entries = new ArrayList<>();

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

    public void fetchLeaderboard(SimpleCallback callback) {
        entries.clear();
        if (Constants.LEADERBOARD_ID == 0) return;
        client.fetchLeaderboardEntries("", 7, false, leaderBoard -> {
            entries.clear();
            for (int i = 0; i < leaderBoard.size; i++) {
                entries.add(leaderBoard.get(i));
            }
            callback.callback();
        });
    }

    public void submitScore(String name, int score, Net.HttpResponseListener listener) {
        client.setGuestName(name);
        client.submitToLeaderboard("", score, "", 10000, listener);
    }

    public interface SimpleCallback {
        void callback();
    }

}
