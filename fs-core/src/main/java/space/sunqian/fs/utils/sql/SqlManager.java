package space.sunqian.fs.utils.sql;

import space.sunqian.annotation.Nonnull;

/**
 * This interface is the core service for sql operations. It mainly contains two services:
 * <ul>
 *     <li>{@link #connectionPool()}: Provides the JDBC connections to the database;</li>
 *     <li>{@link #newStatementBuilder()}: Provides the statement builder to build sql statements;</li>
 * </ul>
 *
 * @author sunqian
 */
public interface SqlManager {

    /**
     * Returns a new builder for building {@link SqlManager}.
     *
     * @return a new builder
     */
    static @Nonnull Builder newBuilder() {
        return new Builder();
    }

    /**
     * Returns the connection pool used by this {@link SqlManager}.
     *
     * @return the connection pool
     */
    @Nonnull
    SqlConnectionPool connectionPool();

    /**
     * Creates a new statement builder, using the connection pool returned by {@link #connectionPool()}.
     *
     * @return a new statement builder
     */
    @Nonnull
    SqlStatementBuilder newStatementBuilder();

    /**
     * The builder for building {@link SqlManager}.
     */
    class Builder {
    }
}
