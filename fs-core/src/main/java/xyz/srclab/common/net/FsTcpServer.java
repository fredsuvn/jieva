package xyz.srclab.common.net;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;

import java.time.Duration;
import java.util.List;

/**
 * TCP/IP server interface.
 * <p>
 * A {@link FsTcpServer} implementation should support following types of handler:
 * <ul>
 *     <li>
 *         one {@link FsNetServerHandler}: to deal with server actions;
 *     </li>
 *     <li>
 *         a list of {@link FsNetServerHandler}: to deal with actions for each connection;
 *     </li>
 * </ul>
 * These handlers are usually set into {@link Builder} (or its subtype)
 * to build final implementation instance.
 *
 * @param <S> underlying server type
 * @author fredsuvn
 */
public interface FsTcpServer<S> {

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

    /**
     * Returns underlying server type.
     */
    S getServer();

    /**
     * Builder for {@link FsTcpServer}.
     *
     * @param <C> underlying channel type
     * @param <S> underlying server type
     * @param <T> type of this server
     * @param <B> type of this server builder
     */
    abstract class Builder<C, S, T extends FsTcpServer<S>, B extends Builder<C, S, T, B>> {

        protected @Nullable FsNetServerHandler<S> serverHandler;
        protected @Nullable List<FsNetChannelHandler<C, ?>> channelHandlers;

        /**
         * Sets  server handler.
         *
         * @param serverHandler server handler
         */
        public B serverHandler(FsNetServerHandler<S> serverHandler) {
            this.serverHandler = serverHandler;
            return Fs.as(this);
        }

        /**
         * Sets channel handlers.
         *
         * @param channelHandlers channel handlers
         */
        public B channelHandlers(List<FsNetChannelHandler<C, ?>> channelHandlers) {
            this.channelHandlers = channelHandlers;
            return Fs.as(this);
        }

        /**
         * Builds the server.
         */
        public abstract T build();
    }
}
