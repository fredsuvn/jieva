package space.sunqian.fs.base.string;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.RetainedParam;

import java.util.List;

/**
 * This interface represents a view over one or more {@link CharSequence} or {@code char[]}. It provides a unified
 * interface to access character sequences that may be composed of one or multiple underlying sources.
 * <p>
 * This interface holds the data source instead of copy, that means changes in the underlying {@link CharSequence} or
 * {@code char[]} will affect this view.
 *
 * @author sunqian
 */
public interface StringView extends CharSequence {

    /**
     * Returns a {@link StringView} that is composed of the specified {@link CharSequence} instances.
     *
     * @param strings the specified {@link CharSequence} instances to compose
     * @return a {@link StringView} that is composed of the specified {@link CharSequence} instances
     */
    static @Nonnull StringView of(@Nonnull CharSequence @Nonnull @RetainedParam ... strings) {
        return StringViewBack.newView(strings);
    }

    /**
     * Returns a {@link StringView} that is composed of the specified list of {@link CharSequence}.
     *
     * @param strings the specified list of {@link CharSequence} to compose
     * @return a {@link StringView} that is composed of the specified list of {@link CharSequence}
     */
    static @Nonnull StringView of(@Nonnull List<? extends @Nonnull CharSequence> strings) {
        return of(strings.toArray(new CharSequence[0]));
    }

    /**
     * Returns a {@link StringView} that is composed of the specified {@link char} array.
     *
     * @param chars the specified {@link char} array to compose
     * @return a {@link StringView} that is composed of the specified {@link char} array
     */
    static @Nonnull StringView of(char @Nonnull @RetainedParam [] chars) {
        return of(chars, 0, chars.length);
    }

    /**
     * Returns a {@link StringView} that is composed of the specified {@link char} array.
     *
     * @param chars the specified {@link char} array to compose
     * @param start the start index of the {@link char} array to compose, inclusive
     * @param end   the end index of the {@link char} array to compose, exclusive
     * @return a {@link StringView} that is composed of the specified {@link char} array
     * @throws IndexOutOfBoundsException if the start or end index is out of bounds
     */
    static @Nonnull StringView of(
        char @Nonnull @RetainedParam [] chars,
        int start,
        int end
    ) throws IndexOutOfBoundsException {
        return StringViewBack.newView(chars, start, end);
    }
}
