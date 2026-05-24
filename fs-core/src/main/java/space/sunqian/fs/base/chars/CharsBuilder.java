package space.sunqian.fs.base.chars;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.Checker;
import space.sunqian.fs.base.string.StringSlice;
import space.sunqian.fs.io.BufferKit;

import java.io.CharArrayWriter;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@code CharsBuilder} is used to build {@link String}, char array, and their derived objects by appending char data.
 * It is similar to {@link CharArrayWriter} and {@link StringBuilder}, but is not thread-safe. and it has no effect on
 * {@code close()} and {@code flush()} methods.
 * <p>
 * It is different from {@link StringBuilder} in that it does not use array copying to ensure and grow the internal
 * buffer. Instead, it holds a list of segments to store the appended data, each segment is a char array or a string or
 * a {@link StringSlice}, using {@link #CharsBuilder(int)} and {@link #CharsBuilder(int, int)} can specify the capacity
 * for them.
 *
 * @author sunqian
 */
public final class CharsBuilder extends Writer {

    /**
     * The default initial segment capacity.
     */
    public static final int DEFAULT_SEGMENT_CAPACITY = 64;

    private final @Nonnull List<@Nonnull Object> segmentList;
    private final int segmentCapacity;
    private char[] segment;
    private int segmentOff = 0;

    private int length = 0;

    /**
     * Constructs with the default initial segment capacity ({@link #DEFAULT_SEGMENT_CAPACITY}).
     */
    public CharsBuilder() {
        this(DEFAULT_SEGMENT_CAPACITY);
    }

    /**
     * Constructs with the specified initial segment capacity and default initial segment list capacity ({@code -1}).
     *
     * @param initialSegmentCapacity the specified initial segment capacity
     * @throws IllegalArgumentException if the capacity is not positive
     */
    public CharsBuilder(int initialSegmentCapacity) throws IllegalArgumentException {
        this(initialSegmentCapacity, -1);
    }

    /**
     * Constructs with the specified initial segment capacity and initial segment list capacity.
     *
     * @param initialSegmentCapacity     the specified initial segment capacity
     * @param initialSegmentListCapacity the initial capacity of the segment list, or {@code -1} for default
     * @throws IllegalArgumentException if the segment capacity is not positive, or the list capacity is neither
     *                                  {@code -1} nor positive
     */
    public CharsBuilder(
        int initialSegmentCapacity,
        int initialSegmentListCapacity
    ) throws IllegalArgumentException {
        Checker.checkArgument(initialSegmentCapacity > 0, "initialSegmentCapacity must > 0");
        Checker.checkArgument(
            initialSegmentListCapacity == -1 || initialSegmentListCapacity > 0,
            "initialSegmentListCapacity must be -1 or > 0"
        );
        this.segmentCapacity = initialSegmentCapacity;
        this.segmentList = initialSegmentListCapacity == -1 ? new ArrayList<>() : new ArrayList<>(initialSegmentListCapacity);
    }

    /**
     * Appends the specified char to this builder.
     *
     * @param b the specified char
     */
    @Override
    public void write(int b) {
        prepareBuffer();
        segment[segmentOff++] = (char) b;
        length++;
    }

    /**
     * Appends all chars from the given array.
     *
     * @param arr the given array
     */
    @Override
    public void write(char @Nonnull [] arr) {
        if (arr.length == 0) {
            return;
        }
        write0(arr, 0, arr.length);
    }

    /**
     * Appends the specified number of chars from the given array, starting at the specified offset.
     *
     * @param arr the given array
     * @param off the specified offset
     * @param len the specified number of chars to append
     * @throws IndexOutOfBoundsException if the offset or length is out of bounds
     */
    @Override
    public void write(char @Nonnull [] arr, int off, int len) throws IndexOutOfBoundsException {
        Checker.checkOffLen(off, len, arr.length);
        if (len == 0) {
            return;
        }
        write0(arr, off, len);
    }

    private void write0(char @Nonnull [] arr, int off, int len) throws IndexOutOfBoundsException {
        prepareBuffer();
        int copyLength = Math.min(segment.length - segmentOff, len);
        System.arraycopy(arr, off, segment, segmentOff, copyLength);
        segmentOff += copyLength;
        if (copyLength < len) {
            segmentList.add(segment);
            segment = null;
            int restLen = len - copyLength;
            if (restLen >= segmentCapacity) {
                segmentList.add(Arrays.copyOfRange(arr, off + copyLength, off + len));
            } else {
                prepareBuffer();
                System.arraycopy(arr, off + copyLength, segment, segmentOff, restLen);
                segmentOff += restLen;
            }
        }
        length += len;
    }

    /**
     * Appends all chars from the given string.
     *
     * @param str the given string
     */
    @Override
    public void write(@Nonnull String str) {
        if (str.isEmpty()) {
            return;
        }
        write0(str);
    }

    private void write0(@Nonnull String str) {
        if (segment == null) {
            segmentList.add(str);
        } else if (segmentOff == segment.length) {
            segmentList.add(segment);
            segment = null;
            segmentList.add(str);
        } else {
            int copyLength = Math.min(segment.length - segmentOff, str.length());
            str.getChars(0, copyLength, segment, segmentOff);
            segmentOff += copyLength;
            if (copyLength < str.length()) {
                segmentList.add(segment);
                segment = null;
                segmentList.add(StringSlice.of(str, copyLength, str.length()));
            }
        }
        length += str.length();
    }

    /**
     * Appends the specified number of chars from the given string, starting at the specified offset.
     *
     * @param str the given string
     * @param off the specified offset
     * @param len the specified number of chars to append
     * @throws IndexOutOfBoundsException if the offset or length is out of bounds
     */
    @Override
    public void write(@Nonnull String str, int off, int len) throws IndexOutOfBoundsException {
        Checker.checkOffLen(off, len, str.length());
        if (len == 0) {
            return;
        }
        write0(str, off, len);
    }

    private void write0(@Nonnull String str, int off, int len) throws IndexOutOfBoundsException {
        if (segment == null) {
            segmentList.add(StringSlice.of(str, off, off + len));
        } else if (segmentOff == segment.length) {
            segmentList.add(segment);
            segment = null;
            segmentList.add(StringSlice.of(str, off, off + len));
        } else {
            int copyLength = Math.min(segment.length - segmentOff, len);
            str.getChars(off, off + copyLength, segment, segmentOff);
            segmentOff += copyLength;
            if (copyLength < len) {
                segmentList.add(segment);
                segment = null;
                segmentList.add(StringSlice.of(str, off + copyLength, off + len));
            }
        }
        length += len;
    }

    /**
     * Appends the specified char to this builder.
     *
     * @param c the specified char
     * @return this builder
     */
    @Override
    public @Nonnull CharsBuilder append(char c) {
        return append((int) c);
    }

    /**
     * Appends the specified char to this builder.
     *
     * @param c the specified char
     * @return this builder
     */
    public @Nonnull CharsBuilder append(int c) {
        write(c);
        return this;
    }

    /**
     * Appends the given char sequence to this builder.
     *
     * @param csq the given char sequence, if it is {@code null}, then it will be considered as {@code "null"}.
     * @return this builder
     */
    @SuppressWarnings("SizeReplaceableByIsEmpty")
    @Override
    public @Nonnull CharsBuilder append(@Nullable CharSequence csq) {
        if (csq == null) {
            write0(Fs.NULL_STRING);
            return this;
        }
        if (csq.length() == 0) {
            return this;
        }
        if (csq instanceof String) {
            write0((String) csq);
            return this;
        }
        append0(csq, 0, csq.length());
        return this;
    }

    /**
     * Appends the specified subsequence of the given char sequence to this builder.
     *
     * @param csq   the given char sequence, if it is {@code null}, then it will be considered as {@code "null"}.
     * @param start the start index of the subsequence, inclusive
     * @param end   the end index of the subsequence, exclusive
     * @return this builder
     * @throws IndexOutOfBoundsException if the start or end index is out of bounds
     */
    @SuppressWarnings("SizeReplaceableByIsEmpty")
    @Override
    public @Nonnull CharsBuilder append(
        @Nullable CharSequence csq, int start, int end
    ) throws IndexOutOfBoundsException {
        if (csq == null) {
            write(Fs.NULL_STRING, start, end - start);
            return this;
        }
        Checker.checkStartEnd(start, end, csq.length());
        if (csq.length() == 0) {
            return this;
        }
        if (csq instanceof String) {
            write0((String) csq, start, end - start);
            return this;
        }
        append0(csq, start, end);
        return this;
    }

    private void append0(@Nonnull CharSequence csq, int start, int end) throws IndexOutOfBoundsException {
        prepareBuffer();
        int len = end - start;
        int copyLength = Math.min(segment.length - segmentOff, len);
        for (int i = 0; i < copyLength; i++) {
            segment[segmentOff++] = csq.charAt(start + i);
        }
        if (copyLength < len) {
            segmentList.add(segment);
            segment = null;
            int restLen = len - copyLength;
            if (restLen >= segmentCapacity) {
                char[] bigSeg = new char[restLen];
                for (int i = 0; i < restLen; i++) {
                    bigSeg[i] = csq.charAt(start + copyLength + i);
                }
                segmentList.add(bigSeg);
            } else {
                prepareBuffer();
                for (int i = 0; i < restLen; i++) {
                    segment[segmentOff++] = csq.charAt(start + copyLength + i);
                }
            }
        }
        length += len;
    }

    /**
     * Appends all chars from the given array.
     *
     * @param arr the given array
     * @return this builder
     */
    public @Nonnull CharsBuilder append(char @Nonnull [] arr) {
        write(arr);
        return this;
    }

    /**
     * Appends the specified number of chars from the given array, starting at the specified offset.
     *
     * @param arr the given array
     * @param off the specified offset
     * @param len the specified number of chars to append
     * @return this builder
     * @throws IndexOutOfBoundsException if the offset or length is out of bounds
     */
    public @Nonnull CharsBuilder append(char @Nonnull [] arr, int off, int len) throws IndexOutOfBoundsException {
        write(arr, off, len);
        return this;
    }

    /**
     * Reads and appends all char data from the given buffer. Note the buffer will be advanced to the end.
     *
     * @param buffer the given buffer
     * @return this builder
     */
    public @Nonnull CharsBuilder append(@Nonnull CharBuffer buffer) {
        int remaining = buffer.remaining();
        if (remaining == 0) {
            return this;
        }
        if (buffer.hasArray()) {
            write0(buffer.array(), BufferKit.arrayStartIndex(buffer), buffer.remaining());
            buffer.position(buffer.position() + buffer.remaining());
        } else {
            char[] data = new char[remaining];
            buffer.get(data);
            write0(data, 0, remaining);
        }
        return this;
    }

    private void prepareBuffer() {
        if (segment == null) {
            refreshBuffer();
        } else if (segmentOff == segment.length) {
            segmentList.add(segment);
            refreshBuffer();
        }
    }

    private void refreshBuffer() {
        segment = new char[segmentCapacity];
        segmentOff = 0;
    }

    /**
     * Resets this builder, the appended data will be discarded.
     */
    public void reset() {
        segmentList.clear();
        length = 0;
        segment = null;
        segmentOff = 0;
    }

    /**
     * No effect for this builder.
     */
    @Override
    public void flush() {
    }

    /**
     * No effect for this builder.
     */
    @Override
    public void close() {
    }

    /**
     * Returns the length of appended data.
     *
     * @return the length of appended data
     */
    public int length() {
        return length;
    }

    /**
     * Returns a new array containing a copy of the appended data.
     *
     * @return a new array containing a copy of the appended data
     */
    @SuppressWarnings("PatternVariableCanBeUsed")
    public char @Nonnull [] toCharArray() {
        char[] result = new char[length];
        int off = 0;
        for (Object obj : segmentList) {
            if (obj instanceof char[]) {
                char[] arr = (char[]) obj;
                System.arraycopy(arr, 0, result, off, arr.length);
                off += arr.length;
                continue;
            }
            if (obj instanceof String) {
                String str = (String) obj;
                str.getChars(0, str.length(), result, off);
                off += str.length();
                continue;
            }
            StringSlice slice = (StringSlice) obj;
            slice.source().getChars(slice.startIndex(), slice.endIndex(), result, off);
            off += slice.length();
        }
        if (segment != null) {
            System.arraycopy(segment, 0, result, off, segmentOff);
        }
        return result;
    }

    /**
     * Returns a new buffer containing a copy of the appended data.
     *
     * @return a new buffer containing a copy of the appended data
     */
    public @Nonnull CharBuffer toCharBuffer() {
        return CharBuffer.wrap(toCharArray());
    }

    /**
     * Returns a string from a copy of the appended data.
     *
     * @return a string from a copy of the appended data
     */
    public @Nonnull String toString() {
        return new String(toCharArray());
    }
}
