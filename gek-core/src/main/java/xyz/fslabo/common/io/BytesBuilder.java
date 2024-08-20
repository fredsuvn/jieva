package xyz.fslabo.common.io;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

/**
 * Builder for building bytes, extension of {@link ByteArrayOutputStream}. Just like {@link StringBuilder} but it is
 * used for bytes.
 *
 * @author fredsuvn
 */
public class BytesBuilder extends ByteArrayOutputStream {

    /**
     * Constructs with default settings.
     */
    public BytesBuilder() {
        super();
    }

    /**
     * Constructs with specified initialized size.
     *
     * @param size specified initialized size
     */
    public BytesBuilder(int size) {
        super(size);
    }

    /**
     * Appends a byte into this builder.
     *
     * @param b a byte
     * @return this builder
     */
    public BytesBuilder append(byte b) {
        write(b);
        return this;
    }

    /**
     * Appends given bytes into this builder.
     *
     * @param bytes given bytes
     * @return this builder
     */
    public BytesBuilder append(byte[] bytes) {
        write(bytes, 0, bytes.length);
        return this;
    }

    /**
     * Appends given bytes of specified length from given offset into this builder.
     *
     * @param bytes  given bytes
     * @param offset given offset
     * @param length specified length
     * @return this builder
     */
    public BytesBuilder append(byte[] bytes, int offset, int length) {
        write(bytes, offset, length);
        return this;
    }

    /**
     * Appends given byte buffer into this builder.
     *
     * @param bytes given byte buffer
     * @return this builder
     */
    public BytesBuilder append(ByteBuffer bytes) {
        if (!bytes.hasRemaining()) {
            return this;
        }
        if (bytes.hasArray()) {
            write(bytes.array(), bytes.arrayOffset() + bytes.position(), bytes.remaining());
            bytes.position(bytes.position() + bytes.remaining());
        } else {
            byte[] remaining = JieIO.read(bytes);
            write(remaining, 0, remaining.length);
        }
        return this;
    }

    /**
     * Builds to byte array and wraps as byte buffer. It is equivalent to:
     * <pre>
     *     return ByteBuffer.wrap(toByteArray());
     * </pre>
     *
     * @return wrapped byte buffer
     */
    public ByteBuffer toByteBuffer() {
        return ByteBuffer.wrap(toByteArray());
    }
}
