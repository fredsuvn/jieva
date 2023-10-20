package xyz.fsgek.common.base;

/**
 * Utilities for byte array, etc.
 *
 * @author fredsuvn
 */
public class GekBytes {

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
