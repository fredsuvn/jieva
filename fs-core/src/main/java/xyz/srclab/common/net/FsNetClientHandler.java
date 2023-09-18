package xyz.srclab.common.net;

/**
 * Network handler for server actions.
 *
 * @author fredsuvn
 */
public interface FsNetClientHandler {

    /**
     * Callback when an exception occurs on the server.
     *
     * @param exception the exception for server
     */
    default void onException(FsNetEndpointException exception) {
    }

    /**
     * Callback when a new channel was opened.
     *
     * @param channel the channel
     */
    default void onOpen(FsNetChannel channel) {
    }

    /**
     * Callback when a channel was closed.
     *
     * @param channel the channel
     */
    default void onClose(FsNetChannel channel) {
    }

    /**
     * Callback when an exception occurs in this handle.
     *
     * @param channel   the channel
     * @param throwable the exception
     */
    default void onException(FsNetChannel channel, Throwable throwable) {
    }

    /**
     * Callback for each read loop of channel.
     * <p>
     * Server has a list of handlers, if there has new data read from remote endpoint,
     * {@link FsNetChannelHandler#onMessage(FsNetChannel, Object)} will be called for each handler.
     * <b>Only all onMessage method of handlers have been called</b>, then this method would be called.
     * <p>
     * If an exception is thrown, {@link #onException(FsNetChannel, Throwable)} will be called.
     *
     * @param channel    the channel
     * @param hasNewData whether there has new data read in current loop
     */
    default void onLoop(FsNetChannel channel, boolean hasNewData) {
    }
}
