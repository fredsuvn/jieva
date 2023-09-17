package xyz.srclab.common.net;

import xyz.srclab.annotations.Nullable;

import java.nio.ByteBuffer;

/**
 * Network channel callback handler.
 * <p>
 * An implementation of {@link FsTcpServer} has a list of {@link FsNetChannelHandler}.
 * <p>
 * When the server receives new data from remote point, the data will be wrapped as {@link ByteBuffer} and passed to
 * {@link #onMessage(FsNetChannel, Object)} of first handler. Returned object from first handler will be passed to next
 * handler as {@code message}, and so on.
 * If null is returned, the handlers calling chain will be broken;
 * if an exception is thrown, {@link FsNetServerHandler#onException(FsNetChannel, Throwable)} will be called
 * and the handlers calling chain will also be broken.
 * <p>
 * {@link #onMessage(FsNetChannel, Object)} only be called when new data was received, for more detailed operations,
 * try {@link #onLoop(FsNetChannel, boolean)}.
 *
 * @param <M> message type
 * @author fredsuvn
 */
public interface FsNetChannelHandler<M> {

    /**
     * Callback for each read loop of channel.
     * <p>
     * Server has a list of handlers, if there has new data read from remote endpoint,
     * {@link #onMessage(FsNetChannel, Object)} will be call for each handler.
     * <b>Only after onMessage of all handlers are called</b>, this method will be called for each handler.
     * <p>
     * Similar to calling of {@link #onMessage(FsNetChannel, Object)}, if an exception is thrown,
     * {@link FsNetServerHandler#onException(FsNetChannel, Throwable)} will be called
     * and the calling chain will be broken.
     *
     * @param channel    the channel
     * @param hasNewData whether there has new data read in current loop
     */
    default void onLoop(FsNetChannel channel, boolean hasNewData) {
    }

    /**
     * Callback when new data has received.
     * <p>
     * Server has a list of handlers,
     * the first handler will be passed a {@link ByteBuffer} as {@code message} parameter,
     * then returned object from first handler will be passed to next handler as {@code message} parameter, and so on.
     * If null is returned from any handler, the handlers calling chain will be broken at that handler;
     * if an exception is thrown, {@link FsNetServerHandler#onException(FsNetChannel, Throwable)} will be called
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
     * or using {@link #onLoop(FsNetChannel, boolean)} to deal with un-consumed data.
     *
     * @param channel the channel
     * @param message the message
     */
    @Nullable
    Object onMessage(FsNetChannel channel, M message);
}
