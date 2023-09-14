package xyz.srclab.common.net;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.data.FsData;

import java.net.InetSocketAddress;
import java.time.Duration;

/**
 * TCP/IP client.
 *
 * @author fredsuvn
 */
public interface FsTcpClient {

    /**
     * Starts with given server address.
     *
     * @param address server address
     */
    void start(InetSocketAddress address);

    /**
     * Starts with given server host and port.
     *
     * @param host server host
     * @param port server port
     */
    default void start(String host, int port) {
        try {
            start(new InetSocketAddress(host, port));
        } catch (Exception e) {
            throw new FsNetException(e);
        }
    }

    /**
     * Returns whether this client is alive.
     */
    boolean isAlive();

    /**
     * Closes this client.
     * It will wait all buffered operations has been flushed for given timeout, or keep waiting if the timeout is null.
     *
     * @param timeout given timeout
     */
    void close(@Nullable Duration timeout);

    /**
     * Closes this client immediately, without waiting the buffered operations to be flushed.
     */
    void closeNow();

    /**
     * Sends data to remote server.
     */
    void send(FsData data);
}
