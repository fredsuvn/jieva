package space.sunqian.fs.base.string;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.Checker;
import space.sunqian.fs.base.lang.Slice;

/**
 * This class represents a slice of a {@link String}, defined by a start index inclusive, and an end index exclusive. It
 * is a view of the original string, and since {@link String} is immutable, any modification to the slice will not
 * affect the original string.
 *
 * @author sunqian
 */
public final class StringSlice implements Slice<String> {

    /**
     * Returns a new {@link StringSlice} from the specified string, start index, and end index.
     *
     * @param string     the string to slice from
     * @param startIndex the start index of the slice, inclusive
     * @param endIndex   the end index of the slice, exclusive
     * @return the new {@link StringSlice} from the specified string, start index, and end index
     * @throws IndexOutOfBoundsException if the start index or end index is out of range
     */
    public static @Nonnull StringSlice of(
        @Nonnull String string,
        int startIndex,
        int endIndex
    ) throws IndexOutOfBoundsException {
        Checker.checkInBounds(startIndex, endIndex, 0, string.length());
        return new StringSlice(string, startIndex, endIndex);
    }

    private final @Nonnull String string;
    private final int startIndex;
    private final int endIndex;

    private StringSlice(@Nonnull String string, int startIndex, int endIndex) {
        this.string = string;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public @Nonnull String source() {
        return string;
    }

    @Override
    public int startIndex() {
        return startIndex;
    }

    @Override
    public int endIndex() {
        return endIndex;
    }

    /**
     * Returns the character at the specified index relative to the start index of the slice. For example, the index
     * {@code 1} is the index {@code 1 + startIndex()} in the original string.
     *
     * @param index the index of the character relative to the start index of the slice
     * @return the character at the specified index relative to the start index of the slice
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public char charAt(int index) throws IndexOutOfBoundsException {
        Checker.checkInBounds(index, 0, length());
        return string.charAt(index + startIndex());
    }

    /**
     * Returns a {@link String} representation of the slice.
     *
     * @return a {@link String} representation of the slice
     */
    @Override
    public @Nonnull String toString() {
        return string.substring(startIndex, endIndex);
    }
}