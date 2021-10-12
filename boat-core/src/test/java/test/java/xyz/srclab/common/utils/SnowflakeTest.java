package test.java.xyz.srclab.common.utils;

import org.testng.annotations.Test;
import xyz.srclab.common.base.Nums;
import xyz.srclab.common.logging.Logs;
import xyz.srclab.common.utils.Snowflake;

public class SnowflakeTest {

    @Test
    public void testSnowflake() {
        Snowflake snowflake = new Snowflake(1);
        for (int i = 0; i < 10; i++) {
            long id = snowflake.next();
            Logs.info("Snowflake: " + id + " : " + Nums.toBinaryString(id));
        }
    }
}
