package com.p1_7.game.logic;

/**
 * stateless service that evaluates a player's room choice against the current game state.
 *
 * a correct choice awards points scaled by the current level; an incorrect choice
 * costs the player one health point.
 */
public class AnswerEvaluator {

    /**
     * evaluates the player's chosen room and mutates the game state accordingly.
     *
     * if the chosen room has a label and the label is correct, the player's score
     * increases by 10 * current level. otherwise, the player loses one health point.
     *
     * @param chosen the labelled room selected by the player; must not be null
     * @param state  the current game state to mutate; must not be null
     * @return true if the chosen room was correct, false otherwise
     * @throws IllegalArgumentException if chosen or state is null
     */
    public boolean evaluate(LabelledRoom chosen, GameState state) {
        if (chosen == null) {
            throw new IllegalArgumentException("chosen must not be null");
        }
        if (state == null) {
            throw new IllegalArgumentException("state must not be null");
        }

        if (chosen.hasLabel() && chosen.getLabel().isCorrect()) {
            // award points proportional to the current level
            state.addScore(10 * state.getLevel());
            return true;
        }

        // incorrect choice — penalise the player
        state.removeHealth();
        return false;
    }
}
