package space.sunqian.fs.utils.sql;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.Checker;
import space.sunqian.fs.object.pool.SimplePool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Simple SQL pool interface that provides methods for acquiring and releasing database connections. This pool is built
 * on top of {@link SimplePool} and does not introduce any third-party dependencies.
 * <p>
 * Example usage:
 * <pre>{@code
 * SimpleSqlConnectionPool pool = SimpleSqlConnectionPool.newBuilder()
 *     .url("jdbc:h2:mem:test")
 *     .username("sa")
 *     //.password("password")
 *     .driverClassName("org.h2.Driver")
 *     .coreSize(5)
 *     .maxSize(20)
 *     .idleTimeout(Duration.ofMinutes(5))
 *     .build();
 *
 * try {
 *     // acquire a connection from the pool
 *     Connection connection = pool.get();
 * } finally {
 *     // release the connection back to the pool, not actually close it
 *     connection.close();
 * }
 * }</pre>
 *
 * @author sunqian
 */
public interface SimpleSqlConnectionPool extends SimplePool<Connection> {

    /**
     * Returns a builder for {@link SimpleSqlConnectionPool}.
     *
     * @return a builder for {@link SimpleSqlConnectionPool}
     */
    static @Nonnull Builder newBuilder() {
        return new Builder();
    }

    /**
     * Acquires a database connection from the pool, or {@code null} if no connection is available.
     * <p>
     * If any exception occurs during the acquisition process, {@link #close()} will be invoked to close this pool.
     *
     * @return the acquired connection, or {@code null} if no connection is available
     * @throws SqlRuntimeException if failed to acquire connection
     */
    @Override
    @Nullable
    Connection get() throws SqlRuntimeException;

    /**
     * Cleans the pool, removing idle connections that have timed out or been invalidated, or over the core size, adding
     * new connections up to the core size if necessary. The active connections will not be cleaned.
     * <p>
     * If any exception occurs during the clean process, {@link #close()} will be invoked to close this pool.
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
     * Releases the given connection back to the pool. This method will not close the connection, but will return it to
     * the pool for reuse later. This method has the same effect as {@link Connection#close()} of the {@link Connection}
     * instances which come from this pool.
     *
     * @param obj the connection to release
     * @return {@code true} if the connection is successfully released, {@code false} otherwise
     * @throws SqlRuntimeException if any exception occurs during the release process
     */
    @Override
    boolean release(@Nonnull Connection obj) throws SqlRuntimeException;

    /**
     * Returns the list of unreleased connections after the pool is closed, including idle connections and active
     * connections. If the pool is not closed, this method will return an empty list.
     *
     * @return the list of unreleased connections after the pool is closed, or an empty list if the pool is not closed
     */
    @Override
    @Nonnull
    List<@Nonnull Connection> unreleasedObjects();

    /**
     * Factory interface for creating database connections.
     * <p>
     * This interface is typically used to create underlying database connections.
     */
    interface ConnectionFactory {

        /**
         * Creates a new connection.
         *
         * @param driverClassName the driver class name
         * @param url             the JDBC URL
         * @param username        the username, default is {@code null}
         * @param password        the password, default is {@code null}
         * @return a new connection
         * @throws SqlRuntimeException if failed to create
         */
        @Nonnull
        Connection create(
            @Nonnull String driverClassName,
            @Nonnull String url,
            @Nullable String username,
            @Nullable String password
        ) throws SqlRuntimeException;
    }

    /**
     * Factory interface for wrapping original database connection.
     * <p>
     * This interface is typically used to wrap the underlying database connection with additional pooling behavior.
     */
    interface ConnectionWrapperFactory {

        /**
         * Wraps the original database connection with additional pooling behavior.
         *
         * @param origin the original database connection
         * @param pool   the connection pool for the original connection
         * @return the wrapped connection
         * @throws SqlRuntimeException if failed to wrap
         */
        @Nonnull
        ConnectionWrapper wrap(
            @Nonnull Connection origin, @Nonnull SimplePool<Connection> pool
        ) throws SqlRuntimeException;
    }

    /**
     * Builder class for {@link SimpleSqlConnectionPool}.
     */
    class Builder {

        /**
         * Default core size of the pool: {@code 5}.
         */
        public static final int DEFAULT_CORE_SIZE = 5;
        /**
         * Default max size of the pool: {@code 10}.
         */
        public static final int DEFAULT_MAX_SIZE = 10;

        // default connection closer
        private static final @Nonnull Consumer<@Nonnull Connection> CLOSER = connection ->
            Fs.uncheck(connection::close, SqlRuntimeException::new);
        // default connection validation
        private static final @Nonnull Predicate<@Nonnull Connection> VALIDATOR = connection ->
            Fs.uncheck(() -> {
                ConnectionWrapper wrapper = (ConnectionWrapper) connection;
                return wrapper.getWrappedConnection().isValid(1);
            }, SqlRuntimeException::new);

        // JDBC configuration
        private @Nullable String url;
        private @Nullable String username;
        private @Nullable String password;
        private @Nullable String driver;

        // Pool configuration
        private int coreSize = DEFAULT_CORE_SIZE;
        private int maxSize = DEFAULT_MAX_SIZE;
        private @Nonnull Duration idleTimeout = Duration.ofMinutes(5);
        private @Nullable ConnectionFactory connectionFactory = null;
        private @Nullable ConnectionWrapperFactory connectionWrapperFactory = null;
        private @Nonnull Consumer<@Nonnull Connection> closer = CLOSER;
        private @Nonnull Predicate<@Nonnull Connection> validator = VALIDATOR;

        /**
         * Sets the JDBC URL for the database connection.
         *
         * @param url the JDBC URL
         * @return this builder
         */
        public @Nonnull Builder url(@Nonnull String url) {
            this.url = url;
            return this;
        }

        /**
         * Sets the username for the database connection, default is {@code null}.
         *
         * @param username the username
         * @return this builder
         */
        public @Nonnull Builder username(@Nullable String username) {
            this.username = username;
            return this;
        }

        /**
         * Sets the password for the database connection, default is {@code null}.
         *
         * @param password the password
         * @return this builder
         */
        public @Nonnull Builder password(@Nullable String password) {
            this.password = password;
            return this;
        }

        /**
         * Sets the JDBC driver class name.
         *
         * @param driver the JDBC driver class name
         * @return this builder
         */
        public @Nonnull Builder driverClassName(@Nullable String driver) {
            this.driver = driver;
            return this;
        }

        /**
         * Sets the core size of the connection pool. This is the minimum number of connections that will be maintained
         * in the pool. Default is {@link #DEFAULT_CORE_SIZE}.
         *
         * @param coreSize the core size of the connection pool
         * @return this builder
         * @throws IllegalArgumentException if {@code coreSize <= 0}
         */
        public @Nonnull Builder coreSize(int coreSize) throws IllegalArgumentException {
            Checker.checkArgument(coreSize > 0, "The coreSize must > 0.");
            this.coreSize = coreSize;
            return this;
        }

        /**
         * Sets the maximum size of the connection pool. This is the maximum number of connections that can be created
         * in the pool. Default is {@link #DEFAULT_MAX_SIZE}.
         *
         * @param maxSize the maximum size of the connection pool
         * @return this builder
         * @throws IllegalArgumentException if {@code maxSize < coreSize}
         */
        public @Nonnull Builder maxSize(int maxSize) throws IllegalArgumentException {
            Checker.checkArgument(maxSize >= coreSize, "The maxSize must >= coreSize.");
            this.maxSize = maxSize;
            return this;
        }

        /**
         * Sets the idle timeout for connections in the pool. Connections that have been idle for longer than this
         * duration will be eligible for removal during cleanup. Default is {@code 5 minutes}.
         *
         * @param idleTimeout the idle timeout for connections
         * @return this builder
         * @throws IllegalArgumentException if {@code idleTimeout <= 0}
         */
        public @Nonnull Builder idleTimeout(@Nonnull Duration idleTimeout) throws IllegalArgumentException {
            Checker.checkArgument(idleTimeout.toMillis() > 0, "The idleTimeout must > 0.");
            this.idleTimeout = idleTimeout;
            return this;
        }

        /**
         * Sets the connection factory for creating underlying database connections. By default, connections are created
         * using
         * <pre>{@code
         * DriverManager.getConnection(url, username, password);
         * }</pre>.
         *
         * @param connectionFactory the connection factory for creating underlying database connections
         * @return this builder
         */
        public @Nonnull Builder connectionFactory(@Nonnull ConnectionFactory connectionFactory) {
            this.connectionFactory = connectionFactory;
            return this;
        }

        /**
         * Sets the connection wrapper factory for wrapping underlying database connections. By default, connections are
         * wrapped with pooled behavior.
         *
         * @param connectionWrapperFactory the connection wrapper factory for wrapping underlying database connections
         * @return this builder
         */
        public @Nonnull Builder connectionWrapperFactory(@Nonnull ConnectionWrapperFactory connectionWrapperFactory) {
            this.connectionWrapperFactory = connectionWrapperFactory;
            return this;
        }

        /**
         * Sets the closer for closing database connections. By default, connections are closed using
         * <pre>{@code
         * connection.close();
         * }</pre>.
         *
         * @param closer the closer for closing database connections
         * @return this builder
         */
        public @Nonnull Builder closer(@Nonnull Consumer<@Nonnull Connection> closer) {
            this.closer = closer;
            return this;
        }

        /**
         * Sets the validator for checking connection validity. By default, connections are validated if
         * <pre>{@code
         * connection.isValid(1);
         * }</pre>.
         *
         * @param validator the validator for checking connection validity
         * @return this builder
         */
        public @Nonnull Builder validator(@Nonnull Predicate<@Nonnull Connection> validator) {
            this.validator = validator;
            return this;
        }

        /**
         * Builds and returns a new {@link SimpleSqlConnectionPool}.
         *
         * @return the built new {@link SimpleSqlConnectionPool}
         * @throws IllegalArgumentException if some configuration is invalid
         * @throws SqlRuntimeException      if failed to build the pool
         */
        public @Nonnull SimpleSqlConnectionPool build() throws IllegalArgumentException, SqlRuntimeException {
            if (url == null) {
                throw new IllegalArgumentException("The url for JDBC connection must be set.");
            }
            if (driver == null) {
                throw new IllegalArgumentException("The driver class name for JDBC connection must be set.");
            }
            return new SimpleSqlConnectionPoolImpl(
                url, username, password, driver,
                connectionFactory == null ? DefaultConnectionFactory.INST : connectionFactory,
                connectionWrapperFactory == null ? AsmConnectionWrapperFactory.INST : connectionWrapperFactory,
                closer, validator, coreSize, maxSize, idleTimeout
            );
        }

        private enum DefaultConnectionFactory implements ConnectionFactory {
            INST;

            @Override
            public synchronized @Nonnull Connection create(
                @Nonnull String driverClassName,
                @Nonnull String url,
                @Nullable String username,
                @Nullable String password
            ) throws SqlRuntimeException {
                return Fs.uncheck(() -> {
                        Class.forName(driverClassName);
                        Connection realConnection;
                        if (username != null && password != null) {
                            realConnection = DriverManager.getConnection(url, username, password);
                        } else if (username != null) {
                            realConnection = DriverManager.getConnection(url, username, null);
                        } else {
                            realConnection = DriverManager.getConnection(url);
                        }
                        return realConnection;
                    },
                    SqlRuntimeException::new);
            }
        }
    }
}