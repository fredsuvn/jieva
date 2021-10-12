package test.xyz.srclab.common.id;

import org.testng.annotations.Test;
import xyz.srclab.common.base.Nums;
import xyz.srclab.common.id.IdGenerator;
import xyz.srclab.common.id.SnowflakeIdGenerator;
import xyz.srclab.common.logging.Logs;

import java.util.UUID;

/**
 * @author sunqian
 */
public class IdTest {

    @Test
    public void testSnowflake() {
        SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator(1);
        for (int i = 0; i < 10; i++) {
            long id = snowflakeIdGenerator.next();
            Logs.info("Snowflake: " + id + " : " + Nums.toBinaryString(id));
        }
    }

    @Test
    public void testIdGenerator() {
        IdGenerator<String, String, String, String> idGenerator = IdGenerator.newIdGenerator(
            () -> UUID.randomUUID().toString(),
            l -> l.substring(0, 10),
            i -> i.substring(11, 15),
            (l, i) -> l + "-" + i
        );
        for (int i = 0; i < 10; i++) {
            String id = idGenerator.next();
            Logs.info("IdGenerator: " + id);
        }
    }
}
