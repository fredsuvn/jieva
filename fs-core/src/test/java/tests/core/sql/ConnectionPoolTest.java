package tests.core.sql;

import internal.annotations.J17Only;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.value.IntVar;
import space.sunqian.fs.base.value.Var;
import space.sunqian.fs.sql.ConnectionWrapper;
import space.sunqian.fs.sql.SimpleSqlConnectionPool;
import space.sunqian.fs.sql.SqlRuntimeException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@J17Only
public class ConnectionPoolTest {

    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:mem:" + ConnectionPoolTest.class.getName();
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    @Test
    public void testConnectionPoolBasicFunctionality() throws Exception {
        SimpleSqlConnectionPool pool = SimpleSqlConnectionPool.newBuilder()
            .driverClassName(DB_DRIVER)
            .url(DB_URL)
            .username(DB_USER)
            .password(DB_PASSWORD)
            .coreSize(2)
            .maxSize(3)
            .idleTimeout(Duration.ofDays(9999))
            .build();

        // Test getting connections
        Connection conn1 = pool.get();
        assertNotNull(conn1);
        Connection conn2 = pool.get();
        assertNotNull(conn2);
        Connection conn3 = pool.get();
        assertNotNull(conn3);
        Connection conn4 = pool.get();
        assertNull(conn4);

        // Test returning connection to pool
        conn1.close();
        Connection conn5 = pool.get();
        assertNotNull(conn5);
        assertSame(conn1, conn5);

        // release wrong connection
        Connection connX = ConnectionWrapper.newConnectionWrapper(null, null);
        assertFalse(pool.release(connX));

        // Test pool statistics
        pool.clean();
        assertEquals(3, pool.activeSize());
        assertEquals(0, pool.idleSize());
        assertEquals(3, pool.size());
        assertFalse(pool.isClosed());

        // Test releasing connection
        assertTrue(pool.release(conn5));
        assertEquals(2, pool.activeSize());
        assertEquals(1, pool.idleSize());
        assertEquals(3, pool.size());
        assertTrue(conn5.isClosed());
        assertFalse(pool.release(conn5));

        // Test closing pool
        conn2.close();
        pool.close();
        assertTrue(pool.isClosed());
        assertEquals(1, pool.activeSize());
        assertEquals(0, pool.idleSize());
        assertEquals(0, pool.size());
        List<Connection> unreleasedObjects = pool.unreleasedObjects();
        assertEquals(1, unreleasedObjects.size());
        ConnectionWrapper unreleasedConn = (ConnectionWrapper) unreleasedObjects.get(0);
        assertSame(conn3, unreleasedConn);
        assertInstanceOf(ConnectionWrapper.class, unreleasedConn);
        assertThrows(SqlRuntimeException.class, unreleasedConn::close);
        unreleasedConn.getWrappedConnection().close();
    }

    @Test
    public void testConnectionPoolWithCustomFactoryAndCloser() throws Exception {
        IntVar connCount = IntVar.of(0);
        IntVar closeCount = IntVar.of(0);

        Supplier<Connection> supplier = () -> Fs.uncheck(() -> {
            connCount.incrementAndGet();
            Class.forName(DB_DRIVER);
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }, SqlRuntimeException::new);

        Consumer<Connection> closer = conn -> Fs.uncheck(() -> {
            closeCount.incrementAndGet();
            conn.close();
        }, SqlRuntimeException::new);

        SimpleSqlConnectionPool pool = SimpleSqlConnectionPool.newBuilder()
            .driverClassName(DB_DRIVER)
            .url(DB_URL)
            .username(DB_USER)
            .password(DB_PASSWORD)
            .coreSize(2)
            .maxSize(3)
            .idleTimeout(Duration.ofSeconds(10))
            .connectionFactory(
                (driverClassName, url, username, password) -> supplier.get())
            .closer(closer)
            .validator(conn -> Fs.uncheck(() ->
                ((ConnectionWrapper) conn).getWrappedConnection().isValid(0), SqlRuntimeException::new))
            .build();

        // Test initial connection count
        assertEquals(2, connCount.get());
        assertEquals(0, closeCount.get());

        // Test getting connections
        Connection conn1 = pool.get();
        assertNotNull(conn1);
        assertEquals(2, connCount.get());
        assertEquals(0, closeCount.get());

        Connection conn2 = pool.get();
        assertNotNull(conn2);
        assertEquals(2, connCount.get());
        assertEquals(0, closeCount.get());

        Connection conn3 = pool.get();
        assertNotNull(conn3);
        assertEquals(3, connCount.get());
        assertEquals(0, closeCount.get());

        Connection conn4 = pool.get();
        assertNull(conn4);
        assertEquals(3, connCount.get());
        assertEquals(0, closeCount.get());

        // Test returning connection to pool
        conn1.close();
        Connection conn5 = pool.get();
        assertNotNull(conn5);
        assertSame(conn1, conn5);
        assertEquals(3, connCount.get());
        assertEquals(0, closeCount.get());

        // Test closing connections and pool
        conn2.close();
        conn3.close();
        conn5.close();
        assertEquals(0, closeCount.get());

        pool.close();
        assertEquals(3, connCount.get());
        assertEquals(3, closeCount.get());
        assertEquals(0, pool.activeSize());
    }

    @Test
    public void testConnectionPoolWithDifferentCredentials() throws Exception {
        // Test with username only
        SimpleSqlConnectionPool.newBuilder()
            .driverClassName(DB_DRIVER)
            .url(DB_URL)
            .username(DB_USER)
            .build()
            .close();

        // Test with no credentials
        SimpleSqlConnectionPool.newBuilder()
            .driverClassName(DB_DRIVER)
            .url(DB_URL)
            .build()
            .close();
    }

    @Test
    public void testConnectionPoolWithExceptions() {
        // Test invalid core size
        assertThrows(IllegalArgumentException.class, () -> SimpleSqlConnectionPool.newBuilder().coreSize(-2));

        // Test invalid max size
        assertThrows(IllegalArgumentException.class, () -> SimpleSqlConnectionPool.newBuilder().maxSize(1));

        // Test invalid idle timeout
        assertThrows(IllegalArgumentException.class, () -> SimpleSqlConnectionPool.newBuilder().idleTimeout(Duration.ofSeconds(-1)));

        // Test missing required parameters
        assertThrows(IllegalArgumentException.class, () -> SimpleSqlConnectionPool.newBuilder().build());
        assertThrows(IllegalArgumentException.class, () -> SimpleSqlConnectionPool.newBuilder().url(DB_URL).build());
    }

    @Test
    public void testConnectionPoolWithCustomWrapperFactory() throws Exception {
        Var<Connection> vc = Var.of(null);

        SimpleSqlConnectionPool pool = SimpleSqlConnectionPool.newBuilder()
            .driverClassName(DB_DRIVER)
            .url(DB_URL)
            .username(DB_USER)
            .password(DB_PASSWORD)
            .coreSize(2)
            .maxSize(3)
            .idleTimeout(Duration.ofSeconds(10))
            .connectionWrapperFactory((o, p) -> {
                if (vc.get() == null) {
                    vc.set(ConnectionWrapper.newConnectionWrapper(o, p));
                }
                return (ConnectionWrapper) vc.get();
            })
            .build();

        // Test that all connections use the same wrapped connection
        Connection c1 = pool.get();
        Connection c2 = pool.get();
        Connection c3 = pool.get();
        assertSame(c1, c2);
        assertSame(c1, c3);

        pool.close();
    }
}