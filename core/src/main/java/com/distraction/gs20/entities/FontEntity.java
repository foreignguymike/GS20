package com.distraction.gs20.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.distraction.gs20.Context;

import java.util.Objects;

public class FontEntity extends Entity {

    private final GlyphLayout glyphLayout;
    private final BitmapFont font;

    private String currentText = "";

    public FontEntity(BitmapFont font) {
        this.font = font;
        glyphLayout = new GlyphLayout();
        glyphLayout.setText(font,"");
    }

    public void setText(String text) {
        if (!Objects.equals(currentText, text)) {
            currentText = text;
            glyphLayout.setText(font, text);
            setSize(glyphLayout.width, glyphLayout.height);
        }
    }

    @Override
    public void render(Batch b) {
        b.setColor(1, 1, 1, 1);
        font.draw(b, glyphLayout, p.x - glyphLayout.width / 2f, p.y + glyphLayout.height / 2f);
    }

    public void renderLeft(Batch b) {
        b.setColor(1, 1, 1, 1);
        font.draw(b, glyphLayout, p.x, p.y + glyphLayout.height / 2f);
    }
}
