package com.p1_7.abstractengine.collision;

import com.badlogic.gdx.utils.Array;

import com.p1_7.abstractengine.engine.UpdatableManager;

/**
 * abstract per-frame manager that tests all registered {@link ICollidable}
 * entities for pairwise overlap using a two-phase architecture:
 * detection followed by resolution.
 *
 * <p>Entities must be explicitly registered via
 * {@link #registerCollidable(ICollidable)}. The detection phase uses a
 * stateless {@link CollisionDetector} and iterates unique pairs (O(n²))
 * to identify all collisions in the current frame. The resolution phase
 * processes these detected collisions according to the strategy implemented
 * by concrete subclasses.</p>
 *
 * <p>Subclasses can override {@link #detect()} to implement optimised
 * detection algorithms (spatial partitioning, grid-based, etc.) and must
 * implement {@link #resolve(Array)} to define collision resolution behaviour
 * (callbacks, physics impulses, layered filtering, etc.).</p>
 */
public abstract class CollisionManager extends UpdatableManager {

    /** all collidable entities managed by this manager */
    private final Array<ICollidable> collidables = new Array<>();

    /** stateless detector that performs the overlap test */
    private final CollisionDetector detector = new CollisionDetector();

    /** detected collisions from the current frame */
    private final Array<CollisionPair> detectedCollisions = new Array<>();

    // ---------------------------------------------------------------
    // registration
    // ---------------------------------------------------------------

    /**
     * Adds an {@link ICollidable} to the detection list.
     *
     * @param collidable the collidable entity to register
     */
    public void registerCollidable(ICollidable collidable) {
        collidables.add(collidable);
    }

    /**
     * Removes an {@link ICollidable} from the detection list.
     *
     * @param collidable the collidable entity to unregister
     */
    public void unregisterCollidable(ICollidable collidable) {
        collidables.removeValue(collidable, true);
    }

    // ---------------------------------------------------------------
    // UpdatableManager hook
    // ---------------------------------------------------------------

    /**
     * runs collision detection and resolution in two phases:
     * first detects all collisions, then resolves them.
     *
     * @param deltaTime seconds elapsed since the previous frame
     */
    @Override
    protected void onUpdate(float deltaTime) {
        detect();
        resolve(detectedCollisions);
    }

    // ---------------------------------------------------------------
    // detection & resolution
    // ---------------------------------------------------------------

    /**
     * detects all collisions by iterating unique pairs {@code (i, j)} where
     * {@code i < j}. detected collisions are stored in {@link #detectedCollisions}
     * for processing by {@link #resolve(Array)}.
     *
     * <p>this method clears the previous frame's collisions before detection.
     * subclasses can override this method to implement optimised detection
     * algorithms (spatial partitioning, quadtrees, etc.).</p>
     */
    protected void detect() {
        detectedCollisions.clear();
        for (int i = 0; i < collidables.size - 1; i++) {
            ICollidable a = collidables.get(i);
            for (int j = i + 1; j < collidables.size; j++) {
                ICollidable b = collidables.get(j);
                if (detector.checkCollision(a, b)) {
                    detectedCollisions.add(new CollisionPair(a, b));
                }
            }
        }
    }

    /**
     * resolves detected collisions according to the collision resolution
     * strategy implemented by concrete subclasses.
     *
     * <p>examples of resolution strategies include:
     * <ul>
     *   <li>callback-based: invoke {@link ICollidable#onCollision(ICollidable)} on both entities</li>
     *   <li>physics-based: apply impulses or forces to separate entities</li>
     *   <li>layered: filter collisions based on entity groups or categories</li>
     *   <li>batched: group collisions by type and handle differently</li>
     * </ul>
     *
     * @param collisions the array of detected collision pairs from this frame
     */
    protected abstract void resolve(Array<CollisionPair> collisions);
}
