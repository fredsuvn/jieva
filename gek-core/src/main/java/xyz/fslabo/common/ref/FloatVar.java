package xyz.fslabo.common.ref;

/**
 * Float version of {@link Var}.
 *
 * @author fredsuvn
 */
public interface FloatVar extends FloatVal {

    /**
     * Returns an instance of {@link FloatVar} of 0 initialized value.
     *
     * @return an instance of {@link FloatVar} of 0 initialized value
     */
    static FloatVar ofZero() {
        return of(0);
    }

    /**
     * Returns an instance of {@link FloatVar} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link FloatVar} of initialized value
     */
    static FloatVar of(float value) {
        return VarImpls.ofFloat(value);
    }

    /**
     * Sets and returns specified value.
     *
     * @param value specified value
     * @return specified value
     */
    float set(float value);

    /**
     * Adds current value by specified value, and returns the result.
     *
     * @param value specified value
     * @return the result
     */
    float add(float value);

    /**
     * Returns {@link Var} version with current value.
     *
     * @return {@link Var} version with current value
     */
    default Var<Float> toWrapper() {
        return Var.of(get());
    }
}
