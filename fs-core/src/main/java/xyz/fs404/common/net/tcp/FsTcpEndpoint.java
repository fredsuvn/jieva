package xyz.fs404.common.net.tcp;

import xyz.fs404.annotations.Nullable;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
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
     * Returns bound address of this point.
     */
    InetAddress getAddress();

    /**
     * Returns bound port of this point.
     */
    int getPort();

    /**
     * Returns bound socket address of this point.
     */
    SocketAddress getSocketAddress();

    /**
     * Returns underlying object which implements this interface, such as {@link ServerSocket} or {@link Socket}.
     */
    Object getSource();

    /**
     * Returns whether this point is opened.
     */
    boolean isOpened();

    /**
     * Returns whether this point is closed.
     */
    boolean isClosed();

    /**
     * Closes this point, blocks current thread for buffered operations.
     */
    default void close() {
        close(null);
    }

    /**
     * Closes this point, blocks current thread for buffered operations in given timeout.
     *
     * @param timeout given timeout, maybe null to always wait
     */
    void close(@Nullable Duration timeout);

    /**
     * Closes this point immediately, without blocking and buffered operations.
     */
    void closeNow();
}
