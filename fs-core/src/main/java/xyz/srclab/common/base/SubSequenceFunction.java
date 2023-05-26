package xyz.srclab.common.base;

/**
 * A functional interface to create sub CharSequence.
 *
 * @author fredsuvn
 */
@FunctionalInterface
public interface SubSequenceFunction {

    /**
     * Returns sub CharSequence of given chars from given start index inclusive to given end index exclusive.
     *
     * @param chars given chars
     * @param start given start index inclusive
     * @param end   given end index exclusive
     */
    CharSequence apply(CharSequence chars, int start, int end);
}
