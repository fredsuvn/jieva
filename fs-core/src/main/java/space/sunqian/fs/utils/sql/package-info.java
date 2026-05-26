/**
 * This package provides JDBC and SQL utilities for database operations, for example:
 * <pre>{@code
 * // Quickly create a connection pool to get a connection:
 * SimpleSqlConnectionPool pool = SimpleSqlConnectionPool.newBuilder()
 *     .driverClassName(className)
 *     .url(url)
 *     .username(username)
 *     .password(password)
 *     .build();
 * Connection connection = pool.get();
 * // Using SqlBuilder for fluent SQL construction:
 * List<User> users = SqlBuilder.newBuilder()
 *     .append("SELECT * FROM `users` WHERE 1=1")
 *     .appendIf(searchEnabled, " AND name LIKE ", "%" + searchTerm + "%")
 *     .append(" ORDER BY created_at DESC")
 *     .build()
 *     .query(User.class, connection)
 *     .list();
 * // Release (but not actually close) the connection pool:
 * connection.close();
 * // Close the connection pool, all connections in the pool will be closed:
 * pool.close();
 * }</pre>
 * Core interfaces:
 * <ul>
 *     <li>{@link space.sunqian.fs.utils.sql.SimpleSqlConnectionPool}</li>
 *     <li>{@link space.sunqian.fs.utils.sql.ConnectionWrapper}</li>
 *     <li>{@link space.sunqian.fs.utils.sql.SqlBuilder}</li>
 *     <li>{@link space.sunqian.fs.utils.sql.PreparedSql}</li>
 *     <li>{@link space.sunqian.fs.utils.sql.PreparedBatchSql}</li>
 *     <li>{@link space.sunqian.fs.utils.sql.SqlQuery}</li>
 *     <li>{@link space.sunqian.fs.utils.sql.SqlInsert}</li>
 *     <li>{@link space.sunqian.fs.utils.sql.SqlUpdate}</li>
 *     <li>{@link space.sunqian.fs.utils.sql.SqlOperation}</li>
 *     <li>{@link space.sunqian.fs.utils.sql.SqlBatch}</li>
 * </ul>
 * Utility classes:
 * <ul>
 *     <li>{@link space.sunqian.fs.utils.sql.SqlKit}</li>
 * </ul>
 * Runtime exceptions for SQL operations:
 * <ul>
 *     <li>{@link space.sunqian.fs.utils.sql.SqlRuntimeException}</li>
 * </ul>
 */
package space.sunqian.fs.utils.sql;