package xyz.srclab.common.net;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.net.handlers.LengthBasedTcpChannelHandler;
import xyz.srclab.common.net.handlers.SeparateBasedTcpChannelHandler;

import java.nio.ByteBuffer;

/**
 * TCP/IP network channel callback handler.
 * <p>
 * An implementation of {@link FsTcpServer} has a list of {@link FsTcpChannelHandler}.
 * <p>
 * When the server receives new data from remote point, the data will be wrapped as {@link ByteBuffer} and passed to
 * {@link #onMessage(FsTcpChannel, Object)} of first handler. Returned object from first handler will be passed to next
 * handler as {@code message}, and so on.
 * If null is returned, the handlers calling chain will be broken;
 * if an exception is thrown, {@link FsTcpServerHandler#onException(FsTcpChannel, Throwable, ByteBuffer)} will be called
 * and the handlers calling chain will also be broken.
 * <p>
 * {@link #onMessage(FsTcpChannel, Object)} only be called when new data was received, for more detailed operations,
 * try {@link FsTcpServerHandler#onLoop(FsTcpChannel, boolean, ByteBuffer)}.
 * <p>
 * There are built-in handler implementations:
 * <ul>
 *     <li>{@link LengthBasedTcpChannelHandler}: to deal with length based data;</li>
 *     <li>{@link SeparateBasedTcpChannelHandler}: to deal with separate based data;</li>
 * </ul>
 *
 * @param <M> message type
 * @author fredsuvn
 * @see LengthBasedTcpChannelHandler
 * @see SeparateBasedTcpChannelHandler
 */
public interface FsTcpChannelHandler<M> {

    /**
     * Callback when new data has received.
     * <p>
     * Server has a list of handlers,
     * the first handler will be passed a {@link ByteBuffer} as {@code message} parameter,
     * then returned object from first handler will be passed to next handler as {@code message} parameter, and so on.
     * If null is returned from any handler, the handlers calling chain will be broken at that handler;
     * if an exception is thrown, {@link FsTcpServerHandler#onException(FsTcpChannel, Throwable, ByteBuffer)} will be called
     * and the calling chain will also be broken. The process like:
     * <pre>
     *     Object message = ByteBuffer.wrap(...);
     *     for (FsNetChannelHandler<...> channelHandler : channelHandlers) {
     *         try {
     *             Object result = channelHandler.onMessage(channel, message);
     *             if (result == null) {
     *                 break;
     *             }
     *             message = result;
     *         } catch (Throwable e) {
     *             serverHandler.onException(channel, e);
     *             break;
     *         }
     *     }
     * </pre>
     * If the {@link ByteBuffer} on this callback is not fully consumed (remaining() > 0), the remaining data will be
     * reserved to next calling. However, the handler chain will be called if and only if there has new data read from
     * the remote endpoint. It's best to try to consume as much as possible for each time,
     * or using {@link FsTcpServerHandler#onLoop(FsTcpChannel, boolean, ByteBuffer)} to deal with un-consumed data.
     *
     * @param channel the channel
     * @param message the message
     */
    @Nullable
    Object onMessage(FsTcpChannel channel, M message);
}
