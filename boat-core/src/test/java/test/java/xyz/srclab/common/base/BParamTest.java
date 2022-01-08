package test.java.xyz.srclab.common.base;

import org.junit.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BParam;

public class BParamTest {

    @Test
    public void testParam() {
        Assert.assertEquals(BParam.remainingLength(100, 10), 90);
    }
}
