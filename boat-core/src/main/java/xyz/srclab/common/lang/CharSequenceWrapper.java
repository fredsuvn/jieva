package xyz.srclab.common.lang;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Wraps source char sequence from start index inclusive to end index exclusive.
 * This wrapper is backed by the source char sequence, that means,
 * any modification to the source char sequence will reflect to this wrapper.
 *
 * @author fredsuvn
 */
@EqualsAndHashCode
public class CharSequenceWrapper implements CharSequence {

    /**
     * Source char sequence.
     */
    @Getter
    private final CharSequence source;
    /**
     * Start index of source char sequence.
     */
    @Getter
    private final int sourceStartIndex;
    /**
     * Start index of source char sequence.
     */
    @Getter
    private final int sourceEndIndex;

    /**
     * Constructs with source char sequence from start index inclusive to end index exclusive.
     *
     * @param source     source char sequence
     * @param startIndex start index inclusive
     * @param endIndex   end index exclusive
     */
    public CharSequenceWrapper(CharSequence source, int startIndex, int endIndex) {
        MgCheck.checkRangeInBounds(startIndex, endIndex, 0, source.length());
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
        return source.charAt(index + sourceStartIndex);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        if (start == 0 && end == length()) {
            return this;
        }
        MgCheck.checkRangeInBounds(start, end, 0, length());
        return new CharSequenceWrapper(source, sourceStartIndex + start, sourceStartIndex + end);
    }

    @Override
    public String toString() {
        return source.subSequence(sourceStartIndex, sourceEndIndex).toString();
    }
}
