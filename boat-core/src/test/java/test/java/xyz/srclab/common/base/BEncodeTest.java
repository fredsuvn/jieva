package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BDefault;
import xyz.srclab.common.base.BEncode;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BRandom;

import java.util.Arrays;
import java.util.Base64;

public class BEncodeTest {

    @Test
    public void test() {
        String rd1 = BRandom.randomString(100);
        String rd1Base64 = BEncode.base64(rd1);
        byte[] rd1Bytes = rd1.getBytes(BDefault.DEFAULT_CHARSET);
        BLog.info("rd1: {}, rd1.base64: {}", rd1, rd1Base64);
        Assert.assertEquals(rd1Base64, new String(Base64.getEncoder().encode(rd1.getBytes(BDefault.DEFAULT_CHARSET))));
        Assert.assertEquals(
            new String(BEncode.base64(rd1Bytes, 10, 22)),
            new String(Base64.getEncoder().encode(Arrays.copyOfRange(rd1Bytes, 10, 10 + 22)))
        );
        String rd2 = BRandom.randomString(1);
        String rd2Base64 = BEncode.base64(rd2);
        BLog.info("rd2: {}, rd2.base64: {}", rd2, rd2Base64);
        Assert.assertEquals(rd2Base64, new String(Base64.getEncoder().encode(rd2.getBytes(BDefault.DEFAULT_CHARSET))));

        String rd1DeBase64 = BEncode.deBase64(rd1Base64);
        byte[] rd1Base64Bytes = rd1Base64.getBytes(BDefault.DEFAULT_CHARSET);
        BLog.info("rd1.de-base64: {}", rd1DeBase64);
        Assert.assertEquals(rd1DeBase64, rd1);
        Assert.assertEquals(
            BEncode.deBase64(rd1Base64Bytes, 4 * 3, 4 * 10),
            Base64.getDecoder().decode(Arrays.copyOfRange(rd1Base64Bytes, 4 * 3, 4 * 3 + 4 * 10))
        );
        String rd2DeBase64 = BEncode.deBase64(rd2Base64);
        BLog.info("rd2.de-base64: {}", rd2DeBase64);
        Assert.assertEquals(rd2DeBase64, new String(Base64.getDecoder().decode(rd2Base64.getBytes(BDefault.DEFAULT_CHARSET))));
    }
}
