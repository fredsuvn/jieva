package xyz.srclab.common.net.handlers;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.net.FsTcpChannel;
import xyz.srclab.common.net.FsTcpChannelHandler;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * Channel handler to read buffer to data segment of fixed length:
 * <pre>
 *     buffer -> data|data|data|..(remaining but not enough length)
 * </pre>
 * The returned object of {@link #onMessage(FsTcpChannel, ByteBuffer)} is {@link List}&lt;{@link ByteBuffer}>.
 */
public class SeparateBasedTcpChannelHandler implements FsTcpChannelHandler<ByteBuffer> {

    private final int length;

    /**
     * Constructs with given length.
     *
     * @param length given length
     */
    public SeparateBasedTcpChannelHandler(int length) {
        this.length = length;
    }

    @Override
    public @Nullable Object onMessage(FsTcpChannel channel, ByteBuffer message) {
        if (message.remaining() < length) {
            return null;
        }
        List<ByteBuffer> result = new LinkedList<>();
        while (message.remaining() >= length) {
            byte[] bytes = new byte[length];
            message.get(bytes);
            result.add(ByteBuffer.wrap(bytes));
        }
        return result;
    }
}
