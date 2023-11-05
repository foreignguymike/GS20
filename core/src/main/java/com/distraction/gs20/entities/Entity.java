package com.distraction.gs20.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {

    // position
    public Vector2 p = new Vector2();

    // size
    public Vector2 s = new Vector2();

    // vector
    public Vector2 v = new Vector2();

    // dest
    public Vector2 d = new Vector2();

    public float scale = 1f;
    public boolean remove;

    protected void setSize(TextureRegion image) {
        s.x = image.getRegionWidth();
        s.y = image.getRegionHeight();
    }

    protected void setSize(float w, float h) {
        s.x = w;
        s.y = h;
    }

    protected void move(float dt) {
        p.add(v.x * dt, v.y * dt);
        if ((v.x > 0 && p.x > d.x) || (v.x < 0 && p.x < d.x)) {
            v.x = 0;
            p.x = d.x;
        }
        if ((v.y > 0 && p.y > d.y) || (v.y < 0 && p.y < d.y)) {
            v.y = 0;
            p.y = d.y;
        }
    }

    public boolean atDestination() {
        return p.x == d.x && p.y == d.y;
    }

    public void setVectorFromDist(float speed) {
        float dx = d.x - p.x;
        float dy = d.y - p.y;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        v.set(dx * speed / len, dy * speed / len);
    }

    public boolean contains(float x, float y) {
        return x >= left() && x <= right() && y >= bottom() && y <= top();
    }

    public boolean contains(float x, float y, float padding) {
        return x >= left() - padding && x <= right() + padding && y >= bottom() - padding && y <= top() + padding;
    }

    public boolean intersects(Entity other) {
        float x = left();
        float ox = other.left();
        float w = s.x;
        float ow = other.s.x;
        float y = bottom();
        float oy = other.bottom();
        float h = s.y;
        float oh = other.s.y;
        return x < ox + ow && x + w > ox && y < oy + oh && y + h > oy;
    }

    public float left() {
        return p.x - s.x / 2f;
    }

    public float sleft() {
        return p.x - scale * s.x / 2f;
    }

    public float left(TextureRegion image) {
        return p.x - image.getRegionWidth() / 2f;
    }

    public float sleft(TextureRegion image) {
        return p.x - image.getRegionWidth() * scale / 2f;
    }

    public float right() {
        return p.x + s.x / 2f;
    }

    public float sright() {
        return p.x + scale * s.x / 2f;
    }

    public float top() {
        return p.y + s.y / 2f;
    }

    public float stop() {
        return p.y + scale * s.y / 2f;
    }

    public float bottom() {
        return p.y - s.y / 2f;
    }

    public float sbottom() {
        return p.y - scale * s.y / 2f;
    }

    public float bottom(TextureRegion image) {
        return p.y - image.getRegionHeight() / 2f;
    }

    public float sbottom(TextureRegion image) {
        return p.y - image.getRegionHeight() * scale / 2f;
    }

    public float swidth() {
        return s.x * scale;
    }

    public float swidth(TextureRegion image) {
        return image.getRegionWidth() * scale;
    }

    public float sheight() {
        return s.y * scale;
    }

    public float sheight(TextureRegion image) {
        return image.getRegionHeight() * scale;
    }

    public void update(float dt) {
    }

    public void render(Batch b) {
    }

    protected void basicRender(Batch b, TextureRegion image) {
        b.draw(image, sleft(image), sbottom(image), swidth(image), sheight(image));
    }

}
