package xyz.srclab.common.lang;

/**
 * String utilities.
 *
 * @author fredsuvn
 */
public class MgString {

    /**
     * Copies chars from source char sequence to dest char array.
     *
     * @param src       source char sequence
     * @param srcStart  start index inclusive of source char sequence
     * @param srcEnd    start index exclusive of source char sequence
     * @param dest      dest char array
     * @param destStart start index inclusive of dest char array
     */
    public static void copyChars(CharSequence src, int srcStart, int srcEnd, char[] dest, int destStart) {
        MgCheck.checkRangeInBounds(srcStart, srcEnd, 0, src.length());
        int length = srcEnd - srcStart;
        MgCheck.checkRangeInBounds(destStart, destStart + length, 0, dest.length);
        if (src instanceof String) {
            ((String) src).getChars(srcStart, srcEnd, dest, destStart);
            return;
        }
        if (src instanceof CharArrayWrapper) {
            char[] array = ((CharArrayWrapper) src).getSource();
            int arrayStart = ((CharArrayWrapper) src).getSourceStartIndex();
            System.arraycopy(array, arrayStart + srcStart, dest, destStart, length);
            return;
        }
        if (src instanceof CharSequenceWrapper) {
            CharSequence chars = ((CharSequenceWrapper) src).getSource();
            int charsStart = ((CharSequenceWrapper) src).getSourceStartIndex();
            copyChars(chars, charsStart + srcStart, charsStart + srcEnd, dest, destStart);
            return;
        }
        for (int i = 0; i < length; i++) {
            dest[destStart + i] = src.charAt(srcStart + i);
        }
    }

    /**
     * Wraps source char sequence from start index inclusive to end index exclusive.
     * The new char sequence will be backed by the source char sequence, that means,
     * any modification to the source char sequence will reflect to the new char sequence.
     *
     * @param source     source char sequence
     * @param startIndex start index inclusive
     * @param endIndex   end index exclusive
     * @return new char sequence wraps source char sequence from start index inclusive to end index exclusive
     * @see CharSequenceWrapper
     */
    public static CharSequenceWrapper wrap(CharSequence source, int startIndex, int endIndex) {
        return new CharSequenceWrapper(source, startIndex, endIndex);
    }

    /**
     * Wraps source char array from start index inclusive to end index exclusive.
     * The new char sequence will be backed by the source char array, that means,
     * any modification to the source char array will reflect to the new char sequence.
     *
     * @param source     source char sequence
     * @param startIndex start index inclusive
     * @param endIndex   end index exclusive
     * @return new char sequence wraps source char array from start index inclusive to end index exclusive
     * @see CharArrayWrapper
     */
    public static CharArrayWrapper wrap(char[] source, int startIndex, int endIndex) {
        return new CharArrayWrapper(source, startIndex, endIndex);
    }
}
