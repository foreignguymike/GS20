package com.distraction.gs20.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.distraction.gs20.Context;
import com.distraction.gs20.utils.Constants;

public class Tile extends Entity {

    private final TextureRegion pixel;

    public Gem gem;

    public Tile(Context context) {
        pixel = context.getImage("pixel");
        float size = Constants.WIDTH * Constants.TILE_SIZE;
        setSize(size, size);
    }

    public void setGem(Gem gem) {
        this.gem = gem;
        gem.p.set(p);
    }

    @Override
    public void render(Batch b) {
        b.setColor(Constants.TILE_COLOR);
        b.draw(pixel, sleft(), sbottom(), swidth(), 1f);
        b.draw(pixel, sleft(), sbottom(), 1f, sheight());
        b.draw(pixel, sleft(), stop(), swidth(), 1f);
        b.draw(pixel, sright(), sbottom(), 1f, sheight());

        if (gem != null) {
            gem.render(b);
        }
    }
}
