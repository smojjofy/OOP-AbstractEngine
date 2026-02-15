package com.p1_7.demo.display;

import com.p1_7.demo.Settings;

/**
 * text entity displaying remaining lives count.
 *
 * extends basetextdisplay to leverage shared text rendering logic.
 * positioned at the top-left corner of the screen.
 */
public class LivesDisplay extends BaseTextDisplay {

    /** current lives count */
    private int lives;

    /**
     * constructs a lives display with the specified initial lives.
     *
     * @param initialLives the starting number of lives
     */
    public LivesDisplay(int initialLives) {
        // position at top-left corner with 100x20 size and 1.0 scale
        super(10f, Settings.WINDOW_HEIGHT - 10f, 100f, 20f, 1.0f);
        this.lives = initialLives;
    }

    /**
     * sets the current lives count.
     *
     * @param lives the new lives value
     */
    public void setLives(int lives) {
        this.lives = lives;
    }

    /**
     * returns the current lives count.
     *
     * @return the number of lives remaining
     */
    public int getLives() {
        return lives;
    }

    /**
     * returns the formatted text to display.
     *
     * @return the lives text string
     */
    @Override
    public String getText() {
        return "Lives: " + lives;
    }
}
