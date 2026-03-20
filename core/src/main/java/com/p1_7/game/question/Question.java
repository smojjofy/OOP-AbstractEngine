package com.p1_7.game.question;

import java.util.Arrays;

/**
 * an immutable value object representing a single arithmetic question.
 *
 * the operands array is defensively copied on construction and on access
 * so the caller cannot mutate the internal state after creation.
 */
public class Question {

    /** the text shown to the player, e.g. "3 + 4 = ?" */
    private final String displayText;

    /** the correct numeric answer */
    private final int correctAnswer;

    /** the arithmetic operator used in this question */
    private final Operator operator;

    /** the two operands; stored as [a, b] matching the display order */
    private final int[] operands;

    /**
     * constructs a question with the given display text, answer, operator, and operands.
     *
     * @param displayText   the text shown to the player; must not be null
     * @param correctAnswer the correct numeric answer
     * @param operator      the arithmetic operator; must not be null
     * @param operands      the two operands in display order; must not be null
     * @throws IllegalArgumentException if displayText, operator, or operands is null
     */
    public Question(String displayText, int correctAnswer, Operator operator, int[] operands) {
        if (displayText == null) {
            throw new IllegalArgumentException("displayText must not be null");
        }
        if (operator == null) {
            throw new IllegalArgumentException("operator must not be null");
        }
        if (operands == null) {
            throw new IllegalArgumentException("operands must not be null");
        }
        this.displayText   = displayText;
        this.correctAnswer = correctAnswer;
        this.operator      = operator;
        this.operands      = Arrays.copyOf(operands, operands.length);
    }

    /**
     * returns the display text for this question.
     *
     * @return the display text, e.g. "3 + 4 = ?"
     */
    public String getDisplayText() {
        return displayText;
    }

    /**
     * returns the correct answer to this question.
     *
     * @return the correct numeric answer
     */
    public int getCorrectAnswer() {
        return correctAnswer;
    }

    /**
     * returns the arithmetic operator used in this question.
     *
     * @return the operator
     */
    public Operator getOperator() {
        return operator;
    }

    /**
     * returns a copy of the operands array.
     *
     * a defensive copy is returned so callers cannot mutate the internal state.
     *
     * @return a copy of the operands in display order, e.g. [a, b]
     */
    public int[] getOperands() {
        return Arrays.copyOf(operands, operands.length);
    }
}
