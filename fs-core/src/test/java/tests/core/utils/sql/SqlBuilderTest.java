package tests.core.utils.sql;

import internal.annotations.J17Only;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.utils.sql.PreparedBatchSql;
import space.sunqian.fs.utils.sql.PreparedSql;
import space.sunqian.fs.utils.sql.SqlBuilder;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@J17Only
public class SqlBuilderTest {

    @Test
    public void testSqlBuilderWithBasicOperations() throws Exception {
        ZonedDateTime now = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(System.currentTimeMillis()),
            ZoneId.systemDefault()
        );
        SqlBuilder builder = SqlBuilder.newBuilder()
            .append("INSERT INTO `user` (name, age, birthday) VALUES ")
            .append("(", "Alice")
            .append(",", 25)
            .appendIf(true, ",", now)
            .appendIf(false, ",", now)
            .appendIf(true, ")")
            .appendIf(false, ")");
        PreparedSql preparedSql = builder.build();
        assertEquals(
            "INSERT INTO `user` (name, age, birthday) VALUES (?,?,?)",
            preparedSql.preparedSql()
        );
        assertEquals(
            ListKit.list("Alice", 25, now),
            preparedSql.parameters()
        );
    }

    @Test
    public void testSqlBuilderWithBatchOperations() throws Exception {
        ZonedDateTime now = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(System.currentTimeMillis()),
            ZoneId.systemDefault()
        );
        SqlBuilder builder = SqlBuilder.newBuilder()
            .append("INSERT INTO `user` (name, age, birthday) VALUES ")
            .append("(", "Alice")
            .append(",", 25)
            .append(",", now)
            .append(")");

        PreparedBatchSql batchSql = builder.buildBatch();
        assertEquals(
            "INSERT INTO `user` (name, age, birthday) VALUES (?,?,?)",
            batchSql.preparedSql()
        );
        assertEquals(
            Collections.emptyList(),
            batchSql.batchParameters()
        );

        batchSql.parameters(ListKit.list("Alice", 25, now));
        batchSql.batchParameters(ListKit.list(
            ListKit.list("Bob", 30, now),
            ListKit.list("Charlie", 35, now),
            ListKit.list("David", 40, now)
        ));
        assertEquals(
            ListKit.list(
                ListKit.list("Alice", 25, now),
                ListKit.list("Bob", 30, now),
                ListKit.list("Charlie", 35, now),
                ListKit.list("David", 40, now)
            ),
            batchSql.batchParameters()
        );
    }

    @Test
    public void testSqlBuilderWithListParameters() throws Exception {
        List<String> ids = ListKit.list("1", "2", "3");
        assertEquals(
            "select * from `user` where id in (?,?,?)",
            SqlBuilder.newBuilder()
                .append("select * from `user` where id in ")
                .append("(", ids).append(")")
                .build()
                .preparedSql()
        );

        Iterable<String> idsIt = ids::iterator;
        assertEquals(
            "select * from `user` where id in (?,?,?)",
            SqlBuilder.newBuilder()
                .append("select * from `user` where id in ")
                .append("(", idsIt).append(")")
                .build()
                .preparedSql()
        );

        assertEquals(
            "select * from `user` where id in ()",
            SqlBuilder.newBuilder()
                .append("select * from `user` where id in ")
                .append("(", Collections.emptyList()).append(")")
                .build()
                .preparedSql()
        );
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class User {
        private Long id;
        private String name;
        private int age;
        private ZonedDateTime birthday;
    }
}