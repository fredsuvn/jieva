package xyz.fsgik.common.io;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * Utilities for {@link Buffer}.
 *
 * @author fredsuvn
 */
public class FsBuffer {

    private static final byte[] EMPTY_BYTES = new byte[0];
    private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(EMPTY_BYTES);

    /**
     * Returns an empty byte buffer.
     *
     * @return an empty byte buffer
     */
    public static ByteBuffer emptyBuffer() {
        return EMPTY_BUFFER;
    }

    /**
     * Reads bytes of specified length from given buffer.
     * If length is greater than remaining of buffer, the returned array will only contain the remaining bytes.
     *
     * @param buffer given buffer
     * @param length specified length
     * @return bytes of specified length from given buffer
     */
    public byte[] getBytes(ByteBuffer buffer, int length) {
        int actualLength = Math.min(buffer.remaining(), length);
        byte[] result = new byte[actualLength];
        buffer.get(result);
        return result;
    }
}
