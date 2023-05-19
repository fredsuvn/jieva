package xyz.srclab.common.base.ref;

/**
 * {@link Ref} for float type.
 *
 * @author fredsuvn
 */
public class FloatRef {

    private float value;

    /**
     * Constructs with 0.
     */
    public FloatRef() {
        this(0);
    }

    /**
     * Constructs with given value.
     *
     * @param value given value
     */
    public FloatRef(float value) {
        this.value = value;
    }

    /**
     * Returns value.
     */
    public float get() {
        return value;
    }

    /**
     * Sets value
     *
     * @param value value
     */
    public void set(float value) {
        this.value = value;
    }

    /**
     * Adds 1 for current value then return:
     *
     * <pre>
     *     return ++value;
     * </pre>
     */
    public float incrementAndGet() {
        return ++value;
    }

    /**
     * Adds specified number for current value then return:
     *
     * <pre>
     *     value += addon;
     *     return value;
     * </pre>
     *
     * @param addon specified number
     */
    public float incrementAndGet(float addon) {
        value += addon;
        return value;
    }

    /**
     * Returns current value then adds 1 for the value:
     *
     * <pre>
     *     return value++;
     * </pre>
     */
    public float getAndIncrement() {
        return value++;
    }

    /**
     * Returns current value then adds specified number for the value:
     *
     * <pre>
     *     float temp = value;
     *     value += addon;
     *     return temp;
     * </pre>
     *
     * @param addon specified number
     */
    public float getAndIncrement(float addon) {
        float temp = value;
        value += addon;
        return temp;
    }
}
