package xyz.fsgik.common.base.ref;

/**
 * {@link FsRef} for char type.
 *
 * @author fredsuvn
 */
public class CharRef {

    private char value;

    CharRef(char value) {
        this.value = value;
    }

    /**
     * Returns value.
     */
    public char get() {
        return value;
    }

    /**
     * Sets value
     *
     * @param value value
     */
    public void set(char value) {
        this.value = value;
    }

    /**
     * Adds 1 for current value then return:
     *
     * <pre>
     *     return ++value;
     * </pre>
     */
    public char incrementAndGet() {
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
    public char incrementAndGet(char addon) {
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
    public char getAndIncrement() {
        return value++;
    }

    /**
     * Returns current value then adds specified number for the value:
     *
     * <pre>
     *     char temp = value;
     *     value += addon;
     *     return temp;
     * </pre>
     *
     * @param addon specified number
     */
    public char getAndIncrement(char addon) {
        char temp = value;
        value += addon;
        return temp;
    }
}
