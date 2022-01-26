package test.java.xyz.srclab.common.base;

import org.junit.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BRange;

public class BRangeTest {

    @Test
    public void testRange() {
        Assert.assertEquals(BRange.remainingLength(100, 10), 90);
        Assert.assertEquals(BRange.needingBlock(100, 10), 10);
        Assert.assertEquals(BRange.needingBlock(101, 10), 11);
        Assert.assertEquals(BRange.needingBlock(9, 10), 1);
    }
}
