package xyz.srclab.common.base.ref;

import xyz.srclab.annotations.Nullable;

/**
 * This class contains an object, usually be used where the variable cannot be re-assigned.
 * <p>
 * For example:
 *
 * <pre>
 *     String str = "a";
 *     Ref&lt;String> ref = new Ref&lt;>("a");
 *     map.computeIfAbsent(key, it -> {
 *         str = "other"; //error: cannot re-assigned variable str!
 *         ref.set("other");
 *         //...
 *     });
 * </pre>
 *
 * @author fredsuvn
 */
public class Ref<T> {

    private T value;

    /**
     * Constructs with null.
     */
    public Ref() {
        this(null);
    }

    /**
     * Constructs with given value.
     *
     * @param value given value
     */
    public Ref(T value) {
        this.value = value;
    }

    /**
     * Returns value.
     */
    @Nullable
    public T get() {
        return value;
    }

    /**
     * Sets value
     *
     * @param value value
     */
    public void set(@Nullable T value) {
        this.value = value;
    }
}
