package xyz.fslabo.common.ref;

/**
 * Float version of {@link Var}.
 *
 * @author fredsuvn
 */
public interface FloatVar {

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
        return VarImpls.newFloatVar(value);
    }

    /**
     * Returns referenced value.
     *
     * @return referenced value
     */
    float get();

    /**
     * Sets referenced value.
     *
     * @param value referenced value
     */
    void set(float value);

    /**
     * Adds current value by specified value, and returns the result.
     *
     * @param v specified value
     * @return the result
     */
    float add(float v);

    /**
     * Returns {@link Var} version with current value.
     *
     * @return {@link Var} version with current value
     */
    default Var<Float> toVar() {
        return Var.of(get());
    }
}
