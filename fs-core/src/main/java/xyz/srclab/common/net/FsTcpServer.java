package xyz.srclab.common.net;

import xyz.srclab.annotations.Nullable;

import java.time.Duration;

/**
 * TCP/IP server.
 *
 * @author fredsuvn
 */
public interface FsTcpServer {

    /**
     * Starts the server and wait until the server has been closed.
     * This method is equivalent to {@link #start(boolean)}:
     * <pre>
     *     start(true);
     * </pre>
     */
    default void start() {
        start(true);
    }

    /**
     * Starts the server.
     * If given {@code block} is true, this method will block current thread until the server has been closed.
     *
     * @param block whether block current thread
     */
    void start(boolean block);

    /**
     * Returns whether this server is alive.
     */
    boolean isAlive();

    /**
     * Closes this server.
     * It will wait all buffered operations has been flushed for given timeout, or keep waiting if the timeout is null.
     *
     * @param timeout given timeout
     */
    void close(@Nullable Duration timeout);

    /**
     * Closes this server immediately, without waiting the buffered operations to be flushed.
     */
    void closeNow();
}
