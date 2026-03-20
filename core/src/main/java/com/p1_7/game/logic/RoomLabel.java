package com.p1_7.game.logic;

/**
 * an immutable value object that labels a dungeon room with an answer number.
 *
 * carries the displayed answer value and a flag indicating whether it is
 * the correct answer to the current question.
 */
public class RoomLabel {

    /** the answer number displayed on the room */
    public final int value;

    /** true if this label represents the correct answer */
    public final boolean isCorrect;

    /**
     * constructs a room label with the given answer value and correctness flag.
     *
     * @param value     the answer number to display
     * @param isCorrect true if this is the correct answer, false for a decoy
     */
    public RoomLabel(int value, boolean isCorrect) {
        this.value     = value;
        this.isCorrect = isCorrect;
    }

    /**
     * returns the answer number displayed on this room.
     *
     * @return the displayed answer value
     */
    public int getValue() {
        return value;
    }

    /**
     * returns whether this label represents the correct answer.
     *
     * @return true if this is the correct answer, false if it is a decoy
     */
    public boolean isCorrect() {
        return isCorrect;
    }
}
