package com.p1_7.game.question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

/**
 * stateless service that generates plausible decoy answers for a given Question.
 *
 * the generator holds no fields; all mutable state is scoped to the generate method.
 * always returns exactly 3 distinct positive integers, none equal to the correct answer.
 */
public class DecoyGenerator {

    /**
     * generates exactly 3 distinct positive decoy answers for the given question parameters.
     *
     * for DIVIDE questions, decoys are nearby multiples of the divisor so they remain
     * plausible. for all other operators, decoys are small integer offsets from the
     * correct answer. all decoys are strictly positive and distinct from each other
     * and from the correct answer.
     *
     * @param correct  the correct answer to exclude from the decoy list
     * @param operator the operator used in the question; must not be null
     * @param operands the operands used in the question; must not be null;
     *                 for DIVIDE, must have at least 2 elements
     * @param rng      random number generator; must not be null
     * @return an unmodifiable list of exactly 3 distinct positive integers, none equal to correct
     * @throws IllegalArgumentException if operator, operands, or rng is null, or if operator
     *                                  is DIVIDE and operands has fewer than 2 elements
     */
    public List<Integer> generate(int correct, Operator operator, int[] operands, Random rng) {
        if (operator == null) {
            throw new IllegalArgumentException("operator must not be null");
        }
        if (operands == null) {
            throw new IllegalArgumentException("operands must not be null");
        }
        if (rng == null) {
            throw new IllegalArgumentException("rng must not be null");
        }
        if (operator == Operator.DIVIDE && operands.length < 2) {
            throw new IllegalArgumentException("operands must have at least 2 elements for DIVIDE");
        }

        List<Integer> candidates;
        if (operator == Operator.DIVIDE) {
            candidates = buildDivideCandidates(correct, operands);
        } else {
            candidates = buildOffsetCandidates(correct, 8);
        }

        List<Integer> decoys = collectDecoys(candidates, correct, 3);

        // fallback: extend the offset walk until we have 3 decoys
        if (decoys.size() < 3) {
            candidates = extendCandidates(candidates, correct, 3);
            decoys = collectDecoys(candidates, correct, 3);
        }

        return Collections.unmodifiableList(decoys);
    }

    /**
     * builds decoy candidates from nearby multiples of the divisor.
     *
     * candidates are: correct ± b, correct + 2b, correct + 3b, correct - 2b,
     * where b is the divisor (operands[1]).
     *
     * @param correct  the correct answer
     * @param operands the question operands; operands[1] is the divisor
     * @return a list of candidate decoy values (may include non-positive values, filtered later)
     */
    private List<Integer> buildDivideCandidates(int correct, int[] operands) {
        int b = operands[1];
        List<Integer> candidates = new ArrayList<>();
        candidates.add(correct + b);
        candidates.add(correct - b);
        candidates.add(correct + 2 * b);
        candidates.add(correct + 3 * b);
        candidates.add(correct - 2 * b);
        return candidates;
    }

    /**
     * builds decoy candidates by walking outward from the correct answer in integer offsets.
     *
     * iterates offsets 1, 2, 3, … adding correct+offset then correct-offset until
     * the requested number of raw candidates is collected.
     *
     * @param correct the correct answer
     * @param count   the number of raw candidates to generate before stopping
     * @return a list of raw candidate values in outward offset order
     */
    private List<Integer> buildOffsetCandidates(int correct, int count) {
        List<Integer> candidates = new ArrayList<>();
        int offset = 1;
        while (candidates.size() < count) {
            candidates.add(correct + offset);
            if (candidates.size() < count) {
                candidates.add(correct - offset);
            }
            offset++;
        }
        return candidates;
    }

    /**
     * filters candidates and collects up to the requested number of valid decoys.
     *
     * a candidate is accepted if it is strictly positive, not equal to correct, and not
     * already chosen. a LinkedHashSet is used to preserve insertion order and prevent duplicates.
     *
     * @param candidates the pool of raw candidate values to filter
     * @param correct    the correct answer to exclude
     * @param needed     the number of decoys to collect
     * @return a list of accepted decoys, at most needed elements
     */
    private List<Integer> collectDecoys(List<Integer> candidates, int correct, int needed) {
        LinkedHashSet<Integer> chosen = new LinkedHashSet<>();
        for (int candidate : candidates) {
            if (chosen.size() == needed) {
                break;
            }
            if (candidate > 0 && candidate != correct) {
                chosen.add(candidate);
            }
        }
        return new ArrayList<>(chosen);
    }

    /**
     * extends the candidate list by walking further outward until it contains at least needed entries.
     *
     * the starting offset is derived from the current list size to avoid revisiting values.
     *
     * @param existing the candidate list to extend in place
     * @param correct  the correct answer (excluded from extension candidates)
     * @param needed   the minimum number of valid decoys required
     * @return the extended candidate list (same reference as existing)
     */
    private List<Integer> extendCandidates(List<Integer> existing, int correct, int needed) {
        // start beyond the initial offset range already covered
        int offset = existing.size() + 1;
        while (collectDecoys(existing, correct, needed).size() < needed) {
            existing.add(correct + offset);
            existing.add(correct - offset);
            offset++;
        }
        return existing;
    }
}
