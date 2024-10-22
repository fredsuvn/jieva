package xyz.fslabo.common.base;

/**
 * Utilities for bytes, etc.
 *
 * @author fredsuvn
 */
public class JieBytes {

    private static final byte[] EMPTY_BYTES = new byte[0];

    /**
     * Returns an empty byte array.
     *
     * @return an empty byte array
     */
    public static byte[] emptyBytes() {
        return EMPTY_BYTES;
    }
}
