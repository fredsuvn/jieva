package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BEncode;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BRandom;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class BEncodeTest {

    @Test
    public void test() {
        String rd1 = BRandom.randomString(100);
        String rd1Base64 = BEncode.base64(rd1);
        byte[] rd1Bytes = rd1.getBytes(StandardCharsets.UTF_8);
        BLog.info("rd1.base64: {}", rd1Base64);
        Assert.assertEquals(rd1Base64, new String(Base64.getEncoder().encode(rd1.getBytes(StandardCharsets.UTF_8))));
        Assert.assertEquals(
           new String(BEncode.base64(rd1Bytes, 10, 22)),
            new String(Base64.getEncoder().encode(Arrays.copyOfRange(rd1Bytes, 10, 10 + 22)))
        );

        String rd2 = BRandom.randomString(1);
        String rd2Base64 = BEncode.base64(rd2);
        BLog.info("rd2.base64: {}", rd2Base64);
        Assert.assertEquals(rd2Base64, new String(Base64.getEncoder().encode(rd2.getBytes(StandardCharsets.UTF_8))));
    }
}
