package space.sunqian.fs.utils.sql;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

import java.util.Collection;

/**
 * SQL statement builder interface, provides methods to build SQL statement with parameters.
 *
 * @author sunqian
 */
public interface SqlStatementBuilder {

    /**
     * Appends a segment of SQL statement to this statement builder.
     *
     * @param sql the given SQL statement segment to append
     * @return this builder
     */
    @Nonnull
    SqlStatementBuilder sql(@Nonnull String sql);

    /**
     * Appends a parameter to this statement builder. The parameter will be bound to the statement with the question
     * mark ({@code ?}) in the final built SQL statement, in order of appearance.
     *
     * @param parameter the given parameter to append
     * @return this builder
     */
    @Nonnull
    SqlStatementBuilder parameter(@Nullable Object parameter);

    /**
     * Appends parameters to this statement builder. The parameters will be bound to the statement with the question
     * mark ({@code ?}) in the final built SQL statement, in order of appearance.
     *
     * @param parameters the given parameters to append
     * @return this builder
     */
    @Nonnull
    SqlStatementBuilder parameters(@Nullable Object @Nonnull ... parameters);

    /**
     * Appends parameters to this statement builder. The parameters will be bound to the statement with the question
     * mark ({@code ?}) in the final built SQL statement, in order of appearance.
     *
     * @param parameters the given parameters to append
     * @return this builder
     */
    @Nonnull
    SqlStatementBuilder parameterList(@Nonnull Collection<?> parameters);
}
