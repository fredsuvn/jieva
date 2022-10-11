package xyz.srclab.common.lang;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Wraps source char array from start index inclusive to end index exclusive.
 * This wrapper is backed by the source char array, that means,
 * any modification to the source char array will reflect to this wrapper.
 *
 * @author fredsuvn
 */
@EqualsAndHashCode
public class CharArrayWrapper implements CharSequence {

    /**
     * Source char array.
     */
    @Getter
    private final char[] source;
    /**
     * Start index of source char array.
     */
    @Getter
    private final int sourceStartIndex;
    /**
     * End index of source char array.
     */
    @Getter
    private final int sourceEndIndex;

    /**
     * Constructs with source char array from start index inclusive to end index exclusive.
     *
     * @param source     source char array
     * @param startIndex start index inclusive
     * @param endIndex   end index exclusive
     */
    public CharArrayWrapper(char[] source, int startIndex, int endIndex) {
        MgCheck.checkRangeInBounds(startIndex, endIndex, 0, source.length);
        this.source = source;
        this.sourceStartIndex = startIndex;
        this.sourceEndIndex = endIndex;
    }

    @Override
    public int length() {
        return sourceEndIndex - sourceStartIndex;
    }

    @Override
    public char charAt(int index) {
        MgCheck.checkInBounds(index, 0, length());
        return source[index + sourceStartIndex];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        if (start == 0 && end == length()) {
            return this;
        }
        MgCheck.checkRangeInBounds(start, end, 0, length());
        return new CharArrayWrapper(source, sourceStartIndex + start, sourceStartIndex + end);
    }

    @Override
    public String toString() {
        return new String(source, sourceStartIndex, sourceEndIndex);
    }
}
