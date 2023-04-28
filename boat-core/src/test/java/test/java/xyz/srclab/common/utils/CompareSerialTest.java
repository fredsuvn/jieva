package test.java.xyz.srclab.common.utils;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BTime;
import xyz.srclab.common.utils.CompareSerial;
import xyz.srclab.common.utils.Counter;

public class CompareSerialTest {

    @Test
    public void testCompareSerial() {
        Counter counter = Counter.startsAt(0);
        CompareSerial<Long, Long, String> compareSerial = new CompareSerial<>(
            -1L,
            0L,
            it -> counter.getLong(),
            it -> {
                if (it < 2) {
                    return it + 1;
                }
                return null;
            },
            (it1, it2) -> "" + it1 + it2
        );
        String n1 = compareSerial.next();
        String n2 = compareSerial.next();
        String n3 = compareSerial.next();
        counter.incrementAndGetInt();
        String n4 = compareSerial.next();
        String n5 = compareSerial.next();
        String n6 = compareSerial.next();
        BLog.info("next: {}, {}, {}, {} ,{}, {}", n1, n2, n3, n4, n5, n6);
        Assert.assertEquals(n1, "00");
        Assert.assertEquals(n2, "01");
        Assert.assertEquals(n3, "02");
        Assert.assertEquals(n4, "10");
        Assert.assertEquals(n5, "11");
        Assert.assertEquals(n6, "12");

        long now = BTime.currentMillis();
        String nn = compareSerial.nextOrNull(3, 500);
        Assert.assertNull(nn);
        Assert.assertTrue(BTime.currentMillis() - now >= 500 * 3);
    }
}
