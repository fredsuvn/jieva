package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BCompare;

public class BCompareTest {

    @Test
    public void testCompares() {
        Assert.assertEquals(BCompare.atBetween(100, 101, 666), Integer.valueOf(101));
        Assert.assertEquals(BCompare.atBetween(100, 0, 99), Integer.valueOf(99));
        Assert.assertEquals(BCompare.atBetween(100, 0, 666), Integer.valueOf(100));
        Assert.assertEquals(BCompare.atLeast(50, 100), Integer.valueOf(100));
        Assert.assertEquals(BCompare.atLeast(111, 100), Integer.valueOf(111));
        Assert.assertEquals(BCompare.atMost(50, 100), Integer.valueOf(50));
        Assert.assertEquals(BCompare.atMost(111, 100), Integer.valueOf(100));
    }
}
