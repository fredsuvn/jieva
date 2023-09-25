package xyz.srclab.common.security;

import java.io.InputStream;
import java.nio.ByteBuffer;

interface Prepareable extends SecurityAlgorithm {

    /**
     * Prepares for source array.
     *
     * @param source source array
     */
    default CryptoProcess prepare(byte[] source) {
        return prepare(source, 0, source.length);
    }

    /**
     * Prepares for source array of specified length from offset index.
     *
     * @param source source array
     * @param offset offset index
     * @param length specified length
     */
    CryptoProcess prepare(byte[] source, int offset, int length);

    /**
     * Prepares for source buffer.
     *
     * @param source source buffer
     */
    CryptoProcess prepare(ByteBuffer source);

    /**
     * Prepares for source stream.
     *
     * @param source source stream
     */
    CryptoProcess prepare(InputStream source);
}
