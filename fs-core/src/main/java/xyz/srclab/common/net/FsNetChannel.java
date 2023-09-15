package xyz.srclab.common.net;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.data.FsData;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.time.Duration;

/**
 * Channel of a network connection.
 *
 * @param <C> underlying channel type
 * @author fredsuvn
 */
public interface FsNetChannel<C> {

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

    /**
     * Returns buffer of this channel.
     */
    ByteBuffer getBuffer();

    /**
     * Returns underlying channel type.
     */
    C getChannel();
}
