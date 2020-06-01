package xyz.srclab.common.lang.lazy;

import xyz.srclab.annotation.Immutable;

import java.util.function.Supplier;

/**
 * @author sunqian
 */
@Immutable
public interface Lazy<T> {

    static <T> Lazy<T> of(Supplier<T> supplier) {
        return Lazy0.newLazy(supplier);
    }

    T get();
}
