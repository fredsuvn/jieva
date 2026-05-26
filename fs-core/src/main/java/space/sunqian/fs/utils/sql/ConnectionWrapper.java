package space.sunqian.fs.utils.sql;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.object.pool.SimplePool;

import java.sql.Connection;

/**
 * Wrapper for {@link Connection}, used to adapt the connection to the pool.
 *
 * @author sunqian
 */
public interface ConnectionWrapper extends Connection {

    /**
     * Creates a new {@link ConnectionWrapper} instance with the given origin connection and pool.
     *
     * @param origin the origin connection to wrap
     * @param pool   the object pool to use for the wrapped connection
     * @return the new {@link ConnectionWrapper} instance
     * @throws SqlRuntimeException if failed to create the {@link ConnectionWrapper} instance
     */
    static @Nonnull ConnectionWrapper newConnectionWrapper(
        @Nonnull Connection origin,
        @Nonnull SimplePool<Connection> pool
    ) throws SqlRuntimeException {
        return AsmConnectionWrapperFactory.INST.wrap(origin, pool);
    }

    /**
     * Returns the wrapped {@link Connection}.
     *
     * @return the wrapped connection
     */
    @Nonnull
    Connection getWrappedConnection();
}
