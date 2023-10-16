package xyz.fsgik.common.net.tcp.handlers;

import xyz.fsgik.annotations.Nullable;
import xyz.fsgik.common.io.FsBuffer;
import xyz.fsgik.common.net.tcp.FsTcpChannel;
import xyz.fsgik.common.net.tcp.FsTcpChannelHandler;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

/**
 * Length based handler implementation, to split passed byte buffer in length:
 * <pre>
 *     buffer -&gt; data1data2data3..
 * </pre>
 * The returned object of {@link #onMessage(FsTcpChannel, ByteBuffer)} is {@link List}&lt;{@link ByteBuffer}&gt;,
 * each byte buffer is split in:
 * <ul>
 *     <li>
 *         Fixed length: created by {@link #LengthBasedTcpChannelHandler(int)}
 *         or {@link #LengthBasedTcpChannelHandler(int, IntFunction)},
 *         to split in fixed length with {@link FsBuffer#splitInLength(ByteBuffer, int)}
 *         and make returned buffers readonly;
 *     </li>
 *     <li>
 *         Specified length: created by {@link #LengthBasedTcpChannelHandler(int, int)}
 *         or {@link #LengthBasedTcpChannelHandler(int, int, IntFunction)},
 *         to split in specified length with {@link FsBuffer#splitInLength(ByteBuffer, int, int)}
 *         and make returned buffers readonly;
 *     </li>
 * </ul>
 *
 * @author fredsuvn
 */
public class LengthBasedTcpChannelHandler implements FsTcpChannelHandler<ByteBuffer> {

    private final int lengthOffset;
    private final int lengthSize;
    private final IntFunction<ByteBuffer> generator;

    /**
     * Constructs with given fixed length and using {@link ByteBuffer#allocate(int)} to create new buffer.
     *
     * @param length given fixed length
     */
    public LengthBasedTcpChannelHandler(int length) {
        this(length, ByteBuffer::allocate);
    }

    /**
     * Constructs with given fixed length and buffer generator.
     *
     * @param length    given fixed length
     * @param generator buffer generator, the function arguments is capacity
     */
    public LengthBasedTcpChannelHandler(int length, IntFunction<ByteBuffer> generator) {
        this.lengthOffset = length;
        this.lengthSize = -1;
        this.generator = generator;
    }

    /**
     * Constructs with given length offset, length size and using {@link ByteBuffer#allocate(int)} to create new buffer.
     *
     * @param lengthOffset given length offset
     * @param lengthSize   given length size
     */
    public LengthBasedTcpChannelHandler(int lengthOffset, int lengthSize) {
        this(lengthOffset, lengthSize, ByteBuffer::allocate);
    }

    /**
     * Constructs with given length offset, length size and buffer generator.
     *
     * @param lengthOffset given length offset
     * @param lengthSize   given length size
     * @param generator    buffer generator, the function arguments is capacity
     */
    public LengthBasedTcpChannelHandler(int lengthOffset, int lengthSize, IntFunction<ByteBuffer> generator) {
        if (lengthSize != 1 && lengthSize != 2 && lengthSize != 4) {
            throw new IllegalArgumentException("lengthSize must in (1, 2, 4).");
        }
        this.lengthOffset = lengthOffset;
        this.lengthSize = lengthSize;
        this.generator = generator;
    }

    @Override
    public @Nullable Object onMessage(FsTcpChannel channel, ByteBuffer message) {
        return lengthSize == -1 ? onFixed(channel, message) : onSpecified(channel, message);
    }

    private @Nullable Object onFixed(FsTcpChannel channel, ByteBuffer message) {
        List<ByteBuffer> result = FsBuffer.splitInLength(message, lengthOffset, generator);
        return result.isEmpty() ? null : asReadOnly(result);
    }

    private @Nullable Object onSpecified(FsTcpChannel channel, ByteBuffer message) {
        List<ByteBuffer> result = FsBuffer.splitInLength(message, lengthOffset, lengthSize, generator);
        return result.isEmpty() ? null : asReadOnly(result);
    }

    private List<ByteBuffer> asReadOnly(List<ByteBuffer> buffers) {
        List<ByteBuffer> result = new ArrayList<>(buffers.size());
        for (ByteBuffer buffer : buffers) {
            if (buffer.isReadOnly()) {
                result.add(buffer);
            } else {
                result.add(buffer.asReadOnlyBuffer());
            }
        }
        return result;
    }
}
