package xyz.fsgek.common.base.ref;

import xyz.fslabo.annotations.Nullable;

/**
 * Gek Ref is an object which references a value. It is usually be used where a variable cannot be re-assigned.
 * For example:
 * <pre>
 *     String str = "a";
 *     GekRef&lt;String&gt; ref = GekRef.of("a");
 *     map.computeIfAbsent(key, it -&gt; {
 *         str = "other"; //error: cannot re-assigned variable str!
 *         ref.set("other");
 *         //...
 *     });
 * </pre>
 * In simple terms, it is just a container to hold an object. It also has version for primitive types:
 * {@link BooleanRef}, {@link ByteRef}, {@link CharRef}, {@link ShortRef}, {@link IntRef}, {@link LongRef},
 * {@link DoubleRef} and {@link FloatRef}.
 *
 * @param <T> type of referenced value
 * @author fredsuvn
 */
public interface GekRef<T> {

    /**
     * Return an instance of {@link GekRef} of null initialized value.
     *
     * @param <T> type of initialized value
     * @return an instance of {@link GekRef} of null initialized value
     */
    static <T> GekRef<T> ofNull() {
        return of(null);
    }

    /**
     * Returns an instance of {@link GekRef} of initialized value.
     *
     * @param value initialized value
     * @param <T>   type of initialized value
     * @return an instance of {@link GekRef} of initialized value
     */
    static <T> GekRef<T> of(@Nullable T value) {
        return Impls.newGekRef(value);
    }

    /**
     * Returns referenced value.
     *
     * @return referenced value
     */
    @Nullable
    T get();

    /**
     * Sets referenced value.
     *
     * @param value referenced value
     */
    void set(@Nullable T value);
}
