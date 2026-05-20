/**
 * This package provides JDBC and SQL utilities for database operations, for example:
 * <pre>{@code
 * // Quickly create a connection pool to get a connection:
 * SimpleJdbcPool pool = SimpleJdbcPool.newBuilder()
 *     .driverClassName(className)
 *     .url(url)
 *     .username(username)
 *     .password(password)
 *     .build();
 * Connection connection = pool.getConnection();
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
 *     <li>{@link space.sunqian.fs.sql.SimpleJdbcPool}</li>
 *     <li>{@link space.sunqian.fs.sql.SqlBuilder}</li>
 *     <li>{@link space.sunqian.fs.sql.PreparedSql}</li>
 *     <li>{@link space.sunqian.fs.sql.PreparedBatchSql}</li>
 *     <li>{@link space.sunqian.fs.sql.SqlQuery}</li>
 *     <li>{@link space.sunqian.fs.sql.SqlInsert}</li>
 *     <li>{@link space.sunqian.fs.sql.SqlUpdate}</li>
 *     <li>{@link space.sunqian.fs.sql.SqlOperation}</li>
 *     <li>{@link space.sunqian.fs.sql.SqlBatch}</li>
 * </ul>
 * Utility classes:
 * <ul>
 *     <li>{@link space.sunqian.fs.sql.SqlKit}</li>
 * </ul>
 * Runtime exceptions for SQL operations:
 * <ul>
 *     <li>{@link space.sunqian.fs.sql.SqlRuntimeException}</li>
 * </ul>
 */
package space.sunqian.fs.sql;