package xyz.fslabo.common.base;

import java.nio.ByteBuffer;

/**
 * Utilities for bytes, etc.
 *
 * @author fredsuvn
 */
public class JieBytes {

    private static final byte[] EMPTY_BYTES = new byte[0];
    private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(JieBytes.emptyBytes());

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
}
