package test.java.xyz.srclab.common.base;

import org.junit.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BMath;

public class BMathTest {

    @Test
    public void testMath() {
        Assert.assertEquals(BMath.remainingLength(100, 10), 90);
        Assert.assertEquals(BMath.blockNumber(100, 10), 10);
        Assert.assertEquals(BMath.blockNumber(101, 10), 11);
        Assert.assertEquals(BMath.blockNumber(9, 10), 1);
        Assert.assertEquals(BMath.newSizeForBlock(12, 3, 4), 16);
        Assert.assertEquals(BMath.newSizeForBlock(13, 3, 4), 20);
        Assert.assertEquals(BMath.endIndex(0, 10), 10);
        Assert.assertEquals(BMath.endIndex(1, 10), 11);
    }
}
