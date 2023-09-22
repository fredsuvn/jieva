package xyz.srclab.common.net.tcp;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.data.FsData;

import java.net.InetAddress;
import java.net.Socket;
import java.time.Duration;

/**
 * This class represents a TCP/IP connection channel between local and remote endpoint.
 *
 * @author fredsuvn
 */
public interface FsTcpChannel {

    /**
     * Returns address of remote endpoint.
     */
    InetAddress getRemoteAddress();

    /**
     * Returns port of remote endpoint.
     */
    int getRemotePort();

    /**
     * Returns address of local endpoint.
     */
    InetAddress getLocalAddress();

    /**
     * Returns port of remote endpoint.
     */
    int getLocalPort();

    /**
     * Returns whether this channel is opened.
     */
    boolean isOpened();

    /**
     * Returns whether this channel is closed.
     */
    boolean isClosed();

    /**
     * Closes this channel, blocks current thread for buffered operations.
     */
    default void close() {
        close(null);
    }

    /**
     * Closes this channel, blocks current thread for buffered operations in given timeout.
     *
     * @param timeout given timeout, maybe null to always wait
     */
    void close(@Nullable Duration timeout);

    /**
     * Closes this channel immediately, without blocking and buffered operations.
     */
    void closeNow();

    /**
     * Writes data to remote endpoint.
     * The written data may be buffered before the {@link #flush()} is called.
     */
    void send(FsData data);

    /**
     * Writes data to remote endpoint and flushes immediately.
     */
    void sendAndFlush(FsData data);

    /**
     * Flushes buffered data to be written to remote endpoint.
     */
    void flush();

    /**
     * Returns underlying object which implements {@link FsTcpChannel} interface, such as {@link Socket}.
     */
    Object getSource();
}
