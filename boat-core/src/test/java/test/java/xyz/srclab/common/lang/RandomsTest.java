package test.java.xyz.srclab.common.lang;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.lang.RandomSupplier;
import xyz.srclab.common.lang.Randoms;
import xyz.srclab.common.test.TestLogger;

public class RandomsTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testRandoms() {
        for (int i = 0; i < 10000; i++) {
            int r = Randoms.between(10, 20);
            logger.log("random[10, 20): {}", r);
            Assert.assertTrue(r >= 10 && r < 20);
        }
    }

    @Test
    public void testRandomSupplier() {
        RandomSupplier<?> randomSupplier = RandomSupplier.newBuilder()
            .score(20, "A")
            .score(20, "B")
            .score(60, "C")
            .build();
        int countA = 0;
        int countB = 0;
        int countC = 0;
        for (int i = 0; i < 1000; i++) {
            Object result = randomSupplier.get();
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
        logger.log("countA: {}, countB: {}, countC: {}, total: {}", countA, countB, countC, total);
    }
}
