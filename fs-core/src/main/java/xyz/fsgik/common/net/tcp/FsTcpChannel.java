package xyz.fsgik.common.net.tcp;

import xyz.fsgik.annotations.Nullable;
import xyz.fsgik.annotations.ThreadSafe;
import xyz.fsgik.common.data.FsData;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.time.Duration;

/**
 * This class represents a TCP/IP connection channel between local and remote endpoint.
 *
 * @author fredsuvn
 */
@ThreadSafe
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
     * Returns socket address of remote endpoint.
     */
    SocketAddress getRemoteSocketAddress();

    /**
     * Returns socket address of local endpoint.
     */
    SocketAddress getLocalSocketAddress();

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
     * Sends data to remote endpoint.
     * The written data may be buffered before the {@link #flush()} is called.
     */
    void send(FsData data);

    /**
     * Sends data to remote endpoint.
     * The written data may be buffered before the {@link #flush()} is called.
     *
     * @param data the data
     */
    void send(byte[] data);

    /**
     * Sends data of specified length from given offset to remote endpoint.
     * The written data may be buffered before the {@link #flush()} is called.
     *
     * @param data   the data
     * @param offset given offset
     * @param length specified length
     */
    void send(byte[] data, int offset, int length);

    /**
     * Sends data to remote endpoint.
     * The written data may be buffered before the {@link #flush()} is called.
     *
     * @param data the data
     */
    void send(ByteBuffer data);

    /**
     * Sends data to remote endpoint.
     * The written data may be buffered before the {@link #flush()} is called.
     *
     * @param data the data
     */
    void send(InputStream data);

    /**
     * Sends data to remote endpoint and flushes immediately.
     *
     * @param data the data
     */
    default void sendAndFlush(FsData data) {
        send(data);
        flush();
    }

    /**
     * Sends data to remote endpoint and flushes immediately.
     *
     * @param data the data
     */
    default void sendAndFlush(byte[] data) {
        send(data);
        flush();
    }

    /**
     * Sends data of specified length from given offset to remote endpoint and flushes immediately.
     *
     * @param data   the data
     * @param offset given offset
     * @param length specified length
     */
    default void sendAndFlush(byte[] data, int offset, int length) {
        send(data, offset, length);
        flush();
    }

    /**
     * Sends data to remote endpoint and flushes immediately.
     *
     * @param data the data
     */
    default void sendAndFlush(ByteBuffer data) {
        send(data);
        flush();
    }

    /**
     * Sends data to remote endpoint and flushes immediately.
     *
     * @param data the data
     */
    default void sendAndFlush(InputStream data) {
        send(data);
        flush();
    }

    /**
     * Flushes buffered data to be written to remote endpoint.
     */
    void flush();

    /**
     * Returns underlying object which implements {@link FsTcpChannel} interface, such as {@link Socket}.
     */
    Object getSource();
}
