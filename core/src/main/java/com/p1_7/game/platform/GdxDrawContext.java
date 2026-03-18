package com.p1_7.game.platform;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.p1_7.abstractengine.render.IAssetStore;
import com.p1_7.abstractengine.render.IDrawContext;

/**
 * libgdx draw context that manages SpriteBatch / ShapeRenderer pass
 * transitions. entities cast to GdxDrawContext inside their render()
 * implementation to call the drawing methods below.
 *
 * pass transitions are lazy — openBatch() and openShape() are no-ops
 * if the correct pass is already active, preserving batching across
 * consecutive textured items.
 */
public class GdxDrawContext implements IDrawContext {

    /** the active drawing pass for this frame */
    private enum Pass { NONE, BATCH, SHAPE }

    private final SpriteBatch   batch;
    private final ShapeRenderer shapeRenderer;
    private final IAssetStore   assetStore;

    private Pass currentPass = Pass.NONE;

    /**
     * constructs a draw context backed by the provided libgdx resources.
     *
     * @param batch         the sprite batch wrapper
     * @param shapeRenderer the shape renderer wrapper
     * @param assetStore    the asset store for texture lookup
     */
    public GdxDrawContext(GdxSpriteBatch batch, GdxShapeRenderer shapeRenderer,
                          IAssetStore assetStore) {
        this.batch         = batch.unwrap();
        this.shapeRenderer = shapeRenderer.unwrap();
        this.assetStore    = assetStore;
    }

    /**
     * ends the currently active drawing pass and resets to idle.
     * called by RenderManager after all items have been rendered.
     */
    @Override
    public void flush() {
        if (currentPass == Pass.BATCH) {
            batch.end();
        } else if (currentPass == Pass.SHAPE) {
            shapeRenderer.end();
        }
        currentPass = Pass.NONE;
    }

    /**
     * draws a managed texture from the asset store at the specified bounds.
     *
     * @param assetPath the path to the texture asset
     * @param x         left edge in world coordinates
     * @param y         bottom edge in world coordinates
     * @param w         draw width
     * @param h         draw height
     */
    public void drawTexture(String assetPath, float x, float y, float w, float h) {
        openBatch();
        Texture texture = (Texture) assetStore.loadTexture(assetPath);
        batch.draw(texture, x, y, w, h);
    }

    /**
     * draws a raw libgdx Texture (owned directly by the caller) with a colour tint.
     * resets the batch colour to white after drawing.
     *
     * @param texture the texture to draw
     * @param tint    the colour tint to apply
     * @param x       left edge in world coordinates
     * @param y       bottom edge in world coordinates
     * @param w       draw width
     * @param h       draw height
     */
    public void drawRawTexture(Texture texture, Color tint,
                               float x, float y, float w, float h) {
        openBatch();
        batch.setColor(tint);
        batch.draw(texture, x, y, w, h);
        batch.setColor(Color.WHITE);
    }

    /**
     * draws a string using the given BitmapFont at the specified position.
     *
     * @param font the bitmap font to draw with
     * @param text the string to draw
     * @param x    left edge in world coordinates
     * @param y    baseline y in world coordinates
     */
    public void drawFont(BitmapFont font, String text, float x, float y) {
        openBatch();
        font.draw(batch, text, x, y);
    }

    /**
     * fills a rectangle with the given colour.
     *
     * @param color the fill colour
     * @param x     left edge in world coordinates
     * @param y     bottom edge in world coordinates
     * @param w     rectangle width
     * @param h     rectangle height
     */
    public void fillRect(Color color, float x, float y, float w, float h) {
        openShape();
        shapeRenderer.setColor(color);
        shapeRenderer.rect(x, y, w, h);
    }

    // ── private pass management ───────────────────────────────────

    /**
     * ensures the sprite batch pass is open, closing the shape pass first if needed.
     */
    private void openBatch() {
        if (currentPass == Pass.BATCH) {
            return;
        }
        if (currentPass == Pass.SHAPE) {
            shapeRenderer.end();
        }
        batch.begin();
        currentPass = Pass.BATCH;
    }

    /**
     * ensures the shape renderer pass is open, closing the batch pass first if needed.
     */
    private void openShape() {
        if (currentPass == Pass.SHAPE) {
            return;
        }
        if (currentPass == Pass.BATCH) {
            batch.end();
        }
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        currentPass = Pass.SHAPE;
    }
}
