package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Nums;

public class NumsTest {

    @Test
    public void testConvert() {
        String str = "123";
        Assert.assertEquals(Nums.toDouble(str), 123.0);
    }

    @Test
    public void testUnsigned() {
        byte b = -12;
        Assert.assertEquals(Nums.toUnsignedInt(b), -12 & 0xFF);
        short s = -88;
        Assert.assertEquals(Nums.toUnsignedLong(s), -88 & 0xFFFF);
    }
}
