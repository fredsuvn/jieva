package xyz.fsgik.common.base.ref;

import xyz.fsgik.annotations.Nullable;

/**
 * Denote a variable reference for an object, usually be used where the variable cannot be re-assigned.
 * For example:
 *
 * <pre>
 *     String str = "a";
 *     FsRef&lt;String> ref = FsRef.of("a");
 *     map.computeIfAbsent(key, it -> {
 *         str = "other"; //error: cannot re-assigned variable str!
 *         ref.set("other");
 *         //...
 *     });
 * </pre>
 *
 * @author fredsuvn
 */
public class FsRef<T> {

    private T value;

    private FsRef(@Nullable T value) {
        this.value = value;
    }

    /**
     * Return a reference instance with null value.
     *
     * @return a reference instance with null value
     */
    public static <T> FsRef<T> ofNull() {
        return of(null);
    }

    /**
     * Return a reference instance with given value.
     *
     * @param value given value
     * @return a reference instance with given value
     */
    public static <T> FsRef<T> of(@Nullable T value) {
        return new FsRef<>(value);
    }

    /**
     * Return a reference instance with given boolean value.
     *
     * @param value given boolean value
     * @return a reference instance with given boolean value
     */
    public static BooleanRef ofBoolean(boolean value) {
        return new BooleanRef(value);
    }

    /**
     * Return a reference instance with given byte value.
     *
     * @param value given byte value
     * @return a reference instance with given byte value
     */
    public static ByteRef ofByte(byte value) {
        return new ByteRef(value);
    }

    /**
     * Return a reference instance with given short value.
     *
     * @param value given short value
     * @return a reference instance with given short value
     */
    public static ShortRef ofShort(short value) {
        return new ShortRef(value);
    }

    /**
     * Return a reference instance with given char value.
     *
     * @param value given char value
     * @return a reference instance with given char value
     */
    public static CharRef ofChar(char value) {
        return new CharRef(value);
    }

    /**
     * Return a reference instance with given int value.
     *
     * @param value given int value
     * @return a reference instance with given int value
     */
    public static IntRef ofInt(int value) {
        return new IntRef(value);
    }

    /**
     * Return a reference instance with given long value.
     *
     * @param value given long value
     * @return a reference instance with given long value
     */
    public static LongRef ofLong(long value) {
        return new LongRef(value);
    }

    /**
     * Return a reference instance with given float value.
     *
     * @param value given float value
     * @return a reference instance with given float value
     */
    public static FloatRef ofFloat(float value) {
        return new FloatRef(value);
    }

    /**
     * Return a reference instance with given double value.
     *
     * @param value given double value
     * @return a reference instance with given double value
     */
    public static DoubleRef ofDouble(double value) {
        return new DoubleRef(value);
    }

    /**
     * Returns value of this ref.
     *
     * @return value of this ref
     */
    @Nullable
    public T get() {
        return value;
    }

    /**
     * Sets value of this ref.
     *
     * @param value value of this ref
     */
    public void set(@Nullable T value) {
        this.value = value;
    }
}
