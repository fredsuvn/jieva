package xyz.fslabo.common.base.ref;

/**
 * {@link GekRef} for float type.
 *
 * @author fredsuvn
 */
public interface FloatRef {

    /**
     * Returns an instance of {@link FloatRef} of 0 initialized value.
     *
     * @return an instance of {@link FloatRef} of 0 initialized value
     */
    static FloatRef ofZero() {
        return of(0);
    }

    /**
     * Returns an instance of {@link FloatRef} of initialized value.
     *
     * @param value initialized value
     * @return an instance of {@link FloatRef} of initialized value
     */
    static FloatRef of(float value) {
        return Impls.newFloatRef(value);
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
}
