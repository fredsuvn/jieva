package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BId;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BNumber;

import java.util.UUID;

public class BIdTest {

    @Test
    public void testUuid() {
        String uuid = BId.uuid();
        Assert.assertEquals(uuid.length(), UUID.randomUUID().toString().length());
        BLog.info("BId.uuid(): {}", BId.uuid());
    }

    @Test
    public void testSnowflake() {
        for (int i = 0; i < 10; i++) {
            long id = BId.snowflakeId();
            BLog.info("Snowflake Id: {}" + BNumber.toBinaryString(id));
        }
    }
}
