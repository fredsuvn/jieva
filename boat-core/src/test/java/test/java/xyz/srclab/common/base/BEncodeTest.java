package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.*;
import xyz.srclab.common.io.BIO;
import xyz.srclab.common.io.BytesAppender;

import java.util.Arrays;
import java.util.Base64;

public class BEncodeTest {

    @Test
    public void testBase64() {
        Assert.assertEquals(
            BEncode.base64("123456"),
            "MTIzNDU2"
        );
        Assert.assertEquals(
            BEncode.deBase64("MTIzNDU2"),
            "123456"
        );

        String rd1 = BRandom.randomString(100);
        String rd1Base64 = BEncode.base64(rd1);
        byte[] rd1Bytes = rd1.getBytes(BDefault.charset());
        BLog.info("rd1: {}, rd1.base64: {}", rd1, rd1Base64);
        Assert.assertEquals(rd1Base64, new String(Base64.getEncoder().encode(rd1.getBytes(BDefault.charset()))));
        Assert.assertEquals(
            new String(BEncode.base64(rd1Bytes, 10, 22)),
            new String(Base64.getEncoder().encode(Arrays.copyOfRange(rd1Bytes, 10, 10 + 22)))
        );
        String rd2 = BRandom.randomString(1);
        String rd2Base64 = BEncode.base64(rd2);
        BLog.info("rd2: {}, rd2.base64: {}", rd2, rd2Base64);
        Assert.assertEquals(rd2Base64, new String(Base64.getEncoder().encode(rd2.getBytes(BDefault.charset()))));

        String rd1DeBase64 = BEncode.deBase64(rd1Base64);
        byte[] rd1Base64Bytes = rd1Base64.getBytes(BDefault.charset());
        BLog.info("rd1.de-base64: {}", rd1DeBase64);
        Assert.assertEquals(rd1DeBase64, rd1);
        Assert.assertEquals(
            BEncode.deBase64(rd1Base64Bytes, 4 * 3, 4 * 10),
            Base64.getDecoder().decode(Arrays.copyOfRange(rd1Base64Bytes, 4 * 3, 4 * 3 + 4 * 10))
        );
        String rd2DeBase64 = BEncode.deBase64(rd2Base64);
        BLog.info("rd2.de-base64: {}", rd2DeBase64);
        Assert.assertEquals(rd2DeBase64, new String(Base64.getDecoder().decode(rd2Base64.getBytes(BDefault.charset()))));

        //Test output stream
        BytesAppender out = new BytesAppender();
        BEncode.base64(BIO.asInputStream(rd1.getBytes(BDefault.charset())), out);
        String outBase64String = BString.toString8Bit(out.toBytes());
        BLog.info("outBase64String: {}", outBase64String);
        Assert.assertEquals(outBase64String, rd1Base64);
    }

    @Test
    public void testHex() {
        // BEncode.hex2();
        // BEncode.hex();
    }

    private final static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }

        return new String(hexChars);
    }
}
