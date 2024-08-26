package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.common.base.GekLog;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.base.RandomBuilder;

import java.util.function.Supplier;

public class RandomTest {

    @Test
    public void testRandom() {
        Supplier<String> gekRandom = Jie.randomBuilder()
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
        GekLog.getInstance().info("countA: ", countA, " countB: ", countB, ", countC: ", countC, ", total: ", total);

        String randomStr = String.join("", gekRandom.nextList(100));
        GekLog.getInstance().info("randomStr: ", randomStr);
        Assert.assertEquals(randomStr.length(), 100);
    }
}
