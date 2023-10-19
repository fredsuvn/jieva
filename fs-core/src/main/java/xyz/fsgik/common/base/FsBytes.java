package xyz.fsgik.common.base;

/**
 * Utilities for byte array, etc.
 *
 * @author fredsuvn
 */
public class FsBytes {

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
