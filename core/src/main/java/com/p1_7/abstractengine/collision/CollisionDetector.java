package com.p1_7.abstractengine.collision;

/**
 * stateless utility that checks whether two ICollidable
 * objects overlap.
 *
 * the check is performed by comparing the axis-aligned bounding
 * rectangles returned by each collidable via
 * com.badlogic.gdx.math.Rectangle.overlaps(com.badlogic.gdx.math.Rectangle).
 */
public class CollisionDetector {

    /**
     * determines whether the bounding rectangles of the two
     * collidables overlap.
     *
     * @param a the first collidable
     * @param b the second collidable
     * @return true if their bounds overlap
     */
    public boolean checkCollision(ICollidable a, ICollidable b) {
        return a.getBounds().overlaps(b.getBounds());
    }
}
