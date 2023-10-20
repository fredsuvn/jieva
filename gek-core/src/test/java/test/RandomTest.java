package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fsgek.common.base.GekLogger;
import xyz.fsgek.common.base.GekRandom;

public class RandomTest {

    @Test
    public void testRandom() {
        GekRandom<String> gekRandom = GekRandom.newBuilder()
            .score(20, "A")
            .score(20, "B")
            .score(60, () -> "C")
            .build();
        int countA = 0;
        int countB = 0;
        int countC = 0;
        for (int i = 0; i < 1000; i++) {
            Object result = gekRandom.next();
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
        GekLogger.defaultLogger().info("countA: ", countA, " countB: ", countB, ", countC: ", countC, ", total: ", total);

        String randomStr = String.join("", gekRandom.nextList(100));
        GekLogger.defaultLogger().info("randomStr: ", randomStr);
        Assert.assertEquals(randomStr.length(), 100);
    }
}
