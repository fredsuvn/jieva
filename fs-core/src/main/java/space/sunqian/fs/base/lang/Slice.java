package space.sunqian.fs.base.lang;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.string.StringSlice;
import space.sunqian.fs.collect.ArraySlice;

/**
 * This interface represents a slice of a slice-able object, such as a string ({@link StringSlice}) or an array
 * ({@link ArraySlice}), defined by a start index inclusive, and an end index exclusive. It is a view of the original
 * object, and any modification to the slice will be reflected in the original object, and vice versa.
 *
 * @param <T> the type of the slice-able source object
 * @author sunqian
 */
public interface Slice<T> {

    /**
     * Returns the source object of the slice.
     *
     * @return the source object of the slice
     */
    @Nonnull
    T source();

    /**
     * Returns the start index of the slice.
     *
     * @return the start index of the slice
     */
    int startIndex();

    /**
     * Returns the end index of the slice.
     *
     * @return the end index of the slice
     */
    int endIndex();

    /**
     * Returns the length of the slice.
     *
     * @return the length of the slice
     */
    default int length() {
        return endIndex() - startIndex();
    }
}