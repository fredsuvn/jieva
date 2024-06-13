package xyz.fslabo.common.ref;

import xyz.fslabo.annotations.Nullable;

/**
 * This interface represents a mutable variable reference which references an object.
 * It is typically used in places where variables cannot be reassigned, for example:
 * <pre>
 *     String str = "a";
 *     Var&lt;String&gt; ref = Var.of("a");
 *     map.computeIfAbsent(key, it -&gt; {
 *         str = "other"; //error: cannot be re-assigned!
 *         ref.set("other"); //OK!
 *         //...
 *     });
 * </pre>
 * It also has versions for primitive types:
 * {@link BooleanVar}, {@link ByteVar}, {@link CharVar}, {@link ShortVar}, {@link IntVar}, {@link LongVar},
 * {@link DoubleVar} and {@link FloatVar}.
 *
 * @param <T> type of referenced value
 * @author fredsuvn
 */
public interface Var<T> {

    /**
     * Return an instance of {@link Var} of null initialized value.
     *
     * @param <T> type of initialized value
     * @return an instance of {@link Var} of null initialized value
     */
    static <T> Var<T> ofNull() {
        return of(null);
    }

    /**
     * Returns an instance of {@link Var} of initialized value.
     *
     * @param value initialized value
     * @param <T>   type of initialized value
     * @return an instance of {@link Var} of initialized value
     */
    static <T> Var<T> of(@Nullable T value) {
        return VarImpls.newVar(value);
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
