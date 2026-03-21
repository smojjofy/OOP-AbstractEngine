package com.p1_7.game.gameplay;

/**
 * difficulty levels available in the math maze game.
 *
 * each constant maps to an inclusive operand range used by the question generator
 * to control how large the numbers in arithmetic questions can be.
 */
public enum Difficulty {

    /** easy difficulty — operands drawn from 1 to 10 inclusive */
    EASY(1, 10),

    /** medium difficulty — operands drawn from 1 to 20 inclusive */
    MEDIUM(1, 20),

    /** hard difficulty — operands drawn from 1 to 100 inclusive */
    HARD(1, 100);

    /** the smallest operand value that may appear in a question at this difficulty */
    private final int minOperand;

    /** the largest operand value that may appear in a question at this difficulty */
    private final int maxOperand;

    /**
     * constructs a difficulty level with the given operand range.
     *
     * @param minOperand the inclusive lower bound for operands
     * @param maxOperand the inclusive upper bound for operands
     */
    Difficulty(int minOperand, int maxOperand) {
        if (minOperand > maxOperand) {
            throw new IllegalArgumentException(
                "minOperand (" + minOperand + ") must be <= maxOperand (" + maxOperand + ")");
        }
        this.minOperand = minOperand;
        this.maxOperand = maxOperand;
    }

    /**
     * returns the smallest operand value for this difficulty.
     *
     * @return inclusive lower bound for operands
     */
    public int getMinOperand() {
        return minOperand;
    }

    /**
     * returns the largest operand value for this difficulty.
     *
     * @return inclusive upper bound for operands
     */
    public int getMaxOperand() {
        return maxOperand;
    }
}
