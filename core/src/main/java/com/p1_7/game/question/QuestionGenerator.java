package com.p1_7.game.question;

import java.util.Random;

/**
 * stateless service that generates a randomised arithmetic Question at a given difficulty level.
 *
 * the generator holds no fields; all mutable state is scoped to the generate method.
 * supplying the same level and rng seed always produces an identical question.
 */
public class QuestionGenerator {

    /**
     * generates a randomised arithmetic question at the given difficulty level.
     *
     * the operator pool and operand range expand with level:
     * level 1 uses ADD and SUBTRACT with operands in [2, 10];
     * level 2 adds MULTIPLY with operands in [2, 15];
     * level 3 and above adds DIVIDE with operands in [5, 20].
     * levels below 1 are treated as level 1.
     *
     * @param level difficulty level; values below 1 are clamped to 1
     * @param rng   random number generator to use; must not be null
     * @return a freshly generated Question matching the level's constraints
     * @throws IllegalArgumentException if rng is null
     */
    public Question generate(int level, Random rng) {
        if (rng == null) {
            throw new IllegalArgumentException("rng must not be null");
        }

        int effectiveLevel = Math.max(1, level);

        Operator[] pool    = resolveOperatorPool(effectiveLevel);
        int[]      range   = resolveRange(effectiveLevel);
        Operator   chosenOp = pool[rng.nextInt(pool.length)];

        if (chosenOp == Operator.DIVIDE) {
            return generateDivide(rng);
        }
        return generateStandard(chosenOp, range[0], range[1], rng);
    }

    /**
     * returns the operator pool for the given (already clamped) level.
     *
     * @param level the clamped difficulty level (minimum 1)
     * @return an array of operators available at this level
     */
    private Operator[] resolveOperatorPool(int level) {
        if (level == 1) {
            return new Operator[]{ Operator.ADD, Operator.SUBTRACT };
        }
        if (level == 2) {
            return new Operator[]{ Operator.ADD, Operator.SUBTRACT, Operator.MULTIPLY };
        }
        return new Operator[]{ Operator.ADD, Operator.SUBTRACT, Operator.MULTIPLY, Operator.DIVIDE };
    }

    /**
     * returns the operand range [min, max] for the given (already clamped) level.
     *
     * @param level the clamped difficulty level (minimum 1)
     * @return a two-element array where index 0 is rangeMin and index 1 is rangeMax
     */
    private int[] resolveRange(int level) {
        if (level == 1) {
            return new int[]{ 2, 10 };
        }
        if (level == 2) {
            return new int[]{ 2, 15 };
        }
        return new int[]{ 5, 20 };
    }

    /**
     * generates a DIVIDE question that always produces a whole-number answer.
     *
     * a divisor b is picked in [1, 12] and a multiplier in [2, 12]; the dividend
     * a is set to b * multiplier so a / b is always exact.
     *
     * @param rng random number generator to use
     * @return a Question with operator DIVIDE and a whole-number answer
     */
    private Question generateDivide(Random rng) {
        // start divisor at 2 to avoid trivial "n ÷ 1 = n" questions
        int b          = 2 + rng.nextInt(11);
        int multiplier = 2 + rng.nextInt(11);
        int a          = b * multiplier;
        int answer     = a / b;

        String displayText = buildDisplayText(a, b, Operator.DIVIDE);
        return new Question(displayText, answer, Operator.DIVIDE, new int[]{ a, b });
    }

    /**
     * generates a question for ADD, SUBTRACT, or MULTIPLY.
     *
     * operands are picked uniformly from [rangeMin, rangeMax]. for SUBTRACT,
     * operands are swapped if needed to ensure the answer is non-negative.
     *
     * @param op       the operator to use
     * @param rangeMin minimum operand value (inclusive)
     * @param rangeMax maximum operand value (inclusive)
     * @param rng      random number generator to use
     * @return a Question with the given operator
     */
    private Question generateStandard(Operator op, int rangeMin, int rangeMax, Random rng) {
        int span = rangeMax - rangeMin + 1;
        int a    = rangeMin + rng.nextInt(span);
        int b    = rangeMin + rng.nextInt(span);

        // ensure a >= b for subtraction so the answer is never negative
        if (op == Operator.SUBTRACT && a < b) {
            int tmp = a;
            a = b;
            b = tmp;
        }

        int answer;
        switch (op) {
            case ADD:      answer = a + b; break;
            case SUBTRACT: answer = a - b; break;
            default:       answer = a * b; break;  // MULTIPLY
        }

        String displayText = buildDisplayText(a, b, op);
        return new Question(displayText, answer, op, new int[]{ a, b });
    }

    /**
     * formats the display text for a question.
     *
     * @param a  the first operand
     * @param b  the second operand
     * @param op the operator
     * @return a string of the form "a <symbol> b = ?"
     */
    private String buildDisplayText(int a, int b, Operator op) {
        return a + " " + op.symbol + " " + b + " = ?";
    }
}
