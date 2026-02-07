package com.p1_7.abstractengine.scene;

import com.p1_7.abstractengine.entity.IEntityRepository;
import com.p1_7.abstractengine.input.IInputQuery;
import com.p1_7.abstractengine.render.IRenderQueue;

/**
 * read-only snapshot of engine state passed into every Scene
 * callback.
 *
 * a Scene receives a SceneContext so that it can
 * query entities, submit render items and read input without holding
 * direct references to the underlying managers.
 */
public interface SceneContext {

    /**
     * returns the read-only entity repository.
     *
     * @return the IEntityRepository; never null
     */
    IEntityRepository entities();

    /**
     * returns the render queue for submitting items this frame.
     *
     * @return the IRenderQueue; never null
     */
    IRenderQueue renderQueue();

    /**
     * returns the input query interface for the current frame.
     *
     * @return the IInputQuery; never null
     */
    IInputQuery input();

    /**
     * returns the scene manager for requesting scene transitions.
     *
     * @return the SceneManager; never null
     */
    SceneManager sceneManager();
}
