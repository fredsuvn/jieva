package xyz.fsgik.common.base;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Utilities for byte array, {@link ByteBuffer}, etc.
 *
 * @author sunq62
 */
public class FsBytes {

    private static final byte[] EMPTY_BYTES = new byte[0];
    private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(EMPTY_BYTES);

    /**
     * Returns an empty byte array.
     *
     * @return an empty byte array
     */
    public static byte[] emptyBytes() {
        return EMPTY_BYTES;
    }

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
    public static ByteBuffer subBuffer(ByteBuffer buffer, int offset) {
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
    public static ByteBuffer subBuffer(ByteBuffer buffer, int offset, int length) {
        FsCheck.checkRangeInBounds(offset, offset + length, 0, buffer.limit());
        int pos = buffer.position();
        buffer.position(offset);
        ByteBuffer slice = buffer.slice();
        slice.limit(length);
        buffer.position(pos);
        return slice;
    }
}
