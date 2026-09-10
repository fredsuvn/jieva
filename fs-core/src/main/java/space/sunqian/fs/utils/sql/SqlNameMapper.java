package space.sunqian.fs.utils.sql;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.ThreadSafe;
import space.sunqian.fs.base.string.NameMapper;

import java.lang.reflect.Type;

/**
 * This interface represents a name mapper to map names between java objects and SQL column/table names.
 *
 * @author sunqian
 */
@ThreadSafe
public interface SqlNameMapper {

    /**
     * Returns the default {@link SqlNameMapper}. The name mappers for the default {@link SqlNameMapper} are:
     * <ul>
     *     <li>
     *         toColumnNameMapper: from lower camel case to upper underscore case,
     *         e.g. {@code userName} to {@code USER_NAME}
     *     </li>
     *     <li>
     *         toPropertyNameMapper: from upper underscore case to lower camel case,
     *         e.g. {@code USER_NAME} to {@code userName}
     *     </li>
     *     <li>
     *         toTableNameMapper: from upper camel case without prefix package name to upper underscore case,
     *         e.g. {@code a.b.c.TableName} to {@code TABLE_NAME}
     *     </li>
     * </ul>
     *
     * @return the default {@link SqlNameMapper}
     */
    static @Nonnull SqlNameMapper defaultMapper() {
        return SqlNameBack.DefaultSqlNameMapper.INST;
    }

    /**
     * Maps the java property name to the SQL column name.
     *
     * @param propertyName the java property name
     * @return the SQL column name
     */
    default @Nonnull String toColumnName(@Nonnull String propertyName) {
        return toColumnNameMapper().map(propertyName);
    }

    /**
     * Maps the SQL column name to the java property name.
     *
     * @param columnName the SQL column name
     * @return the java property name
     */
    default @Nonnull String toPropertyName(@Nonnull String columnName) {
        return toPropertyNameMapper().map(columnName);
    }

    /**
     * Maps the java type name to the SQL table name.
     *
     * @param javaTypeName the java type name
     * @return the SQL table name
     */
    default @Nonnull String toTableName(@Nonnull String javaTypeName) {
        return toTableNameMapper().map(javaTypeName);
    }

    /**
     * Maps the java type name to the SQL table name.
     *
     * @param javaType the java type to provide java type name
     * @return the SQL table name
     */
    default String toTableName(@Nonnull Type javaType) {
        return toTableName(javaType.getTypeName());
    }

    /**
     * Returns a name mapper to map java property names to SQL column names.
     *
     * @return the name mapper to map java property names to SQL column names
     */
    @Nonnull
    NameMapper toColumnNameMapper();

    /**
     * Returns a name mapper to map SQL column names to java property names.
     *
     * @return the name mapper to map SQL column names to java property names
     */
    @Nonnull
    NameMapper toPropertyNameMapper();

    /**
     * Returns a name mapper to map java type names to SQL table names.
     *
     * @return the name mapper to map java type names to SQL table names
     */
    @Nonnull
    NameMapper toTableNameMapper();
}
