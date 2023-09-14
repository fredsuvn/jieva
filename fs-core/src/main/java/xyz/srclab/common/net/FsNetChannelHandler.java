package xyz.srclab.common.net;

import xyz.srclab.annotations.Nullable;

import java.nio.ByteBuffer;

/**
 * Network callback handler for channel.
 *
 * @param <C> underlying channel type
 * @param <M> message type
 * @author fredsuvn
 */
public interface FsNetChannelHandler<C, M> {

    /**
     * Handler for the remote opens the channel.
     *
     * @param channel the channel
     */
    default void onOpen(FsNetChannel<C> channel) {
    }

    /**
     * Handler for the remote closes the channel.
     *
     * @param channel the channel
     */
    default void onClose(FsNetChannel<C> channel, M message) {
    }

    default void onException(FsNetChannel<C> channel) {

    }

    /**
     * Handler for receiving a message from remote.
     * <p>
     * Server has a list of handlers,
     * the first handler will be passed a {@link ByteBuffer} as {@code message} parameter,
     * then returned object from first handler will be passed to next handler as {@code message} parameter, and so on:
     * <pre>
     *     Object message = ByteBuffer.wrap(...);
     *     for (FsNetChannelHandler<?> channelHandler : channelHandlers) {
     *         Object result = channelHandler.onMessage(channel, message);
     *         message = result;
     *     }
     * </pre>
     * Note:
     * <ul>
     *     <li>
     *         If one handler returns null, the chain calling will be broken.
     *     </li>
     *     <li>
     *         If the contents of the first {@link ByteBuffer} are not fully consumed (remaining() > 0),
     *         the remaining data will be saved for use in the next callback.
     *     </li>
     * </ul>
     *
     * @param channel the channel
     * @param message the message
     */
    @Nullable
    Object onMessage(FsNetChannel<C> channel, M message, @Nullable Throwable exception);
}
