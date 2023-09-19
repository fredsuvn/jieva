package xyz.srclab.common.net;

import xyz.srclab.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.function.IntFunction;

/**
 * Network utilities.
 *
 * @author fredsuvn
 */
public class FsNet {

    /**
     * Puts given bytes into source buffer, grows source buffer if its capacity is not enough.
     * If source buffer is null, return new buffer with {@link ByteBuffer#wrap(byte[])}.
     * If the remaining capacity of source buffer is enough to put (capacity - limit >= bytes.length), the given bytes
     * will be put into source buffer and source buffer will be returned.
     * Otherwise, a new buffer will be created from given buffer generator, and remaining of source buffer and bytes
     * will be put into the new buffer and the new buffer will be returned.
     * <p>
     * Position of returned buffer is 0 and limit is remaining + bytes.length.
     *
     * @param source          source buffer
     * @param bytes           given bytes
     * @param bufferGenerator given buffer generator
     */
    public static ByteBuffer growBuffer(
        @Nullable ByteBuffer source, byte[] bytes, IntFunction<ByteBuffer> bufferGenerator) {
        if (source == null) {
            return ByteBuffer.wrap(bytes);
        }
        if (source.capacity() - source.limit() >= bytes.length) {
            source.compact();
            source.put(bytes);
            source.flip();
            return source;
        }
        ByteBuffer buffer = bufferGenerator.apply(source.remaining() + bytes.length);
        buffer.put(source);
        buffer.put(bytes);
        buffer.flip();
        return buffer;
    }

    /**
     * Compacts source buffer.
     * {@link ByteBuffer#compact()} will be called first for source buffer.
     * If source buffer's capacity &lt;= {@code maxSize} or its remaining >= {@code maxSize},
     * source buffer will be returned directly.
     * Otherwise, a new buffer with capacity of {@code maxSize} will be created by given buffer generator,
     * and dat of source buffer will be put into the new buffer and the new buffer will be returned.
     * <p>
     * Position of returned buffer is 0 and limit is remaining of source buffer.
     *
     * @param source          source buffer
     * @param maxSize         max size after Compacting
     * @param bufferGenerator given buffer generator
     */
    public static ByteBuffer compactBuffer(ByteBuffer source, int maxSize, IntFunction<ByteBuffer> bufferGenerator) {
        source.compact();
        source.flip();
        if (source.capacity() <= maxSize || source.remaining() >= maxSize) {
            return source;
        }
        ByteBuffer buffer = bufferGenerator.apply(maxSize);
        buffer.put(source);
        buffer.flip();
        return buffer;
    }
}
