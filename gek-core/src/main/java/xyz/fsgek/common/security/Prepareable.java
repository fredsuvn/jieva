package xyz.fsgek.common.security;

import java.io.InputStream;
import java.nio.ByteBuffer;

interface Prepareable extends SecurityAlgorithm {

    /**
     * Prepares for source array.
     *
     * @param source source array
     * @return prepared process
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
     * @return prepared process
     */
    CryptoProcess prepare(byte[] source, int offset, int length);

    /**
     * Prepares for source buffer.
     *
     * @param source source buffer
     * @return prepared process
     */
    CryptoProcess prepare(ByteBuffer source);

    /**
     * Prepares for source stream.
     *
     * @param source source stream
     * @return prepared process
     */
    CryptoProcess prepare(InputStream source);
}
