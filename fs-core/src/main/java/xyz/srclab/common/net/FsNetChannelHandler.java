package xyz.srclab.common.net;

import xyz.srclab.annotations.Nullable;

import java.nio.ByteBuffer;

/**
 * Network callback handler for channel.
 * <p>
 * An implementation of {@link FsTcpServer} has a list of handlers.
 * When a new channel was created, {@link #onOpen(FsNetChannel)} will be called once;
 * when that channel was closed, {@link #onClose(FsNetChannel)} will also be called once.
 * <p>
 * When the channel receives new data from remote point, the data will be wrapped as {@link ByteBuffer} and passed to
 * {@link #onMessage(FsNetChannel, Object)} of first handler. Returned object from first handler will be passed to next
 * handler as {@code message}, and so on.
 * If null is returned, the handlers calling chain will be broken;
 * if an exception is thrown, {@link #onException(FsNetChannel, Throwable)} will be called on current handler and the
 * calling chain will also be broken.
 *
 * @param <C> underlying channel type
 * @param <M> message type
 * @author fredsuvn
 */
public interface FsNetChannelHandler<C, M> {

    /**
     * Callback when the channel was opened.
     *
     * @param channel the channel
     */
    default void onOpen(FsNetChannel<C> channel) {
    }

    /**
     * Callback when the channel was closed.
     *
     * @param channel the channel
     */
    default void onClose(FsNetChannel<C> channel) {
    }

    /**
     * Callback when an exception occurs in this handle.
     *
     * @param channel   the channel
     * @param throwable the exception
     */
    default void onException(FsNetChannel<C> channel, Throwable throwable) {
    }

    /**
     * Callback for each read loop of channel.
     * Note the callback of this method always <b>after</b> {@link #onMessage(FsNetChannel, Object)}.
     *
     * @param channel    the channel
     * @param hasNewData whether there has new data read in current loop
     */
    default void onLoop(FsNetChannel<C> channel, boolean hasNewData) {
    }

    /**
     * Callback when new data has received.
     * <p>
     * Server has a list of handlers,
     * the first handler will be passed a {@link ByteBuffer} as {@code message} parameter,
     * then returned object from first handler will be passed to next handler as {@code message} parameter, and so on.
     * If null is returned from any handler, the handlers calling chain will be broken at the handler;
     * if an exception is thrown, {@link #onException(FsNetChannel, Throwable)} will be called on that handler and the
     * calling chain will also be broken. The process like:
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
     *             channelHandler.onException(channel, e);
     *             break;
     *         }
     *     }
     * </pre>
     * If the {@link ByteBuffer} on this callback is not fully consumed (remaining() > 0), the remaining data will be
     * reserved to next calling. However, the handler chain will be called if and only if there has new data read from
     * the channel, it's best to try to consume as much as possible each time,
     * and using {@link #onLoop(FsNetChannel, boolean)} can deal with un-consumed data.
     *
     * @param channel the channel
     * @param message the message
     */
    @Nullable
    Object onMessage(FsNetChannel<C> channel, M message);
}
