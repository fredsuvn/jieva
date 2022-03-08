package test.java.xyz.srclab.common.base;

import org.junit.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BMath;

public class BMathTest {

    @Test
    public void testRange() {
        Assert.assertEquals(BMath.remainingLength(100, 10), 90);
        Assert.assertEquals(BMath.blockAtLeast(100, 10), 10);
        Assert.assertEquals(BMath.blockAtLeast(101, 10), 11);
        Assert.assertEquals(BMath.blockAtLeast(9, 10), 1);
    }
}
