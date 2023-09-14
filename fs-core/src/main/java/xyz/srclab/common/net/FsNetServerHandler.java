package xyz.srclab.common.net;

import xyz.srclab.annotations.Nullable;

/**
 * Network handler for server.
 *
 * @author fredsuvn
 */
public interface FsNetServerHandler<S> {

    /**
     * Handler for starting the server.
     *
     * @param server    the server
     * @param exception exception when starting,  null if no exception occurs
     */
    void onStart(S server, @Nullable Throwable exception);

    /**
     * Handler for closing the server.
     *
     * @param server    the server
     * @param exception exception when closing,  null if no exception occurs
     */
    void onClose(S server, @Nullable Throwable exception);

    /**
     * Handler for an exception occurs on the server.
     *
     * @param server    the server
     * @param exception the exception
     */
    void onException(S server, Throwable exception);
}
