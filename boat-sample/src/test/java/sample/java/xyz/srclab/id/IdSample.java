package sample.java.xyz.srclab.id;

import org.testng.annotations.Test;
import xyz.srclab.common.id.IdGenerator;
import xyz.srclab.common.id.SnowflakeIdGenerator;
import xyz.srclab.common.lang.Nums;
import xyz.srclab.common.test.TestLogger;

import java.util.UUID;

public class IdSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testSnowflake() {
        SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator(1);
        for (int i = 0; i < 10; i++) {
            long id = snowflakeIdGenerator.next();
            //Snowflake: 6819769124932030464 : 0101111010100100101011111110001011101101110000000001000000000000
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
            //IdGenerator: 4f8c8c34-2-83-4
            logger.log("IdGenerator: " + id);
        }
    }
}
