package com.distraction.gs20.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.distraction.gs20.Context;
import com.distraction.gs20.utils.Constants;
import com.distraction.gs20.utils.Utils;

public class Tile extends Entity {

    private static final float FLASH_TIME = 0.5f;

    private final TextureRegion pixel;

    private Gem gem;

    public boolean highlight;

    private float time;
    private Color flashColor;

    public Tile(Context context) {
        pixel = context.getImage("pixel");
        float size = Constants.WIDTH * Constants.TILE_SIZE;
        setSize(size, size);
    }

    public void setGem(Gem gem) {
        this.gem = gem;
        gem.tile = this;
        gem.p.set(p);
        gem.d.set(p);
    }

    public Gem takeGem() {
        Gem gem1 = gem;
        gem = null;
        return gem1;
    }

    public void flash(Color color) {
        this.flashColor = color;
    }

    @Override
    public void update(float dt) {
        if (flashColor != null) {
            time += dt;
        }
        if (time > FLASH_TIME) {
            flashColor = null;
            time = 0f;
        }
        if (gem != null) {
            gem.update(dt);
        }
    }

    @Override
    public void render(Batch b) {
        if (flashColor != null) {
            b.setColor(flashColor);
            Utils.setAlpha(b, FLASH_TIME - time / FLASH_TIME);
            b.draw(pixel, sleft() + 1, sbottom() + 1, swidth() - 2, sheight() - 2);
        }
        if (highlight) {
            b.setColor(Constants.TILE_HIGHLIGHT_COLOR);
            b.draw(pixel, sleft() + 1, sbottom() + 1, swidth() - 2, sheight() - 2);
        }
    }
}
