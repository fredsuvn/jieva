package xyz.fslabo.common.ref;

/**
 * Char version of {@link Var}.
 *
 * @author fredsuvn
 */
public interface CharVar {

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
        return VarImpls.newCharVar(value);
    }

    /**
     * Returns referenced value.
     *
     * @return referenced value
     */
    char get();

    /**
     * Sets referenced value.
     *
     * @param value referenced value
     */
    void set(char value);

    /**
     * Returns {@link Var} version with current value.
     *
     * @return {@link Var} version with current value
     */
    default Var<Character> toVar() {
        return Var.of(get());
    }
}
