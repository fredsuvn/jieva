package xyz.fslabo.common.ref;

/**
 * Double version of {@link Var}.
 *
 * @author fredsuvn
 */
public interface DoubleVar extends DoubleVal {

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
        return VarImpls.ofDouble(value);
    }

    /**
     * Sets and returns specified value.
     *
     * @param value specified value
     * @return specified value
     */
    double set(double value);

    /**
     * Adds current value by specified value, and returns the result.
     *
     * @param value specified value
     * @return the result
     */
    double add(double value);

    /**
     * Returns {@link Var} version with current value.
     *
     * @return {@link Var} version with current value
     */
    default Var<Double> toWrapper() {
        return Var.of(get());
    }
}
