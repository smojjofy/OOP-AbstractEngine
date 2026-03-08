package com.p1_7.game.platform;

import com.p1_7.abstractengine.render.IAssetStore;
import com.p1_7.abstractengine.render.IShapeRenderer;
import com.p1_7.abstractengine.render.ISpriteBatch;
import com.p1_7.abstractengine.render.RenderManager;

/**
 * libgdx-specific render manager that provides concrete drawing resources.
 */
public class GdxRenderManager extends RenderManager {

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
}
