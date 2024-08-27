package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.common.base.GekLog;
import xyz.fslabo.common.base.RandomSupplier;

public class RandomTest {

    @Test
    public void testRandom() {
        RandomSupplier<String> strRandom = RandomSupplier.of(
            RandomSupplier.pair(20, "A"),
            RandomSupplier.pair(20, "B"),
            RandomSupplier.pair(60, () -> "C")
        );
        int countA = 0;
        int countB = 0;
        int countC = 0;
        for (int i = 0; i < 1000; i++) {
            Object result = strRandom.get();
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
        GekLog.getInstance().info("countA: ", countA, " countB: ", countB, ", countC: ", countC, ", total: ", total);

        String randomStr = String.join("", strRandom.get(100));
        GekLog.getInstance().info("randomStr: ", randomStr);
        Assert.assertEquals(randomStr.length(), 100);
    }
}
