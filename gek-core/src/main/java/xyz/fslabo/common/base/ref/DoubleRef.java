package xyz.fslabo.common.base.ref;

/**
 * {@link GekRef} for double type.
 *
 * @author fredsuvn
 */
public interface DoubleRef {

    /**
     * Returns an instance of {@link DoubleRef} of 0 initialized value.
     *
     * @return an instance of {@link DoubleRef} of 0 initialized value
     */
    static DoubleRef ofZero() {
        return of(0);
    }

    /**
     * Returns an instance of {@link DoubleRef} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link DoubleRef} of initialized value
     */
    static DoubleRef of(double value) {
        return Impls.newDoubleRef(value);
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
}
