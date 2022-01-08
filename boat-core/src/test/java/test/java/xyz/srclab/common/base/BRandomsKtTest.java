package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Randomer;
import xyz.srclab.common.base.Randoms;
import xyz.srclab.common.logging.Logs;

public class BRandomsKtTest {

    @Test
    public void testRandoms() {
        for (int i = 0; i < 10000; i++) {
            int r = Randoms.between(10, 20);
            Logs.info("random[10, 20): {}", r);
            Assert.assertTrue(r >= 10 && r < 20);
        }
    }

    @Test
    public void testRandomSupplier() {
        Randomer<?> BRandomer = BRandomer.newBuilder()
            .score(20, "A")
            .score(20, "B")
            .score(60, "C")
            .build();
        int countA = 0;
        int countB = 0;
        int countC = 0;
        for (int i = 0; i < 1000; i++) {
            Object result = BRandomer.get();
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
        Logs.info("countA: {}, countB: {}, countC: {}, total: {}", countA, countB, countC, total);
    }
}
