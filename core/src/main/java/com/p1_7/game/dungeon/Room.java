package com.p1_7.game.dungeon;

/**
 * an immutable rectangular room placed within a dungeon grid.
 *
 * both width and height must be odd so corridors can connect
 * to the exact centre cell via integer division.
 */
public class Room {

    /** grid column of the room's top-left corner */
    public final int x;

    /** grid row of the room's top-left corner */
    public final int y;

    /** number of columns; must be odd */
    public final int width;

    /** number of rows; must be odd */
    public final int height;

    /**
     * constructs a room at the given position with the given dimensions.
     *
     * @param x      grid column of the top-left corner
     * @param y      grid row of the top-left corner
     * @param width  number of columns (must be odd)
     * @param height number of rows (must be odd)
     * @throws IllegalArgumentException if width or height is even
     */
    public Room(int x, int y, int width, int height) {
        if (width % 2 == 0 || height % 2 == 0) {
            throw new IllegalArgumentException("room dimensions must be odd; got " + width + "x" + height);
        }
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * returns the centre cell of this room.
     *
     * integer division on odd dimensions always lands on the exact centre cell.
     *
     * @return a two-element array where index 0 is the centre column and index 1 is the centre row
     */
    public int[] centre() {
        return new int[]{ x + width / 2, y + height / 2 };
    }

    /**
     * checks whether this room's bounding box overlaps another room's bounding box,
     * expanded by margin cells on each side.
     *
     * @param other  the room to test against
     * @param margin cells of expansion applied to each room's bounding box on each side;
     *               a gap of more than 2*margin cells is required for the method to return false
     * @return true if the rooms overlap including the margin
     */
    public boolean overlaps(Room other, int margin) {
        return x - margin < other.x + other.width  + margin
            && x + width  + margin > other.x - margin
            && y - margin < other.y + other.height + margin
            && y + height + margin > other.y - margin;
    }
}
