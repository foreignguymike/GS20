package com.distraction.gs20.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;

public class Utils {

    private Utils() {}

    public static void setAlpha(Batch b, float alpha) {
        Color c = b.getColor();
        b.setColor(c.r, c.g, c.b, alpha);
    }
}
