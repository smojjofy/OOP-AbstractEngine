package com.p1_7.game.platform;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.p1_7.abstractengine.render.IShapeRenderer;

/**
 * libgdx implementation of IShapeRenderer that wraps a ShapeRenderer.
 */
public class GdxShapeRenderer implements IShapeRenderer {

    /** the underlying libgdx shape renderer */
    private final ShapeRenderer renderer = new ShapeRenderer();

    @Override
    public void begin() {
        renderer.begin(ShapeType.Filled);
    }

    @Override
    public void end() {
        renderer.end();
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }

    /**
     * exposes the underlying libgdx ShapeRenderer for game code that
     * requires direct access (e.g. setColor, rect).
     *
     * @return the wrapped ShapeRenderer
     */
    public ShapeRenderer unwrap() {
        return renderer;
    }
}
