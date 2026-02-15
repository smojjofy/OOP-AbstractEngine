package com.p1_7.demo.managers;

import com.p1_7.abstractengine.render.RenderManager;

/**
 * demo-specific render manager.
 *
 * minimal extension that delegates all custom rendering to icustomrenderable implementations.
 */
public class DemoRenderManager extends RenderManager {
    // no custom rendering logic required - all handled by ICustomRenderable implementations
}
