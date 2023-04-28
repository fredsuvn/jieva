package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BtId;
import xyz.srclab.common.base.BtLog;
import xyz.srclab.common.base.BtNumber;
import xyz.srclab.common.base.BtString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BtIdTest {

    @Test
    public void testUuid() {
        String uuid = BtId.uuid();
        BtLog.info("BId.uuid(): {}", BtId.uuid());
        Assert.assertEquals(uuid.length(), UUID.randomUUID().toString().length() - 4);
        Assert.assertEquals(BtString.hyphenMatcher().countIn(uuid), 0);
    }

    @Test
    public void testSnowflake() {
        List<Long> list = new ArrayList<>(1000);
        for (int i = 0; i < 1000; i++) {
            long id = BtId.snowflakeId();
            list.add(id);
        }
        Long p = null;
        for (Long l : list) {
            BtLog.info("Snowflake Id: {}", BtNumber.toBinaryString(l));
            if (p != null) {
                Assert.assertTrue(l > p);
            }
            p = l;
        }
    }
}
