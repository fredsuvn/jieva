package xyz.fsgik.common.base;

/**
 * Functional interface to create string.
 *
 * @author fredsuvn
 */
@FunctionalInterface
public interface StringFunction {

    /**
     * Returns sub-string of given char sequence from given start index inclusive to given end index exclusive.
     *
     * @param chars given char sequence
     * @param start given start index inclusive
     * @param end   given end index exclusive
     */
    CharSequence apply(CharSequence chars, int start, int end);
}
