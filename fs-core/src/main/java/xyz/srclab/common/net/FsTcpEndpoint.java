package xyz.srclab.common.net;

import xyz.srclab.annotations.Nullable;

import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;

/**
 * This interface represent an endpoint on the TCP/IP network: server or client.
 *
 * @author fredsuvn
 * @see FsTcpServer
 * @see FsTcpClient
 */
public interface FsTcpEndpoint {

    /**
     * Returns whether this point is opened.
     */
    boolean isOpened();

    /**
     * Returns whether this point is closed.
     */
    boolean isClosed();

    /**
     * Closes this point.
     * It will wait all buffered operations has been flushed for given timeout, or keep waiting if the timeout is null.
     *
     * @param timeout given timeout
     */
    void close(@Nullable Duration timeout);

    /**
     * Closes this point immediately, without waiting the buffered operations to be flushed.
     */
    void closeNow();

    /**
     * Returns underlying object which implements this interface, such as {@link ServerSocket} or {@link Socket}.
     */
    Object getSource();
}
