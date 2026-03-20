package com.p1_7.game.question;

/**
 * the arithmetic operator used in a question.
 *
 * each constant carries a display symbol suitable for rendering in the UI.
 */
public enum Operator {

    /** addition operator */
    ADD("+"),

    /** subtraction operator */
    SUBTRACT("-"),

    /** multiplication operator */
    MULTIPLY("×"),

    /** division operator */
    DIVIDE("÷");

    /** the display symbol for this operator */
    public final String symbol;

    /**
     * @param symbol the display symbol for this operator
     */
    Operator(String symbol) {
        this.symbol = symbol;
    }
}
