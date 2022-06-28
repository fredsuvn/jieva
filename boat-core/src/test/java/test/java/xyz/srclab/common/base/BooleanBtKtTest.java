package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BBoolean;

public class BooleanBtKtTest {

    @Test
    public void testBoolean() {
        Assert.assertTrue(BBoolean.toBoolean("true"));
        Assert.assertTrue(BBoolean.allTrue("true", "TRUE"));
        Assert.assertTrue(BBoolean.anyFalse("true", "x"));
        Assert.assertFalse(BBoolean.anyTrue("x", "y"));
    }
}
