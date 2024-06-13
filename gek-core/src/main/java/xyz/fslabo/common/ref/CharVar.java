package xyz.fslabo.common.ref;

/**
 * Char version of {@link Var}.
 *
 * @author fredsuvn
 */
public interface CharVar extends CharVal {

    /**
     * Returns an instance of {@link CharVar} of 0 initialized value.
     *
     * @return an instance of {@link CharVar} of 0 initialized value
     */
    static CharVar ofZero() {
        return of((char) 0);
    }

    /**
     * Returns an instance of {@link CharVar} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link CharVar} of initialized value
     */
    static CharVar of(char value) {
        return VarImpls.ofChar(value);
    }

    /**
     * Sets and returns specified value.
     *
     * @param value specified value
     * @return specified value
     */
    char set(char value);

    /**
     * Returns {@link Var} version with current value.
     *
     * @return {@link Var} version with current value
     */
    default Var<Character> toWrapper() {
        return Var.of(get());
    }
}
