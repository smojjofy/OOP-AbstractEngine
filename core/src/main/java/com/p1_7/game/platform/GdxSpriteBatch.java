package com.p1_7.game.platform;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.p1_7.abstractengine.render.ISpriteBatch;

/**
 * libgdx implementation of ISpriteBatch that wraps a SpriteBatch.
 */
public class GdxSpriteBatch implements ISpriteBatch {

    /** the underlying libgdx sprite batch */
    private final SpriteBatch batch = new SpriteBatch();

    @Override
    public void begin() {
        batch.begin();
    }

    @Override
    public void end() {
        batch.end();
    }

    /**
     * draws a texture at the specified position and size.
     *
     * @param textureHandle the loaded texture (must be a libgdx Texture)
     * @param x             the x position
     * @param y             the y position
     * @param width         the draw width
     * @param height        the draw height
     */
    @Override
    public void draw(Object textureHandle, float x, float y, float width, float height) {
        batch.draw((Texture) textureHandle, x, y, width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    /**
     * exposes the underlying libgdx SpriteBatch for game code that
     * requires direct access (e.g. BitmapFont.draw).
     *
     * @return the wrapped SpriteBatch
     */
    public SpriteBatch unwrap() {
        return batch;
    }
}
