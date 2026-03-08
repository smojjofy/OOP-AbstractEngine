package com.p1_7.game.entities;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.p1_7.abstractengine.entity.Entity;
import com.p1_7.abstractengine.render.ICustomRenderable;
import com.p1_7.abstractengine.render.IRenderItem;
import com.p1_7.abstractengine.render.IShapeRenderer;
import com.p1_7.abstractengine.render.ISpriteBatch;
import com.p1_7.abstractengine.transform.ITransform;
import com.p1_7.game.core.Transform2D;
import com.p1_7.game.platform.GdxShapeRenderer;
import com.p1_7.game.platform.GdxSpriteBatch;

/**
 * A renderable entity that draws a string using a BitmapFont.
 *
 * Returns null from getAssetPath() to signal the render manager that it
 * should call renderCustom() instead of the standard sprite path. Casts
 * the engine-provided batch and shapeRenderer to their libGDX wrappers so
 * that BitmapFont.draw() can access the underlying SpriteBatch.
 */
public class HelloWorldText extends Entity implements IRenderItem, ICustomRenderable {

    private final Transform2D transform;
    private final BitmapFont font;
    private final String text;

    /**
     * @param text  the string to display
     * @param x     left edge of the text in screen coordinates
     * @param y     baseline of the text in screen coordinates
     * @param scale uniform scale applied to the default bitmap font
     */
    public HelloWorldText(String text, float x, float y, float scale) {
        this.text = text;
        this.transform = new Transform2D(x, y, 0f, 0f);
        this.font = new BitmapFont();
        this.font.getData().setScale(scale);
    }

    /**
     * Returns null so the render manager routes this entity through
     * the custom-rendering path rather than the sprite path.
     */
    @Override
    public String getAssetPath() {
        return null;
    }

    @Override
    public ITransform getTransform() {
        return transform;
    }

    /**
     * Draws the text using the underlying libGDX sprite batch.
     *
     * The render manager leaves the shape renderer open when it calls this
     * method, so we must end it before opening the batch, then restore it
     * afterwards so subsequent custom renderables work correctly.
     *
     * @param batch         the engine sprite batch (cast to GdxSpriteBatch)
     * @param shapeRenderer the engine shape renderer (cast to GdxShapeRenderer)
     */
    @Override
    public void renderCustom(ISpriteBatch batch, IShapeRenderer shapeRenderer) {
        // close the shape renderer before opening the sprite batch
        ((GdxShapeRenderer) shapeRenderer).unwrap().end();
        ((GdxSpriteBatch) batch).unwrap().begin();

        font.draw(
                ((GdxSpriteBatch) batch).unwrap(),
                text,
                transform.getPosition(0),
                transform.getPosition(1));

        // restore the shape renderer for any subsequent custom renderables
        ((GdxSpriteBatch) batch).unwrap().end();
        ((GdxShapeRenderer) shapeRenderer).unwrap().begin(ShapeType.Filled);
    }
}
