package com.p1_7.abstractengine.demo;

/**
 * Functional interface for handling droplet catch events.
 *
 * <p>Invoked by the bucket when it collides with a falling droplet,
 * allowing the game scene to respond with score updates, sound effects,
 * and entity management.</p>
 */
@FunctionalInterface
public interface DropletCatchHandler {

    /**
     * Handles a droplet being caught by the bucket.
     *
     * @param droplet the droplet that was caught
     */
    void handleCatch(Droplet droplet);
}
