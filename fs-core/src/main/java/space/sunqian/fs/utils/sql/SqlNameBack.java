package space.sunqian.fs.utils.sql;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.string.NameFormatter;
import space.sunqian.fs.base.string.NameMapper;
import space.sunqian.fs.base.string.StringKit;
import space.sunqian.fs.reflect.TypeKit;

final class SqlNameBack {

    enum DefaultSqlNameMapper implements SqlNameMapper {

        INST;

        private final @Nonnull NameFormatter LOWER_CAMEL = NameFormatter.lowerCamel();
        private final @Nonnull NameFormatter UPPER_CAMEL = NameFormatter.upperCamel();
        private final @Nonnull NameFormatter DELIMITER = NameFormatter.delimiter("_");

        private final @Nonnull NameMapper TO_COLUMN_NAME_MAPPER = propertyName ->
            StringKit.upperCase(LOWER_CAMEL.format(propertyName, DELIMITER));

        private final @Nonnull NameMapper TO_PROPERTY_NAME_MAPPER = columnName ->
            DELIMITER.format(StringKit.lowerCase(columnName), LOWER_CAMEL);

        private final @Nonnull NameMapper TO_TABLE_NAME_MAPPER = javaTypeName ->
            StringKit.upperCase(UPPER_CAMEL.format(TypeKit.getLastName(javaTypeName, true), DELIMITER));

        @Override
        public @Nonnull NameMapper toColumnNameMapper() {
            return TO_COLUMN_NAME_MAPPER;
        }

        @Override
        public @Nonnull NameMapper toPropertyNameMapper() {
            return TO_PROPERTY_NAME_MAPPER;
        }

        @Override
        public @Nonnull NameMapper toTableNameMapper() {
            return TO_TABLE_NAME_MAPPER;
        }
    }

    private SqlNameBack() {
    }
}
