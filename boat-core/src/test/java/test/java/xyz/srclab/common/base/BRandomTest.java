package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BRandom;

import java.util.function.Supplier;

public class BRandomTest {

    @Test
    public void testRandoms() {
        for (int i = 0; i < 10000; i++) {
            int r = BRandom.between(10, 20);
            BLog.info("random[10, 20): {}", r);
            Assert.assertTrue(r >= 10 && r < 20);
        }
    }

    @Test
    public void testRandomSupplier() {
        Supplier<?> rs = BRandom.randomSupplierBuilder()
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
        BLog.info("countA: {}, countB: {}, countC: {}, total: {}", countA, countB, countC, total);
    }
}
