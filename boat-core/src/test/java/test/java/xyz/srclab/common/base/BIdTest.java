package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BId;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BNumber;
import xyz.srclab.common.base.BString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BIdTest {

    @Test
    public void testUuid() {
        String uuid = BId.uuid();
        BLog.info("BId.uuid(): {}", BId.uuid());
        Assert.assertEquals(uuid.length(), UUID.randomUUID().toString().length() - 4);
        Assert.assertEquals(BString.hyphenMatcher().countIn(uuid), 0);
    }

    @Test
    public void testSnowflake() {
        List<Long> list = new ArrayList<>(1000);
        for (int i = 0; i < 1000; i++) {
            long id = BId.snowflakeId();
            list.add(id);
        }
        Long p = null;
        for (Long l : list) {
            BLog.info("Snowflake Id: {}", BNumber.toBinaryString(l));
            if (p != null) {
                Assert.assertTrue(l > p);
            }
            p = l;
        }
    }
}
