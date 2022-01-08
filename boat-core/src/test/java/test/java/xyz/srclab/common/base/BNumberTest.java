package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BNumber;

public class BNumberTest {

    @Test
    public void testConvert() {
        String str = "123";
        Assert.assertEquals(BNumber.toDouble(str), 123.0);
    }

    @Test
    public void testUnsigned() {
        byte b = -12;
        Assert.assertEquals(BNumber.toUnsignedInt(b), -12 & 0xFF);
        short s = -88;
        Assert.assertEquals(BNumber.toUnsignedLong(s), -88 & 0xFFFF);
    }

    @Test
    public void testPadding() {
        int i = 2;
        Assert.assertEquals(BNumber.toBinaryString(i), "0000000000" + "0000000000" + "0000000000" + "10");
        Assert.assertEquals(BNumber.toHexString(i), "00000002");
    }
}
