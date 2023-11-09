package com.distraction.gs20.utils;

import com.badlogic.gdx.graphics.Color;

public class Constants {

    public static final String GAME_TITLE = "Gem Snag 20";

    public static final boolean FULLSCREEN = true;

    public static final int WIDTH = 480;
    public static final int HEIGHT = 270;

    public static final int DESKTOP_SCALE = 2;
    public static final int DESKTOP_WIDTH = WIDTH * DESKTOP_SCALE;
    public static final int DESKTOP_HEIGHT = HEIGHT * DESKTOP_SCALE;

    public static final int TILEMAP_SIZE = 5;
    public static final float TILE_SIZE = 0.1407f;

    public static final Color TILE_HIGHLIGHT_COLOR = new Color(1, 1, 1, 0.3f);
    public static final Color RED = Color.valueOf("E43B44");
    public static final Color GREEN = Color.valueOf("63C74D");
    public static final Color BLUE = Color.valueOf("0099DB");
    public static final Color YELLOW = Color.valueOf("FEE761");
    public static final Color DARK = Color.valueOf("181425");

    public static String APP_ID = "";
    public static String API_KEY = "";
    public static int LEADERBOARD_ID = 0;

    static {
        // not for you
        APP_ID = ApiConstants.APP_ID;
        API_KEY = ApiConstants.API_KEY;
        LEADERBOARD_ID = ApiConstants.LEADERBOARD_ID;
    }

}
