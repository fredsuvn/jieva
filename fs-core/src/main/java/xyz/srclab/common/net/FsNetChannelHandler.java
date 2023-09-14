package xyz.srclab.common.net;

import xyz.srclab.annotations.Nullable;

import java.nio.ByteBuffer;

/**
 * Network callback handler for channel. See:
 * <ul>
 *     <li>{@link #onMessage(FsNetChannel, Object)};</li>
 *     <li>{@link #onOpen(FsNetChannel)};</li>
 *     <li>{@link #onClose(FsNetChannel)};</li>
 *     <li>{@link #onException(FsNetChannel, Throwable)};</li>
 * </ul>
 *
 * @author fredsuvn
 */
public interface FsNetChannelHandler<M> {

    /**
     * Handler for the remote opens the channel.
     *
     * @param channel the channel
     */
    default void onOpen(FsNetChannel channel) {
    }

    /**
     * Handler for the remote closes the channel.
     *
     * @param channel the channel
     */
    default void onClose(FsNetChannel channel) {
    }

    /**
     * Handler for an exception occurs on the channel.
     *
     * @param channel   the channel
     * @param exception the exception
     */
    default void onException(FsNetChannel channel, Throwable exception) {
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
    Object onMessage(FsNetChannel channel, M message);
}
