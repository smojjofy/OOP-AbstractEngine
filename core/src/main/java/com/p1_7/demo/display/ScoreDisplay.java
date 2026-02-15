package com.p1_7.demo.display;

/**
 * text entity displaying current score.
 *
 * extends basetextdisplay to leverage shared text rendering logic.
 */
public class ScoreDisplay extends BaseTextDisplay {

    /** current score */
    private int score;

    /**
     * constructs a score display with the specified initial score and position.
     *
     * @param x the x position (left edge)
     * @param y the y position (baseline)
     * @param initialScore the starting score
     */
    public ScoreDisplay(float x, float y, int initialScore) {
        // call baseclass constructor with 100x20 size and 1.0 scale
        super(x, y, 100f, 20f, 1.0f);
        this.score = initialScore;
    }

    /**
     * sets the current score.
     *
     * @param score the new score value
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * returns the current score.
     *
     * @return the score value
     */
    public int getScore() {
        return score;
    }

    /**
     * returns the formatted text to display.
     *
     * @return the score text string
     */
    @Override
    public String getText() {
        return "Score: " + score;
    }
}
