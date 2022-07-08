package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BtLog;
import xyz.srclab.common.base.BtRandom;

import java.util.function.Supplier;

public class BtRandomTest {

    @Test
    public void testRandoms() {
        for (int i = 0; i < 10000; i++) {
            int r = BtRandom.between(10, 20);
            // BLog.info("random[10, 20): {}", r);
            Assert.assertTrue(r >= 10 && r < 20);
        }
        String rd = BtRandom.randomString(100);
        BtLog.info("randomString: {}", rd);
        Assert.assertEquals(rd.length(), 100);
        for (int i = 0; i < rd.length(); i++) {
            char c = rd.charAt(i);
            Assert.assertTrue(Character.isLetterOrDigit(c));
        }
    }

    @Test
    public void testRandomSupplier() {
        Supplier<?> rs = BtRandom.newBuilder()
            .score(20, "A")
            .score(20, "B")
            .score(60, "C")
            .build();
        int countA = 0;
        int countB = 0;
        int countC = 0;
        for (int i = 0; i < 1000; i++) {
            Object result = rs.get();
            if (result.equals("A")) {
                countA++;
            } else if (result.equals("B")) {
                countB++;
            } else if (result.equals("C")) {
                countC++;
            }
        }
        int total = countA + countB + countC;
        Assert.assertEquals(total, 1000);
        BtLog.info("countA: {}, countB: {}, countC: {}, total: {}", countA, countB, countC, total);
    }
}
