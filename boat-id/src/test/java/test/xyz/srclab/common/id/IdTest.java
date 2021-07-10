package test.xyz.srclab.common.id;

import org.testng.annotations.Test;
import xyz.srclab.common.id.IdGenerator;
import xyz.srclab.common.id.SnowflakeIdGenerator;
import xyz.srclab.common.lang.Nums;
import xyz.srclab.common.test.TestLogger;

import java.util.UUID;

/**
 * @author sunqian
 */
public class IdTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testSnowflake() {
        SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator(1);
        for (int i = 0; i < 10; i++) {
            long id = snowflakeIdGenerator.next();
            logger.log("Snowflake: " + id + " : " + Nums.toBinaryString(id));
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
            logger.log("IdGenerator: " + id);
        }
    }
}
