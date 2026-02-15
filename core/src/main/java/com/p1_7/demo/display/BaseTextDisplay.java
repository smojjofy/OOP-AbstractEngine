package com.p1_7.demo.display;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.p1_7.abstractengine.entity.Entity;
import com.p1_7.abstractengine.render.ICustomRenderable;
import com.p1_7.abstractengine.render.IRenderItem;
import com.p1_7.abstractengine.transform.ITransform;
import com.p1_7.demo.core.Transform2D;

/**
 * abstract base class for text-based display entities.
 *
 * consolidates common text rendering logic across multiple display types.
 * subclasses only need to provide the text content via gettext().
 */
public abstract class BaseTextDisplay extends Entity implements IRenderItem, ICustomRenderable {

    /** 2d spatial transform for positioning */
    protected final Transform2D transform;

    /** font used for rendering the text */
    protected final BitmapFont font;

    /**
     * constructs a text display with the specified position, size, and font scale.
     *
     * @param x the x position (left edge)
     * @param y the y position (baseline)
     * @param width the width (used for transform, may be 0 for text)
     * @param height the height (used for transform, may be 0 for text)
     * @param scale the font scale multiplier (1.0f = normal size)
     */
    protected BaseTextDisplay(float x, float y, float width, float height, float scale) {
        super();
        this.font = new BitmapFont(); // libgdx default font
        this.font.getData().setScale(scale);
        this.transform = new Transform2D(x, y, width, height);
    }

    /**
     * renders the text by switching to spritebatch then restoring shaperenderer.
     *
     * @param batch the sprite batch (currently inactive)
     * @param shapeRenderer the shape renderer (currently active)
     */
    @Override
    public void renderCustom(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        // switch from shaperenderer to batch for text rendering
        shapeRenderer.end();
        batch.begin();

        // render the text at the transform position
        float[] position = transform.getPosition();
        font.draw(batch, getText(), position[0], position[1]);

        // restore shaperenderer for subsequent procedural items
        batch.end();
        shapeRenderer.begin(ShapeType.Filled);
    }

    /**
     * returns the text content to display.
     *
     * @return the text string to render
     */
    public abstract String getText();

    /**
     * returns the font used for rendering.
     *
     * @return the bitmapfont instance
     */
    public BitmapFont getFont() {
        return font;
    }

    /**
     * disposes the font resource.
     * should be called when the text display is no longer needed.
     */
    public void dispose() {
        font.dispose();
    }

    @Override
    public String getAssetPath() {
        // null signals custom rendering via icustomrenderable
        return null;
    }

    @Override
    public ITransform getTransform() {
        return transform;
    }
}
