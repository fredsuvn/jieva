package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BtNumber;

public class BtNumberTest {

    @Test
    public void testConvert() {
        String str = "123";
        Assert.assertEquals(BtNumber.toDouble(str), 123.0);
    }

    @Test
    public void testUnsigned() {
        byte b = -12;
        Assert.assertEquals(BtNumber.toUnsignedInt(b), -12 & 0xFF);
        short s = -88;
        Assert.assertEquals(BtNumber.toUnsignedLong(s), -88 & 0xFFFFL);
    }

    @Test
    public void testPadding() {
        int i = 2;
        Assert.assertEquals(BtNumber.toBinaryString(i), "0000000000" + "0000000000" + "0000000000" + "10");
        Assert.assertEquals(BtNumber.toHexString(i), "00000002");
    }

    @Test
    public void testConstant() {
        Assert.assertSame(BtNumber.hundredInt(), BtNumber.hundredInt());
        Assert.assertSame(BtNumber.hundredDecimal(), BtNumber.hundredDecimal());
        Assert.assertSame(BtNumber.thousandInt(), BtNumber.thousandInt());
        Assert.assertSame(BtNumber.thousandDecimal(), BtNumber.thousandDecimal());
    }
}
