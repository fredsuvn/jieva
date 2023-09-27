package xyz.fs404.common.net;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Network utilities.
 *
 * @author fredsuvn
 */
public class FsNet {

    /**
     * Reads and split given buffer in fixed length. This method will move buffer's position by reading.
     * If the remaining length of buffer is not enough to split,
     * the buffer's position will be reset to last start position.
     *
     * @param buffer given buffer
     * @param length fixed length
     */
    public static List<ByteBuffer> splitWithFixedLength(ByteBuffer buffer, int length) {
        if (buffer.remaining() < length) {
            return Collections.emptyList();
        }
        List<ByteBuffer> result = new LinkedList<>();
        while (buffer.remaining() >= length) {
            byte[] bytes = new byte[length];
            buffer.get(bytes);
            result.add(ByteBuffer.wrap(bytes));
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
     *
     * @param buffer       given buffer
     * @param lengthOffset offset of length
     * @param lengthSize   length size must in 1, 2, 4
     */
    public static List<ByteBuffer> splitWithSpecifiedLength(ByteBuffer buffer, int lengthOffset, int lengthSize) {
        int minSize = lengthOffset + lengthSize;
        if (buffer.remaining() < minSize) {
            return Collections.emptyList();
        }
        List<ByteBuffer> result = new LinkedList<>();
        while (true) {
            buffer.mark();
            buffer.position(buffer.position() + lengthOffset);
            int length = readLength(buffer, lengthSize);
            buffer.reset();
            if (buffer.remaining() < length) {
                break;
            }
            byte[] bytes = new byte[length];
            buffer.get(bytes);
            result.add(ByteBuffer.wrap(bytes));
            if (buffer.remaining() < minSize) {
                break;
            }
        }
        return result.isEmpty() ? Collections.emptyList() : result;
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
}
