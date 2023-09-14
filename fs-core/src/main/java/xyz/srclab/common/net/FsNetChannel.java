package xyz.srclab.common.net;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.data.FsData;

import java.net.InetSocketAddress;
import java.time.Duration;

/**
 * Channel info about current connection.
 *
 * @author fredsuvn
 */
public interface FsNetChannel {

    /**
     * Returns remote address.
     */
    InetSocketAddress getRemoteAddress();

    /**
     * Returns host/current/local address of this channel.
     */
    InetSocketAddress getHostAddress();

    /**
     * Returns whether this channel is alive.
     */
    boolean isAlive();

    /**
     * Closes this channel.
     * It will wait all buffered data has been flushed for given timeout, or keep waiting if the timeout is null.
     *
     * @param timeout given timeout
     */
    void close(@Nullable Duration timeout);

    /**
     * Closes this channel immediately, without waiting the buffered data to be flushed.
     */
    void closeNow();

    /**
     * Sends data to remote of this channel.
     */
    void send(FsData data);

    /**
     * Flushes buffered data to be sent.
     */
    void flush();
}
