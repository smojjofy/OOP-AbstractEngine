package com.p1_7.abstractengine.collision;

import com.badlogic.gdx.math.Rectangle;

/**
 * capability interface for any entity that participates in collision
 * detection.
 *
 * the bounding Rectangle is used by
 * CollisionDetector to perform overlap tests. the
 * onCollision(ICollidable) callback is invoked by the
 * CollisionManager when an overlap is detected with another
 * collidable.
 */
public interface ICollidable {

    /**
     * returns the axis-aligned bounding rectangle that represents
     * this entity's collision shape.
     *
     * @return the bounding rectangle; must not be null
     */
    Rectangle getBounds();

    /**
     * called when this entity has been found to overlap with another.
     *
     * @param other the collidable that this entity collided with
     */
    void onCollision(ICollidable other);
}
