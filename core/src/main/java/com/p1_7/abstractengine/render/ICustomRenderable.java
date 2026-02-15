package com.p1_7.abstractengine.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * interface for entities that provide custom rendering logic.
 *
 * entities implementing this interface own their rendering behaviour, eliminating
 * instanceof checks in render managers. when rendercustom() is called, the shaperenderer
 * is active with filled mode and the spritebatch is inactive. implementations that need
 * to switch renderers must end/begin them correctly and restore the original state.
 */
public interface ICustomRenderable {

    /**
     * renders this entity using custom logic.
     *
     * @param batch the sprite batch (currently inactive)
     * @param shapeRenderer the shape renderer (currently active)
     */
    void renderCustom(SpriteBatch batch, ShapeRenderer shapeRenderer);
}
