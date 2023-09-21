package xyz.srclab.common.net.handlers;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.net.FsTcpChannel;
import xyz.srclab.common.net.FsTcpChannelHandler;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * Length based handler implementation, to split passed byte buffer to data segment in length:
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
 */
public class LengthBasedTcpChannelHandler implements FsTcpChannelHandler<ByteBuffer> {

    private final int lengthOffset;
    private final int lengthSize;

    /**
     * Constructs with given fixed length.
     * <p>
     * This constructor will set the handler split passed byte buffer into segments in fixed length.
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
     * This constructor will set the handler split passed byte buffer into segments in dynamic length.
     * The actual length value is specified at offset ({@code lengthOffset}) of buffer, and the {@code lengthSize}
     * specifies width of {@code lengthOffset} (must in 1, 2, 4).
     * <b>The actual length value must &lt;= {@link Integer#MAX_VALUE}</b>
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
        return lengthSize == -1 ? onFixed(channel, message) : onDynamic(channel, message);
    }

    private @Nullable Object onFixed(FsTcpChannel channel, ByteBuffer message) {
        if (message.remaining() < lengthOffset) {
            return null;
        }
        List<ByteBuffer> result = new LinkedList<>();
        while (message.remaining() >= lengthOffset) {
            byte[] bytes = new byte[lengthOffset];
            message.get(bytes);
            result.add(ByteBuffer.wrap(bytes));
        }
        return result;
    }

    private @Nullable Object onDynamic(FsTcpChannel channel, ByteBuffer message) {
        int minSize = lengthOffset + lengthSize;
        if (message.remaining() < minSize) {
            return null;
        }
        List<ByteBuffer> result = new LinkedList<>();
        while (true) {
            message.mark();
            message.position(lengthOffset);
            int length = readLength(message);
            message.reset();
            if (message.remaining() < length) {
                break;
            }
            byte[] bytes = new byte[length];
            message.get(bytes);
            result.add(ByteBuffer.wrap(bytes));
            if (message.remaining() < minSize) {
                break;
            }
        }
        return result.isEmpty() ? null : result;
    }

    private int readLength(ByteBuffer message) {
        switch (lengthSize) {
            case 1:
                return message.get() & 0x000000ff;
            case 2:
                return message.getShort() & 0x0000ffff;
            case 4:
                return message.getInt();
        }
        throw new IllegalArgumentException("lengthSize must in (1, 2, 4).");
    }
}
