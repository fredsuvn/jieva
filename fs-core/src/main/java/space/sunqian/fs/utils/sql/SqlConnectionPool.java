package space.sunqian.fs.utils.sql;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.object.pool.SimplePool;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * Connection pool interface, sub-interface of {@link SimplePool}{@code <}{@link Connection}{@code >}, provides methods
 * for pooling database connections.
 *
 * @author sunqian
 */
public interface SqlConnectionPool extends SimplePool<Connection> {

    /**
     * Gets a connection from the pool, or {@code null} if no connection is available.
     * <p>
     * If any exception occurs during this operation, {@link #close()} will be invoked to close this pool and the
     * {@link #unreleasedObjects()} will return the list of unreleased connections, including idle connections and
     * active connections.
     *
     * @return the connection from the pool, or {@code null} if no connection is available
     * @throws SqlRuntimeException if failed to get connection
     */
    @Override
    @Nullable
    Connection get() throws SqlRuntimeException;

    /**
     * Releases the given active connection to the pool. Returns {@code true} if the connection is released
     * successfully, {@code false} otherwise. If the connection is not acquired from this pool, this method will do
     * nothing just return {@code false}.
     * <p>
     * If any exception occurs during the release process, {@link #close()} will be invoked to close this pool and the
     * {@link #unreleasedObjects()} will return the list of unreleased connections, including idle connections and
     * active connections.
     *
     * @param obj the given active connection to release
     * @return {@code true} if the connection is released successfully, {@code false} otherwise
     * @throws SqlRuntimeException if failed to release connection
     */
    @Override
    boolean release(@Nonnull Connection obj) throws SqlRuntimeException;

    /**
     * Cleans the pool, removing idle connections that idle timeout or invalidated. If the current pool has a specified
     * core size and the idle connections count is over or less than the core size, removing idle connections or adding
     * new prepared connections up to the core size. The active connections will not be cleaned.
     * <p>
     * If any exception occurs during the clean process, {@link #close()} will be invoked to close this pool and the
     * {@link #unreleasedObjects()} will return the list of unreleased connections, including idle connections and
     * active connections.
     *
     * @throws SqlRuntimeException if any exception occurs during the clean process
     */
    @Override
    void clean() throws SqlRuntimeException;

    /**
     * Closes the pool. The idle connections will be closed, but the active connections will not be closed. If this
     * process is failed, no error thrown, and the pool will be in a closed state. The {@link #unreleasedObjects()} will
     * return the list of unreleased connections, including idle connections and active connections.
     */
    @Override
    void close();

    /**
     * Closes the pool and closes all connections, including idle and active connections. Returns a {@link Map}
     * containing connections that were not closed normally due to an exception. If there is no error occurs during the
     * close processing, an empty {@link Map} will be returned.
     *
     * @return a {@link Map} containing connections that were not closed normally due to an exception
     */
    @Override
    @Nonnull
    Map<@Nonnull Connection, ? extends @Nonnull Throwable> closeAll();

    /**
     * Returns {@code true} if this pool is closed, {@code false} otherwise.
     *
     * @return {@code true} if this pool is closed, {@code false} otherwise
     */
    @Override
    boolean isClosed();

    /**
     * Returns the total number of connections in this pool, including both idle and active connections.
     *
     * @return the total number of connections in this pool
     */
    @Override
    int size();

    /**
     * Returns the number of idle connections in this pool.
     *
     * @return the number of idle connections in this pool
     */
    @Override
    int idleSize();

    /**
     * Returns the number of active connections in this pool. If the pool is closed, this method will return the number
     * of unreleased active connections.
     *
     * @return the number of active connections in this pool
     */
    @Override
    int activeSize();

    /**
     * Returns the list of unreleased connections after the pool is closed, including idle connections and active
     * connections. If the pool is not closed, this method will return an empty list.
     *
     * @return the list of unreleased connections after the pool is closed, or an empty list if the pool is not closed
     */
    @Override
    @Nonnull
    List<@Nonnull Connection> unreleasedObjects();
}
