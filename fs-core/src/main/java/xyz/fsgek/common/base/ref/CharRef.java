package xyz.fsgek.common.base.ref;

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
     * Returns value of this ref.
     *
     * @return value of this ref
     */
    public char get() {
        return value;
    }

    /**
     * Sets value of this ref.
     *
     * @param value value of this ref
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
     *
     * @return ++value
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
     * @return value += addon
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
     *
     * @return value++
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
     * @return current value
     */
    public char getAndIncrement(char addon) {
        char temp = value;
        value += addon;
        return temp;
    }
}
