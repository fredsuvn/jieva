package space.sunqian.fs.sql;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.base.chars.CharsBuilder;
import space.sunqian.fs.collect.ListKit;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class is used for building SQL in method chaining. For example:
 * <pre>{@code
 * // simple sql:
 * List<User> users = SqlBuilder.newBuilder()
 *     .append("SELECT * FROM users WHERE 1=1)
 *     .append(" AND gender = ", gender)
 *     .append(" AND id in (", idList).append(")")
 *     .build()
 *     .connection(connection)
 *     .query(User.class);
 * // batch sql:
 * BatchSql batchSql = SqlBuilder.newBuilder()
 *     .append("INSERT INTO users (name, age) VALUES (?, ?);")
 *     .buildBatch()
 *     .batchParameters(batchParams)
 *     .connection(connection)
 *     .execute();
 * }</pre>
 *
 * @author sunqian
 * @see PreparedSql
 */
public final class SqlBuilder {

    /**
     * Returns a new instance of {@link SqlBuilder}.
     *
     * @return a new instance of {@link SqlBuilder}
     */
    public static @Nonnull SqlBuilder newBuilder() {
        return new SqlBuilder();
    }

    private final @Nonnull CharsBuilder sqlBuilder = new CharsBuilder();
    private @Nullable List<Object> parameters;

    private SqlBuilder() {
    }

    /**
     * Appends a raw string to the current SQL.
     *
     * @param sql the given raw string to append
     * @return this builder
     */
    public @Nonnull SqlBuilder append(@Nonnull String sql) {
        sqlBuilder.append(sql);
        return this;
    }

    /**
     * Appends a parameterized string to the current SQL.
     * <p>
     * This method first appends the given raw string, and then appends the question mark (?) for parameter binding. If
     * the given parameter is not an instance of {@link Iterable}, this method appends one question mark (?), otherwise,
     * this method appends multiple question marks joined by commas (?,?,?...), and the number of question marks is
     * equal to the size of the {@link Iterable} parameter.
     *
     * @param sql   the given raw string to append
     * @param param the given parameter value to bind
     * @return this builder
     */
    public @Nonnull SqlBuilder append(@Nonnull String sql, @Nullable Object param) {
        sqlBuilder.append(sql);
        if (param instanceof Collection<?>) {
            @SuppressWarnings("PatternVariableCanBeUsed")
            Collection<?> collection = (Collection<?>) param;
            parameters().addAll(collection);
            sqlBuilder.append(join(collection));
        } else if (param instanceof Iterable<?>) {
            @SuppressWarnings("PatternVariableCanBeUsed")
            Iterable<?> iterable = (Iterable<?>) param;
            Collection<?> collection = ListKit.toList(iterable);
            parameters().addAll(collection);
            sqlBuilder.append(join(collection));
        } else {
            // Handle single parameter
            sqlBuilder.append("?");
            parameters().add(param);
        }
        return this;
    }

    private @Nonnull List<Object> parameters() {
        if (parameters == null) {
            parameters = new ArrayList<>();
        }
        return parameters;
    }

    private @Nonnull String join(Collection<?> collection) {
        if (collection.isEmpty()) {
            return "";
        }
        int size = collection.size();
        char[] chars = new char[size * 2 - 1];
        chars[0] = '?';
        for (int i = 1; i < chars.length; i += 2) {
            chars[i] = ',';
            chars[i + 1] = '?';
        }
        return new String(chars);
    }

    /**
     * Conditionally appends a raw string to the current SQL.
     * <p>
     * The given raw string will only be added if the specified condition evaluates to {@code true}.
     *
     * @param condition the condition that determines whether to append the SQL
     * @param sql       the given raw string to append if condition is {@code true}
     * @return this builder
     */
    public @Nonnull SqlBuilder appendIf(boolean condition, @Nonnull String sql) {
        if (condition) {
            append(sql);
        }
        return this;
    }

    /**
     * Conditionally appends a parameterized string to the current SQL.
     * <p>
     * The given parameterized string will only be added if the specified condition evaluates to {@code true}. If it is,
     * this method first appends the given raw string, and then appends the question mark (?) for parameter binding. If
     * the given parameter is not an instance of {@link Iterable}, this method appends one question mark (?), otherwise,
     * this method appends multiple question marks joined by commas (?,?,?...), and the number of question marks is
     * equal to the size of the {@link Iterable} parameter.
     *
     * @param condition the condition that determines whether to append the SQL
     * @param sql       the given raw string to append if condition is {@code true}
     * @param param     the given parameter value to bind if condition is {@code true}
     * @return this builder
     */
    public @Nonnull SqlBuilder appendIf(boolean condition, @Nonnull String sql, @Nullable Object param) {
        if (condition) {
            append(sql, param);
        }
        return this;
    }

    /**
     * Builds and returns the final prepared SQL.
     *
     * @return the prepared SQL ready for execution
     */
    public @Nonnull PreparedSql build() {
        return new PreparedSqlImpl(
            sqlBuilder.toString(),
            parameters == null ? Collections.emptyList() : parameters
        );
    }

    /**
     * Builds and returns the final prepared SQL for batch execution. The returned {@link PreparedBatchSql} object is
     * only built from the current prepared SQL string, and the previously added parameters will be discarded. The
     * actual batch parameters should be added by {@link PreparedBatchSql#batchParameters(List)} or
     * {@link PreparedBatchSql#parameters(List)}.
     *
     * @return the prepared SQL ready for execution for batch execution
     */
    public @Nonnull PreparedBatchSql buildBatch() {
        return new PreparedBatchSqlImpl(sqlBuilder.toString());
    }

    private static final class PreparedSqlImpl implements PreparedSql {

        private final @Nonnull String preparedSql;
        private final @Nonnull List<Object> parameters;

        private PreparedSqlImpl(@Nonnull String preparedSql, @Nonnull @RetainedParam List<Object> parameters) {
            this.preparedSql = preparedSql;
            this.parameters = parameters;
        }

        @Override
        public @Nonnull String preparedSql() {
            return preparedSql;
        }

        @Override
        public @Nonnull @Immutable List<Object> parameters() {
            return parameters;
        }
    }

    private static final class PreparedBatchSqlImpl implements PreparedBatchSql {

        private final @Nonnull String preparedSql;
        private final @Nonnull List<List<Object>> batchedParameters = new ArrayList<>();

        private @Nullable Connection connection;

        private PreparedBatchSqlImpl(@Nonnull String preparedSql) {
            this.preparedSql = preparedSql;
        }

        @Override
        public @Nonnull String preparedSql() {
            return preparedSql;
        }

        @Override
        public @Nonnull @Immutable List<@Nonnull List<Object>> batchParameters() {
            return Collections.unmodifiableList(batchedParameters);
        }

        @Override
        public @Nonnull PreparedBatchSql batchParameters(@Nonnull List<@Nonnull List<Object>> batchParameters) {
            batchedParameters.addAll(batchParameters);
            return this;
        }

        @Override
        public @Nonnull PreparedBatchSql parameters(@Nonnull List<Object> parameters) {
            batchedParameters.add(parameters);
            return this;
        }
    }
}