package xyz.fslabo.common.ref;

/**
 * Double version of {@link Var}.
 *
 * @author fredsuvn
 */
public interface DoubleVar {

    /**
     * Returns an instance of {@link DoubleVar} of 0 initialized value.
     *
     * @return an instance of {@link DoubleVar} of 0 initialized value
     */
    static DoubleVar ofZero() {
        return of(0);
    }

    /**
     * Returns an instance of {@link DoubleVar} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link DoubleVar} of initialized value
     */
    static DoubleVar of(double value) {
        return VarImpls.newDoubleVar(value);
    }

    /**
     * Returns referenced value.
     *
     * @return referenced value
     */
    double get();

    /**
     * Sets referenced value.
     *
     * @param value referenced value
     */
    void set(double value);

    /**
     * Adds current value by specified value, and returns the result.
     *
     * @param v specified value
     * @return the result
     */
    double add(double v);

    /**
     * Returns {@link Var} version with current value.
     *
     * @return {@link Var} version with current value
     */
    default Var<Double> toVar() {
        return Var.of(get());
    }
}
