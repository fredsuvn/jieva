package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.FsRandom;

public class RandomTest {

    @Test
    public void testRandomSupplier() {
        FsRandom<String> fsRandom = FsRandom.newBuilder()
            .score(20, "A")
            .score(20, "B")
            .score(60, "C")
            .build();
        int countA = 0;
        int countB = 0;
        int countC = 0;
        for (int i = 0; i < 1000; i++) {
            Object result = fsRandom.next();
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
        Out.println("countA: ", countA, " countB: ", countB, ", countC: ", countC, ", total: ", total);
    }
}
