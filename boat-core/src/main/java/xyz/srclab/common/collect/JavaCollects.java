package xyz.srclab.common.collect;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author sunqian
 */
class JavaCollects {

    @NotNull
    static Object[] toArray(@NotNull Collection<?> collection) {
        return collection.toArray();
    }

    @NotNull
    static <T> T[] toArray(@NotNull Collection<T> collection, @NotNull T[] array) {
        return collection.toArray(array);
    }
}
