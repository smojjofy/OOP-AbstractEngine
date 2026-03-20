package com.p1_7.game.logic;

import com.p1_7.game.dungeon.Room;

/**
 * an immutable wrapper that pairs a dungeon room with an optional answer label.
 *
 * a room with no label is the spawn room; all other rooms carry a RoomLabel
 * that the player must evaluate to progress.
 */
public class LabelledRoom {

    /** the underlying dungeon room */
    private final Room room;

    /** the answer label assigned to this room, or null for the spawn room */
    private final RoomLabel label;

    /**
     * constructs a labelled room wrapping the given room and optional label.
     *
     * @param room  the dungeon room to wrap; must not be null
     * @param label the answer label, or null if this room is the spawn
     * @throws IllegalArgumentException if room is null
     */
    public LabelledRoom(Room room, RoomLabel label) {
        if (room == null) {
            throw new IllegalArgumentException("room must not be null");
        }
        this.room  = room;
        this.label = label;
    }

    /**
     * returns whether this room has an answer label.
     *
     * @return true if a label is assigned, false if this is the spawn room
     */
    public boolean hasLabel() {
        return label != null;
    }

    /**
     * returns whether this room is the spawn room (i.e. has no label).
     *
     * @return true if this room is the spawn room, false otherwise
     */
    public boolean isSpawn() {
        return !hasLabel();
    }

    /**
     * returns the underlying dungeon room.
     *
     * @return the room
     */
    public Room getRoom() {
        return room;
    }

    /**
     * returns the answer label for this room.
     *
     * @return the label, or null if this is the spawn room
     */
    public RoomLabel getLabel() {
        return label;
    }
}
