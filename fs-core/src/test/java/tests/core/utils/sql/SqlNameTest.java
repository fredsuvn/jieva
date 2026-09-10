package tests.core.utils.sql;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.utils.sql.SqlNameMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SqlNameTest {

    @Test
    public void testSqlNameMapper() {
        SqlNameMapper mapper = SqlNameMapper.defaultMapper();
        assertEquals("SOME_NAME", mapper.toColumnName("someName"));
        assertEquals("someName", mapper.toPropertyName("SOME_NAME"));
        assertEquals("SOME_NAME", mapper.toTableName(SomeName.class));
        assertEquals("SOME_NAME", mapper.toColumnNameMapper().map("someName"));
        assertEquals("someName", mapper.toPropertyNameMapper().map("SOME_NAME"));
        assertEquals("SOME_NAME", mapper.toTableNameMapper().map("SomeName"));
    }

    public static class SomeName {
    }
}