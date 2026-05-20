package space.sunqian.fs.base.bytes;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.Checker;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.io.BufferKit;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@code BytesBuilder} is used to build byte arrays and their derived objects by appending byte data. It is similar to
 * {@link ByteArrayOutputStream}, provides compatible methods, but is not thread-safe, and the {@code close()} method
 * has no effect.
 * <p>
 * {@code BytesBuilder} uses a segmented storage strategy for efficient memory management and avoids frequent array
 * copying during large data appends. It holds a list of segments, each segment is a byte array, using
 * {@link #BytesBuilder(int)} and {@link #BytesBuilder(int, int)} can specify the capacity for them.
 *
 * @author sunqian
 */
public class BytesBuilder extends OutputStream {

    private final @Nonnull List<byte[]> segmentList;
    private final int segmentCapacity;
    private byte[] segment;
    private int segmentOff = 0;

    private int length = 0;

    /**
     * Constructs with 64-bytes initial segment capacity.
     */
    public BytesBuilder() {
        this(64);
    }

    /**
     * Constructs with the specified initial segment capacity in bytes.
     *
     * @param initialSegmentCapacity the specified initial segment capacity in bytes
     * @throws IllegalArgumentException if the capacity is not positive
     */
    public BytesBuilder(int initialSegmentCapacity) throws IllegalArgumentException {
        this(initialSegmentCapacity, -1);
    }

    /**
     * Constructs with the specified initial segment capacity and initial segment list capacity.
     *
     * @param initialSegmentCapacity     the specified initial segment capacity in bytes
     * @param initialSegmentListCapacity the initial capacity of the segment list, or -1 for default
     * @throws IllegalArgumentException if the segment capacity is not positive, or the list capacity is neither -1 nor
     *                                  positive
     */
    public BytesBuilder(
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
     * Appends the specified byte to this builder.
     *
     * @param b the specified byte
     */
    @Override
    public void write(int b) {
        prepareBuffer();
        segment[segmentOff++] = (byte) b;
        length++;
    }

    /**
     * Appends all bytes from the given array.
     *
     * @param arr the given array
     */
    @Override
    public void write(byte @Nonnull [] arr) {
        write(arr, 0, arr.length);
    }

    /**
     * Appends the specified number of bytes from the given array, starting at the specified offset.
     *
     * @param arr the given array
     * @param off the specified offset
     * @param len the specified number of bytes to append
     * @throws IndexOutOfBoundsException if the offset or length is out of bounds
     */
    @Override
    public void write(byte @Nonnull [] arr, int off, int len) throws IndexOutOfBoundsException {
        Checker.checkOffLen(off, len, arr.length);
        if (len == 0) {
            return;
        }
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
     * Appends the specified byte to this builder.
     *
     * @param b the specified byte
     * @return this builder
     */
    public @Nonnull BytesBuilder append(int b) {
        write(b);
        return this;
    }

    /**
     * Appends the specified byte to this builder.
     *
     * @param b the specified byte
     * @return this builder
     */
    public @Nonnull BytesBuilder append(byte b) {
        write(b);
        return this;
    }

    /**
     * Appends all bytes from the given array.
     *
     * @param bytes the given array
     * @return this builder
     */
    public @Nonnull BytesBuilder append(byte @Nonnull [] bytes) {
        write(bytes);
        return this;
    }

    /**
     * Appends the specified number of bytes from the given array, starting at the specified offset.
     *
     * @param bytes  the given array
     * @param offset the specified offset
     * @param length the specified number of bytes to append
     * @return this builder
     * @throws IndexOutOfBoundsException if the offset or length is out of bounds
     */
    public @Nonnull BytesBuilder append(byte @Nonnull [] bytes, int offset, int length) throws IndexOutOfBoundsException {
        write(bytes, offset, length);
        return this;
    }

    /**
     * Reads and appends all byte data from the given buffer. Note the buffer will be advanced to the end.
     *
     * @param buffer the given buffer
     * @return this builder
     */
    public @Nonnull BytesBuilder append(@Nonnull ByteBuffer buffer) {
        int remaining = buffer.remaining();
        if (remaining == 0) {
            return this;
        }
        if (buffer.hasArray()) {
            write(buffer.array(), BufferKit.arrayStartIndex(buffer), buffer.remaining());
            buffer.position(buffer.position() + buffer.remaining());
        } else {
            byte[] data = new byte[remaining];
            buffer.get(data);
            write(data);
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
        segment = new byte[segmentCapacity];
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
    public byte @Nonnull [] toByteArray() {
        byte[] result = new byte[length];
        int off = 0;
        for (byte[] seg : segmentList) {
            System.arraycopy(seg, 0, result, off, seg.length);
            off += seg.length;
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
    public @Nonnull ByteBuffer toByteBuffer() {
        return ByteBuffer.wrap(toByteArray());
    }

    /**
     * Returns a string decoded from the appended data using {@link CharsKit#defaultCharset()}.
     *
     * @return a string decoded from the appended data using {@link CharsKit#defaultCharset()}
     */
    @Override
    public @Nonnull String toString() {
        return toString(Charset.defaultCharset());
    }

    /**
     * Returns a string decoded from the appended data using the specified charset.
     *
     * @param charset the specified charset
     * @return a string decoded from the appended data using the specified charset
     */
    public @Nonnull String toString(@Nonnull Charset charset) {
        return new String(toByteArray(), charset);
    }
}