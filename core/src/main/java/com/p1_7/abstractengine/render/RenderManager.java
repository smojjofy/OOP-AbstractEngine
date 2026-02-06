package com.p1_7.abstractengine.render;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;

import com.p1_7.abstractengine.engine.Manager;
import com.p1_7.abstractengine.transform.ITransform;

/**
 * Owns the drawing resources and drives the per-frame render pass.
 *
 * <p>This manager extends {@link Manager} directly — it has no
 * per-frame {@code update()} logic.  All drawing happens inside the
 * explicit {@link #render()} call that the {@link com.p1_7.abstractengine.engine.Engine}
 * issues each frame.</p>
 *
 * <p>The render pass is split into two phases:
 * <ol>
 *   <li><strong>Textured phase</strong> — items whose
 *       {@link IRenderable#getAssetPath()} is non-null are drawn via a
 *       {@link SpriteBatch}.</li>
 *   <li><strong>Procedural phase</strong> — items that return
 *       {@code null} from {@code getAssetPath()} are drawn as filled
 *       rectangles via a {@link ShapeRenderer}.</li>
 * </ol>
 * After both phases complete the queue is cleared.</p>
 */
public class RenderManager extends Manager {

    /** sprite batch used for textured items */
    private SpriteBatch batch;

    /** shape renderer used for procedural items */
    private ShapeRenderer shapeRenderer;

    /** asset manager for texture loading and caching */
    private AssetManager assetManager;

    /** single-frame queue of items to draw */
    private final RenderQueue queue = new RenderQueue();

    // ---------------------------------------------------------------
    // Manager lifecycle hooks
    // ---------------------------------------------------------------

    /**
     * Creates the {@link SpriteBatch}, {@link ShapeRenderer}, and
     * {@link AssetManager} resources.
     */
    @Override
    protected void onInit() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        assetManager = new AssetManager();
    }

    /**
     * Disposes the {@link SpriteBatch}, {@link ShapeRenderer}, and
     * {@link AssetManager} resources.
     */
    @Override
    protected void onShutdown() {
        // dispose asset manager (disposes all loaded assets)
        if (assetManager != null) {
            assetManager.dispose();
        }
        if (batch != null) {
            batch.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }

    // ---------------------------------------------------------------
    // public API
    // ---------------------------------------------------------------

    /**
     * Returns the render queue that scenes use to submit items for
     * drawing.
     *
     * @return the {@link IRenderQueue} instance
     */
    public IRenderQueue getRenderQueue() {
        return queue;
    }

    /**
     * Executes the full render pass for the current frame.
     *
     * <ol>
     *   <li>Textured phase: iterates the queue and draws every item
     *       that has a non-null asset path via the {@link SpriteBatch}.</li>
     *   <li>Procedural phase: iterates the queue and draws every item
     *       that has a null asset path as a filled rectangle via the
     *       {@link ShapeRenderer}.</li>
     *   <li>Clears the queue so it is empty for the next frame.</li>
     * </ol>
     */
    public void render() {
        // ensure all queued assets are loaded
        assetManager.finishLoading();

        // --- textured pass ---
        batch.begin();
        for (IRenderItem item : queue.items()) {
            if (item.getAssetPath() != null) {
                draw(item);
            }
        }
        batch.end();

        // --- procedural pass ---
        shapeRenderer.begin(ShapeType.Filled);
        for (IRenderItem item : queue.items()) {
            if (item.getAssetPath() == null) {
                drawProcedural(item);
            }
        }
        shapeRenderer.end();

        // --- flush ---
        queue.clear();
    }

    // ---------------------------------------------------------------
    // private drawing helpers
    // ---------------------------------------------------------------

    /**
     * Draws a textured or text item.  Loads textures via AssetManager
     * on first access and draws them with the SpriteBatch.  Handles
     * special text rendering for LivesDisplay.
     *
     * @param item the render item with a non-null asset path (or text item)
     */
    private void draw(IRenderItem item) {
        String assetPath = item.getAssetPath();

        // skip null paths (handled by procedural pass)
        if (assetPath == null) {
            return;
        }

        // load texture via AssetManager if not already loaded
        if (!assetManager.isLoaded(assetPath, Texture.class)) {
            assetManager.load(assetPath, Texture.class);
            assetManager.finishLoadingAsset(assetPath);  // blocking load
        }

        Texture texture = assetManager.get(assetPath, Texture.class);
        ITransform transform = item.getTransform();
        float[] position = transform.getPosition();
        float[] size = transform.getSize();

        batch.draw(texture, position[0], position[1], size[0], size[1]);
    }

    /**
     * Draws a filled rectangle or text at the item's transform position.
     * Handles special text rendering for LivesDisplay and TextDisplay using BitmapFont.
     * Falls back to rectangle drawing for other null-path items.
     *
     * @param item the render item to draw procedurally
     */
    private void drawProcedural(IRenderItem item) {
        // handle text rendering for LivesDisplay
        if (item instanceof com.p1_7.abstractengine.demo.LivesDisplay) {
            com.p1_7.abstractengine.demo.LivesDisplay display =
                (com.p1_7.abstractengine.demo.LivesDisplay) item;
            ITransform transform = item.getTransform();
            float[] position = transform.getPosition();

            // text rendering requires ending shape renderer and starting batch
            shapeRenderer.end();
            batch.begin();
            display.getFont().draw(batch, display.getText(), position[0], position[1]);
            batch.end();
            shapeRenderer.begin(ShapeType.Filled);
            return;
        }

        // handle text rendering for TextDisplay
        if (item instanceof com.p1_7.abstractengine.demo.TextDisplay) {
            com.p1_7.abstractengine.demo.TextDisplay display =
                (com.p1_7.abstractengine.demo.TextDisplay) item;
            ITransform transform = item.getTransform();
            float[] position = transform.getPosition();

            // text rendering requires ending shape renderer and starting batch
            shapeRenderer.end();
            batch.begin();
            display.getFont().draw(batch, display.getText(), position[0], position[1]);
            batch.end();
            shapeRenderer.begin(ShapeType.Filled);
            return;
        }

        // fallback to rectangle drawing
        ITransform transform = item.getTransform();
        float[] position = transform.getPosition();
        float[] size = transform.getSize();

        // destructure the x, y, width, height from the arrays
        float x = position[0];
        float y = position[1];
        float w = size[0];
        float h = size[1];

        shapeRenderer.rect(x, y, w, h);
    }
}
