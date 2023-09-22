package xyz.srclab.common.net.tcp;

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
     * Closes this point, blocks and waits for buffered operations.
     */
    default void close() {
        close(null);
    }

    /**
     * Closes this point, blocks and waits for buffered operations in given timeout.
     *
     * @param timeout given timeout
     */
    void close(@Nullable Duration timeout);

    /**
     * Closes this point immediately, without blocking and waiting for buffered operations.
     */
    void closeNow();

    /**
     * Returns underlying object which implements this interface, such as {@link ServerSocket} or {@link Socket}.
     */
    Object getSource();
}
