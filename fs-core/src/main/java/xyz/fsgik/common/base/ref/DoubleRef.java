package xyz.fsgik.common.base.ref;

/**
 * {@link FsRef} for double type.
 *
 * @author fredsuvn
 */
public class DoubleRef {

    private double value;

    DoubleRef(double value) {
        this.value = value;
    }

    /**
     * Returns value.
     */
    public double get() {
        return value;
    }

    /**
     * Sets value
     *
     * @param value value
     */
    public void set(double value) {
        this.value = value;
    }

    /**
     * Adds 1 for current value then return:
     *
     * <pre>
     *     return ++value;
     * </pre>
     */
    public double incrementAndGet() {
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
    public double incrementAndGet(double addon) {
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
    public double getAndIncrement() {
        return value++;
    }

    /**
     * Returns current value then adds specified number for the value:
     *
     * <pre>
     *     double temp = value;
     *     value += addon;
     *     return temp;
     * </pre>
     *
     * @param addon specified number
     */
    public double getAndIncrement(double addon) {
        double temp = value;
        value += addon;
        return temp;
    }
}
