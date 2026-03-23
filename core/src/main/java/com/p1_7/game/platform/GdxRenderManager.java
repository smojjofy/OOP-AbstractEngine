package com.p1_7.game.platform;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.p1_7.abstractengine.render.IAssetStore;
import com.p1_7.abstractengine.render.IDrawContext;
import com.p1_7.abstractengine.render.IShapeRenderer;
import com.p1_7.abstractengine.render.ISpriteBatch;
import com.p1_7.abstractengine.render.RenderManager;

/**
 * libgdx-specific render manager that provides concrete drawing resources.
 *
 * overrides onInit() to create concrete wrapper types directly, avoiding
 * downcasts in createDrawContext().
 */
public class GdxRenderManager extends RenderManager {

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        super.render();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /**
     * creates all drawing resources using concrete wrapper types, then stores
     * them in the parent's protected fields. this avoids the downcast that
     * would otherwise be needed in createDrawContext().
     */
    @Override
    protected void onInit() {
        GdxSpriteBatch   gdxBatch = new GdxSpriteBatch();
        GdxShapeRenderer gdxShape = new GdxShapeRenderer();
        this.batch         = gdxBatch;
        this.shapeRenderer = gdxShape;
        this.assetStore    = createAssetStore();
        this.drawCtx       = new GdxDrawContext(gdxBatch, gdxShape, assetStore);
    }

    @Override
    protected ISpriteBatch createSpriteBatch() {
        return new GdxSpriteBatch();
    }

    @Override
    protected IShapeRenderer createShapeRenderer() {
        return new GdxShapeRenderer();
    }

    @Override
    protected IAssetStore createAssetStore() {
        return new GdxAssetStore();
    }

    @Override
    protected IDrawContext createDrawContext(ISpriteBatch batch,
                                             IShapeRenderer shapeRenderer,
                                             IAssetStore assetStore) {
        // not used — onInit() creates the draw context directly to avoid downcasts
        return new GdxDrawContext((GdxSpriteBatch) batch,
                                  (GdxShapeRenderer) shapeRenderer,
                                  assetStore);
    }
}
