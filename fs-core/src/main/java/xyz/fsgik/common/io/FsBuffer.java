package xyz.fsgik.common.io;

import xyz.fsgik.common.base.FsBytes;
import xyz.fsgik.common.base.FsChars;
import xyz.fsgik.common.base.FsCheck;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Utilities for {@link Buffer}, etc.
 *
 * @author fredsuvn
 */
public class FsBuffer {

    private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(FsBytes.emptyBytes());

    /**
     * Returns an empty byte buffer.
     *
     * @return an empty byte buffer
     */
    public static ByteBuffer emptyBuffer() {
        return EMPTY_BUFFER;
    }

    /**
     * Read all bytes of given buffer into a new byte array and return.
     * After reading, the buffer's position will be updated to its limit, its limit will not have been changed.
     *
     * @param buffer given buffer
     * @return read bytes array
     */
    public static byte[] getBytes(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return bytes;
    }

    /**
     * Reads bytes of specified number from given buffer.
     * If the number is greater than remaining of buffer, it will only read the remaining bytes.
     * After reading, the buffer's position will be updated to (position + number) or its limit,
     * its limit will not have been changed.
     *
     * @param buffer given buffer
     * @param number specified number
     * @return bytes of specified limit number from given buffer
     */
    public static byte[] getBytes(ByteBuffer buffer, int number) {
        int length = Math.min(buffer.remaining(), number);
        byte[] result = new byte[length];
        buffer.get(result);
        return result;
    }

    /**
     * Read all bytes of given buffer and encodes to string with {@link FsChars#defaultCharset()}.
     * After reading, the buffer's position will be updated to its limit, its limit will not have been changed.
     *
     * @param buffer given buffer
     * @return read string
     */
    public static String getString(ByteBuffer buffer) {
        return getString(buffer, FsChars.defaultCharset());
    }

    /**
     * Read all bytes of given buffer and encodes to string with given charset.
     * After reading, the buffer's position will be updated to its limit, its limit will not have been changed.
     *
     * @param buffer  given buffer
     * @param charset given charset
     * @return read string
     */
    public static String getString(ByteBuffer buffer, Charset charset) {
        byte[] bytes = getBytes(buffer);
        return new String(bytes, charset);
    }

    /**
     * Returns slice of given buffer by {@link ByteBuffer#slice()}, and set new buffer's limit to given limit.
     *
     * @param buffer given buffer
     * @param limit  given limit
     * @return sliced buffer
     */
    public static ByteBuffer slice(ByteBuffer buffer, int limit) {
        ByteBuffer slice = buffer.slice();
        slice.limit(limit);
        return slice;
    }

    /**
     * Returns a sub-range view of given buffer, start from given offset to limit.
     * The two buffers will share the same data so any operation will reflect each other.
     * <p>
     * Note the offset is start from 0 <b>not</b> current position.
     *
     * @param buffer given buffer
     * @param offset given offset
     * @return the sub-buffer
     */
    public static ByteBuffer subView(ByteBuffer buffer, int offset) {
        FsCheck.checkInBounds(offset, 0, buffer.limit());
        int pos = buffer.position();
        buffer.position(offset);
        ByteBuffer slice = buffer.slice();
        buffer.position(pos);
        return slice;
    }

    /**
     * Returns a sub-range view of given buffer, start from given offset to specified length.
     * The two buffers will share the same data so any operation will reflect each other.
     * <p>
     * Note the offset is start from 0 <b>not</b> current position.
     *
     * @param buffer given buffer
     * @param offset given offset
     * @param length specified length
     * @return the sub-buffer
     */
    public static ByteBuffer subView(ByteBuffer buffer, int offset, int length) {
        FsCheck.checkRangeInBounds(offset, offset + length, 0, buffer.limit());
        int pos = buffer.position();
        buffer.position(offset);
        ByteBuffer slice = buffer.slice();
        slice.limit(length);
        buffer.position(pos);
        return slice;
    }

    /**
     * Reads and split given buffer in fixed length. This method will move buffer's position by reading.
     * If the remaining length of buffer is not enough to split,
     * the buffer's position will be reset to last start position.
     * This method use {@link ByteBuffer#allocate(int)} to create new buffer,
     * then put data into the new buffer from given buffer.
     * <p>
     * Each returned buffer's position is 0, limit and capacity is the length.
     *
     * @param buffer given buffer
     * @param length fixed length
     * @return split buffers
     */
    public static List<ByteBuffer> splitInLength(ByteBuffer buffer, int length) {
        return splitInLength(buffer, length, ByteBuffer::allocate);
    }

    /**
     * Reads and split given buffer in fixed length. This method will move buffer's position by reading.
     * If the remaining length of buffer is not enough to split,
     * the buffer's position will be reset to last start position.
     * This method use given buffer generator to create new buffer, then put data into the new buffer from given buffer.
     * <p>
     * Each returned buffer's position is 0, limit and capacity is the length.
     *
     * @param buffer    given buffer
     * @param length    fixed length
     * @param generator given buffer generator
     * @return split buffers
     */
    public static List<ByteBuffer> splitInLength(ByteBuffer buffer, int length, BufferGen generator) {
        if (!buffer.hasRemaining()) {
            return Collections.emptyList();
        }
        if (buffer.remaining() < length) {
            return Collections.emptyList();
        }
        List<ByteBuffer> result = null;
        while (buffer.remaining() >= length) {
            ByteBuffer subBuffer = generator.apply(length);
            subBuffer.put(slice(buffer, length));
            subBuffer.flip();
            buffer.position(buffer.position() + length);
            if (result == null) {
                result = new LinkedList<>();
            }
            result.add(subBuffer);
        }
        return result;
    }

    /**
     * Reads and split given buffer in specified length.
     * <p>
     * The split length is specified at offset ({@code lengthOffset}) of buffer, and the {@code lengthSize}
     * specifies width of {@code lengthOffset} (must in 1, 2, 4).
     * <b>The split length value must &lt;= {@link Integer#MAX_VALUE}.</b>
     * <p>
     * This method will move buffer's position by reading.
     * If the remaining length of buffer is not enough to split,
     * the buffer's position will be reset to last start position.
     * <p>
     * This method use {@link ByteBuffer#allocate(int)} to create new buffer,
     * then put data into the new buffer from given buffer.
     * Each returned buffer's position is 0, limit and capacity is the length.
     *
     * @param buffer       given buffer
     * @param lengthOffset offset of length
     * @param lengthSize   length size must in 1, 2, 4
     * @return split buffers
     */
    public static List<ByteBuffer> splitInLength(
        ByteBuffer buffer, int lengthOffset, int lengthSize) {
        return splitInLength(buffer, lengthOffset, lengthSize, ByteBuffer::allocate);
    }

    /**
     * Reads and split given buffer in specified length.
     * <p>
     * The split length is specified at offset ({@code lengthOffset}) of buffer, and the {@code lengthSize}
     * specifies width of {@code lengthOffset} (must in 1, 2, 4).
     * <b>The split length value must &lt;= {@link Integer#MAX_VALUE}.</b>
     * <p>
     * This method will move buffer's position by reading.
     * If the remaining length of buffer is not enough to split,
     * the buffer's position will be reset to last start position.
     * <p>
     * This method use given buffer generator to create new buffer, then put data into the new buffer from given buffer.
     * Each returned buffer's position is 0, limit and capacity is the length.
     *
     * @param buffer       given buffer
     * @param lengthOffset offset of length
     * @param lengthSize   length size must in 1, 2, 4
     * @param generator    given buffer generator
     * @return split buffers
     */
    public static List<ByteBuffer> splitInLength(
        ByteBuffer buffer, int lengthOffset, int lengthSize, BufferGen generator) {
        if (!buffer.hasRemaining()) {
            return Collections.emptyList();
        }
        int minSize = lengthOffset + lengthSize;
        if (buffer.remaining() < minSize) {
            return Collections.emptyList();
        }
        List<ByteBuffer> result = null;
        while (true) {
            buffer.mark();
            buffer.position(buffer.position() + lengthOffset);
            int length = readLength(buffer, lengthSize);
            buffer.reset();
            if (buffer.remaining() < length) {
                break;
            }
            ByteBuffer subBuffer = generator.apply(length);
            subBuffer.put(slice(buffer, length));
            subBuffer.flip();
            buffer.position(buffer.position() + length);
            if (result == null) {
                result = new LinkedList<>();
            }
            result.add(subBuffer);
            if (buffer.remaining() < minSize) {
                break;
            }
        }
        return result == null ? Collections.emptyList() : result;
    }

    private static int readLength(ByteBuffer buffer, int lengthSize) {
        switch (lengthSize) {
            case 1:
                return buffer.get() & 0x000000ff;
            case 2:
                return buffer.getShort() & 0x0000ffff;
            case 4:
                return buffer.getInt();
        }
        throw new IllegalArgumentException("lengthSize must in (1, 2, 4).");
    }

    /**
     * Reads and split given buffer by specified delimiter. This method will move buffer's position by reading.
     * If it meets a delimiter, a new buffer will be created by {@link ByteBuffer#allocate(int)},
     * and data from start position or {@code last delimiter pos + 1} (as start position) to
     * {@code current delimiter pos - 1} will be put into the new buffer.
     * If no delimiter read and given buffer is read to the end,
     * the buffer's position will be reset to last start position.
     * <p>
     * For example:
     * <pre>
     *     splitByDelimiter("123|456|789|") = ["123", "456", "789"]
     *     splitByDelimiter("|123|456|789|") = ["", "123", "456", "789"]
     *     splitByDelimiter("|123|456|78") = ["", "123", "456"] and reset to position 9
     * </pre>
     * Each returned buffer's position is 0, limit and capacity is the length.
     *
     * @param buffer    given buffer
     * @param delimiter specified delimiter
     * @return split buffers
     */
    public static List<ByteBuffer> splitByDelimiter(ByteBuffer buffer, byte delimiter) {
        return splitByDelimiter(buffer, delimiter, ByteBuffer::allocate);
    }

    /**
     * Reads and split given buffer by specified delimiter. This method will move buffer's position by reading.
     * If it meets a delimiter, a new buffer will be created by given buffer generator,
     * and data from start position or {@code last delimiter pos + 1} (as start position) to
     * {@code current delimiter pos - 1} will be put into the new buffer.
     * If no delimiter read and given buffer is read to the end,
     * the buffer's position will be reset to last start position.
     * <p>
     * For example:
     * <pre>
     *     splitByDelimiter("123|456|789|") = ["123", "456", "789"]
     *     splitByDelimiter("|123|456|789|") = ["", "123", "456", "789"]
     *     splitByDelimiter("|123|456|78") = ["", "123", "456"] and reset to position 9
     * </pre>
     * Each returned buffer's position is 0, limit and capacity is the length.
     *
     * @param buffer    given buffer
     * @param delimiter specified delimiter
     * @param generator given buffer generator
     * @return split buffers
     */
    public static List<ByteBuffer> splitByDelimiter(ByteBuffer buffer, byte delimiter, BufferGen generator) {
        if (!buffer.hasRemaining()) {
            return Collections.emptyList();
        }
        List<ByteBuffer> result = null;
        buffer.mark();
        while (buffer.hasRemaining()) {
            byte b = buffer.get();
            if (b == delimiter) {
                int delimiterPos = buffer.position() - 1;
                buffer.reset();
                int length = delimiterPos - buffer.position();
                ByteBuffer subBuffer = generator.apply(length);
                subBuffer.put(slice(buffer, length));
                subBuffer.flip();
                buffer.position(delimiterPos + 1);
                buffer.mark();
                if (result == null) {
                    result = new LinkedList<>();
                }
                result.add(subBuffer);
            }
        }
        buffer.reset();
        return result == null ? Collections.emptyList() : result;
    }

    /**
     * Functional interface to generator byte buffer.
     *
     * @author fredsuvn
     */
    @FunctionalInterface
    public interface BufferGen {
        /**
         * Returns a byte buffer with specified capacity.
         * Position of returned buffer is 0 and limit is capacity.
         *
         * @param capacity specified capacity
         */
        ByteBuffer apply(int capacity);
    }
}
