package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BBoolean;

public class BBooleanTest {

    @Test
    public void testBoolean() {
        Assert.assertTrue(BBoolean.toBoolean("true"));
        Assert.assertTrue(BBoolean.allTrue("true", "TURE"));
        Assert.assertTrue(BBoolean.anyFalse("true", "x"));
        Assert.assertFalse(BBoolean.anyFalse("x", "y"));
    }
}
