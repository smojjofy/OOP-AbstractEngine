package com.p1_7.abstractengine.render;

import com.badlogic.gdx.utils.Array;

/**
 * Simple array-backed implementation of {@link IRenderQueue}.
 * One instance is held for the lifetime of the {@link RenderManager}.
 */
class RenderQueue implements IRenderQueue {

    /** the backing store for queued items */
    private final Array<IRenderItem> items = new Array<>();

    /**
     * Adds an item to the queue for drawing this frame.
     *
     * @param item the render item to enqueue
     */
    @Override
    public void queue(IRenderItem item) {
        items.add(item);
    }

    /**
     * Removes all items from the queue.
     */
    @Override
    public void clear() {
        items.clear();
    }

    /**
     * Returns the backing array so that the render manager can
     * iterate it.
     *
     * @return the array of queued render items
     */
    @Override
    public Array<IRenderItem> items() {
        return items;
    }
}
