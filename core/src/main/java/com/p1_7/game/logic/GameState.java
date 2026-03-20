package com.p1_7.game.logic;

/**
 * mutable tracker for the player's score, health, level, and win/lose flags.
 *
 * all mutation methods are no-ops once the game has ended (gameOver or gameWon).
 * reset() restores all fields to their initial values so a single instance can
 * be reused across play sessions.
 */
public class GameState {

    /** the highest level number; advancing past this sets gameWon */
    public static final int MAX_LEVEL = 3;

    /** the player's current score */
    private int score = 0;

    /** the player's remaining health points */
    private int health = 3;

    /** the current dungeon level */
    private int level = 1;

    /** true once the player's health reaches zero */
    private boolean gameOver = false;

    /** true once the player clears the final level */
    private boolean gameWon = false;

    /**
     * adds the given number of points to the player's score.
     *
     * this method is a no-op if the game has already ended.
     *
     * @param points the number of points to add
     */
    public void addScore(int points) {
        if (gameOver || gameWon) {
            return;
        }
        score += points;
    }

    /**
     * decrements the player's health by one.
     *
     * sets gameOver to true if health reaches zero.
     * this method is a no-op if the game has already ended.
     */
    public void removeHealth() {
        if (gameOver || gameWon) {
            return;
        }
        health--;
        if (health <= 0) {
            gameOver = true;
        }
    }

    /**
     * increments the current level by one.
     *
     * sets gameWon to true if the new level exceeds MAX_LEVEL.
     * this method is a no-op if the game has already ended.
     */
    public void advanceLevel() {
        if (gameOver || gameWon) {
            return;
        }
        level++;
        if (level > MAX_LEVEL) {
            gameWon = true;
        }
    }

    /**
     * resets all fields to their initial values.
     *
     * allows the same instance to be reused for a new play session.
     */
    public void reset() {
        score    = 0;
        health   = 3;
        level    = 1;
        gameOver = false;
        gameWon  = false;
    }

    /**
     * returns the player's current score.
     *
     * @return the score
     */
    public int getScore() {
        return score;
    }

    /**
     * returns the player's remaining health.
     *
     * @return the health
     */
    public int getHealth() {
        return health;
    }

    /**
     * returns the current dungeon level.
     *
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * returns whether the game has ended in a loss.
     *
     * @return true if the player's health has reached zero
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * returns whether the player has won the game.
     *
     * @return true if the player has cleared all levels
     */
    public boolean isGameWon() {
        return gameWon;
    }
}
