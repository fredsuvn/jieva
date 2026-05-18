package space.sunqian.fs.base.string;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.RetainedParam;

import java.util.List;

/**
 * This interface represents a slice for char sequence, can be a view over one or more {@link CharSequence} instances.
 * It provides a unified interface to access character sequences that may be composed of multiple underlying sources.
 * <p>
 * Note that changes in the underlying {@link CharSequence} will affect this slice.
 *
 * @author sunqian
 */
public interface StringSlice extends CharSequence {

    /**
     * Returns a {@link StringSlice} that is composed of the specified {@link CharSequence} instances.
     *
     * @param strings the specified {@link CharSequence} instances to compose
     * @return a {@link StringSlice} that is composed of the specified {@link CharSequence} instances
     */
    static @Nonnull StringSlice of(@Nonnull CharSequence @Nonnull @RetainedParam ... strings) {
        return StringSliceBack.newSlice(strings);
    }

    /**
     * Returns a {@link StringSlice} that is composed of the specified list of {@link CharSequence}.
     *
     * @param strings the specified list of {@link CharSequence} to compose
     * @return a {@link StringSlice} that is composed of the specified list of {@link CharSequence}
     */
    static @Nonnull StringSlice of(@Nonnull List<? extends @Nonnull CharSequence> strings) {
        return of(strings.toArray(new CharSequence[0]));
    }

    /**
     * Returns a {@link StringSlice} that is composed of the specified {@link char} array.
     *
     * @param chars the specified {@link char} array to compose
     * @return a {@link StringSlice} that is composed of the specified {@link char} array
     */
    static @Nonnull StringSlice of(char @Nonnull @RetainedParam [] chars) {
        return of(chars, 0, chars.length);
    }

    /**
     * Returns a {@link StringSlice} that is composed of the specified {@link char} array.
     *
     * @param chars the specified {@link char} array to compose
     * @param start the start index of the {@link char} array to compose, inclusive
     * @param end   the end index of the {@link char} array to compose, exclusive
     * @return a {@link StringSlice} that is composed of the specified {@link char} array
     * @throws IndexOutOfBoundsException if the start or end index is out of bounds
     */
    static @Nonnull StringSlice of(
        char @Nonnull @RetainedParam [] chars,
        int start,
        int end
    ) throws IndexOutOfBoundsException {
        return StringSliceBack.newSlice(chars, start, end);
    }
}
