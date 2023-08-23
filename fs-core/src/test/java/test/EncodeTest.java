package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.FsEncode;
import xyz.srclab.common.base.FsString;

import java.nio.ByteBuffer;

public class EncodeTest {

    @Test
    public void testBase64() {
        Assert.assertEquals(FsEncode.base64ToString("123456中文中文"), "MTIzNDU25Lit5paH5Lit5paH");
        Assert.assertEquals(
            FsEncode.base64ToString(ByteBuffer.wrap("123456中文中文".getBytes(FsString.CHARSET))),
            "MTIzNDU25Lit5paH5Lit5paH");
        Assert.assertEquals(FsEncode.deBase64ToString("MTIzNDU25Lit5paH5Lit5paH"), "123456中文中文");
    }
}
