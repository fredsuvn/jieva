package xyz.fs404.common.net.tcp;

import xyz.fs404.annotations.Nullable;
import xyz.fs404.common.net.FsNet;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Length based handler implementation, to split passed byte buffer in length:
 * <pre>
 *     buffer -> data|data|data|..
 * </pre>
 * The returned object of {@link #onMessage(FsTcpChannel, ByteBuffer)} is {@link List}&lt;{@link ByteBuffer}>,
 * each byte buffer is split data segment.
 * <p>
 * There are two length mode:
 * <ul>
 *     <li>
 *         Fixed length: created by {@link #LengthBasedTcpChannelHandler(int)}, to split with fixed length;
 *     </li>
 *     <li>
 *         Specified length: created by {@link #LengthBasedTcpChannelHandler(int, int)}, to split with specified length;
 *     </li>
 * </ul>
 *
 * @author fredsuvn
 */
public class LengthBasedTcpChannelHandler implements FsTcpChannelHandler<ByteBuffer> {

    private final int lengthOffset;
    private final int lengthSize;

    /**
     * Constructs with given fixed length.
     * <p>
     * This constructor will set the handler to split passed byte buffer in fixed length.
     *
     * @param length given fixed length
     */
    public LengthBasedTcpChannelHandler(int length) {
        this.lengthOffset = length;
        this.lengthSize = -1;
    }

    /**
     * Constructs with given length offset and length size.
     * <p>
     * This constructor will set the handler to split passed byte buffer in specified length.
     * The split length is specified at offset ({@code lengthOffset}) of buffer, and the {@code lengthSize}
     * specifies width of {@code lengthOffset} (must in 1, 2, 4).
     * <b>The split length value must &lt;= {@link Integer#MAX_VALUE}.</b>
     *
     * @param lengthOffset given length offset
     * @param lengthSize   given length size
     */
    public LengthBasedTcpChannelHandler(int lengthOffset, int lengthSize) {
        if (lengthSize != 1 && lengthSize != 2 && lengthSize != 4) {
            throw new IllegalArgumentException("lengthSize must in (1, 2, 4).");
        }
        this.lengthOffset = lengthOffset;
        this.lengthSize = lengthSize;
    }

    @Override
    public @Nullable Object onMessage(FsTcpChannel channel, ByteBuffer message) {
        return lengthSize == -1 ? onFixed(channel, message) : onSpecified(channel, message);
    }

    private @Nullable Object onFixed(FsTcpChannel channel, ByteBuffer message) {
        List<ByteBuffer> result = FsNet.splitWithFixedLength(message, lengthOffset);
        return result.isEmpty() ? null : result;
    }

    private @Nullable Object onSpecified(FsTcpChannel channel, ByteBuffer message) {
        List<ByteBuffer> result = FsNet.splitWithSpecifiedLength(message, lengthOffset, lengthSize);
        return result.isEmpty() ? null : result;
    }
}
