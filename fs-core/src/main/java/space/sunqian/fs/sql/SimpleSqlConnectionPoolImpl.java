package space.sunqian.fs.sql;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.object.pool.SimplePool;

import java.sql.Connection;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

final class SimpleSqlConnectionPoolImpl implements SimpleSqlConnectionPool {

    private final @Nonnull SimplePool<@Nonnull Connection> pool;
    // private final @Nonnull ConnectionWrapperFactory connectionWrapperFactory;

    SimpleSqlConnectionPoolImpl(
        @Nonnull String url,
        @Nullable String username,
        @Nullable String password,
        @Nonnull String driver,
        @Nonnull ConnectionFactory connectionFactory,
        @Nonnull ConnectionWrapperFactory connectionWrapperFactory,
        @Nonnull Consumer<@Nonnull Connection> closer,
        @Nonnull Predicate<@Nonnull Connection> validator,
        int coreSize,
        int maxSize,
        @Nonnull Duration idleTimeout
    ) {
        // this.connectionWrapperFactory = connectionWrapperFactory;
        Fs.uncheck(() -> Class.forName(driver));
        pool = SimplePool.<Connection>newBuilder()
            .coreSize(coreSize)
            .maxSize(maxSize)
            .idleTimeout(idleTimeout)
            .supplier(() -> {
                Connection conn = connectionFactory.create(driver, url, username, password);
                return connectionWrapperFactory.wrap(conn, this);
            })
            .destroyer(closer)
            .validator(validator)
            .build();
    }

    @Override
    public @Nullable Connection get() throws SqlRuntimeException {
        return Fs.uncheck(pool::get, SqlRuntimeException::new);
    }

    @Override
    public void clean() throws SqlRuntimeException {
        Fs.uncheck(pool::clean, SqlRuntimeException::new);
    }

    @Override
    public void close() throws SqlRuntimeException {
        Fs.uncheck(pool::close, SqlRuntimeException::new);
    }

    @Override
    public @Nonnull Map<@Nonnull Connection, ? extends @Nonnull Throwable> closeAll() {
        return pool.closeAll();
    }

    @Override
    public boolean isClosed() {
        return pool.isClosed();
    }

    @Override
    public int size() {
        return pool.size();
    }

    @Override
    public int idleSize() {
        return pool.idleSize();
    }

    @Override
    public int activeSize() {
        return pool.activeSize();
    }

    @Override
    public boolean release(@Nonnull Connection obj) throws SqlRuntimeException {
        return Fs.uncheck(() -> pool.release(obj), SqlRuntimeException::new);
    }

    @Override
    public @Nonnull List<@Nonnull Connection> unreleasedObjects() {
        return pool.unreleasedObjects();
    }
}
