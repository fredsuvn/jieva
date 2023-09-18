package xyz.srclab.common.net;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.data.FsData;

import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.time.Duration;

/**
 * This class represents a TCP/IP channel connects the local and remote endpoint.
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
     * Closes this channel.
     * It will wait all buffered data has been flushed in given timeout (or always waiting if the timeout is null).
     *
     * @param timeout given timeout
     */
    void close(@Nullable Duration timeout);

    /**
     * Closes this channel immediately, other operations in processing will in forced interruption.
     */
    void closeNow();

    /**
     * Writes data to remote endpoint.
     * The written data may be buffered before the {@link #flush()} is called.
     */
    void send(FsData data);

    /**
     * Flushes buffered data to be written to remote endpoint.
     */
    void flush();

    /**
     * Returns buffered data read from remote endpoint.
     */
    ByteBuffer getBuffer();

    /**
     * Returns underlying object which implements {@link FsTcpChannel} interface, such as {@link Socket}.
     */
    Object getSource();
}
