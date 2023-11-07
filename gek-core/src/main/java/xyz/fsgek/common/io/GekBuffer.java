package xyz.fsgek.common.io;

import xyz.fsgek.common.base.GekBytes;
import xyz.fsgek.common.base.GekChars;
import xyz.fsgek.common.base.GekCheck;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.IntFunction;

/**
 * Utilities for {@link Buffer}, etc.
 *
 * @author fredsuvn
 */
public class GekBuffer {



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
     * @param generator given buffer generator, the function arguments is capacity
     * @return split buffers
     */
    public static List<ByteBuffer> splitInLength(ByteBuffer buffer, int length, IntFunction<ByteBuffer> generator) {
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
     * @param generator    given buffer generator, the function arguments is capacity
     * @return split buffers
     */
    public static List<ByteBuffer> splitInLength(
        ByteBuffer buffer, int lengthOffset, int lengthSize, IntFunction<ByteBuffer> generator) {
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
     * @param generator given buffer generator, the function arguments is capacity
     * @return split buffers
     */
    public static List<ByteBuffer> splitByDelimiter(
        ByteBuffer buffer, byte delimiter, IntFunction<ByteBuffer> generator) {
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
}
