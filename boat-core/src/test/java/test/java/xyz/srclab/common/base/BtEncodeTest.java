package test.java.xyz.srclab.common.base;

import cn.hutool.core.util.HexUtil;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.*;
import xyz.srclab.common.io.BtIO;

import java.util.Arrays;
import java.util.Base64;

public class BtEncodeTest {

    @Test
    public void testBase64() {
        Assert.assertEquals(
            BtEncode.base64("1qaz2wsx!QAZ@WSX"),
            "MXFhejJ3c3ghUUFaQFdTWA=="
        );
        Assert.assertEquals(
            BtEncode.deBase64("MXFhejJ3c3ghUUFaQFdTWA=="),
            "1qaz2wsx!QAZ@WSX"
        );

        String rd1 = BtRandom.randomString(100);
        String rd1Base64 = BtEncode.base64(rd1);
        byte[] rd1Bytes = rd1.getBytes(Bt.defaultCharset());
        BtLog.info("rd1: {}, rd1.base64: {}", rd1, rd1Base64);
        Assert.assertEquals(rd1Base64, new String(Base64.getEncoder().encode(rd1.getBytes(Bt.defaultCharset()))));
        Assert.assertEquals(
            new String(BtEncode.base64(rd1Bytes, 10, 22)),
            new String(Base64.getEncoder().encode(Arrays.copyOfRange(rd1Bytes, 10, 10 + 22)))
        );
        String rd2 = BtRandom.randomString(1);
        String rd2Base64 = BtEncode.base64(rd2);
        BtLog.info("rd2: {}, rd2.base64: {}", rd2, rd2Base64);
        Assert.assertEquals(rd2Base64, new String(Base64.getEncoder().encode(rd2.getBytes(Bt.defaultCharset()))));

        String rd1DeBase64 = BtEncode.deBase64(rd1Base64);
        byte[] rd1Base64Bytes = rd1Base64.getBytes(Bt.defaultCharset());
        BtLog.info("rd1.de-base64: {}", rd1DeBase64);
        Assert.assertEquals(rd1DeBase64, rd1);
        Assert.assertEquals(
            BtEncode.deBase64(rd1Base64Bytes, 4 * 3, 4 * 10),
            Base64.getDecoder().decode(Arrays.copyOfRange(rd1Base64Bytes, 4 * 3, 4 * 3 + 4 * 10))
        );
        String rd2DeBase64 = BtEncode.deBase64(rd2Base64);
        BtLog.info("rd2.de-base64: {}", rd2DeBase64);
        Assert.assertEquals(rd2DeBase64, new String(Base64.getDecoder().decode(rd2Base64.getBytes(Bt.defaultCharset()))));

        //Test output stream
        BytesBuilder out = new BytesBuilder();
        long enNumber = BtEncode.base64(BtIO.asInputStream(rd1.getBytes(Bt.defaultCharset())), out);
        BtLog.info("enNumber: {}", enNumber);
        Assert.assertEquals(enNumber, rd1.length());
        String outBase64String = BtString.toString8Bit(out.toByteArray());
        BtLog.info("outBase64String: {}", outBase64String);
        Assert.assertEquals(outBase64String, rd1Base64);
        BytesBuilder in = new BytesBuilder();
        long deNumber = BtEncode.deBase64(BtIO.asInputStream(out.toByteArray()), in);
        BtLog.info("deNumber: {}", deNumber);
        Assert.assertEquals(deNumber, BtEncode.getBase64Length(rd1.length()));
        String deBase64String = BtString.toString8Bit(in.toByteArray());
        BtLog.info("deBase64String: {}", deBase64String);
        Assert.assertEquals(deBase64String, rd1);
    }

    @Test
    public void testHex() {
        Assert.assertEquals(
            BtEncode.hex("1qaz2wsx!QAZ@WSX"),
            "3171617a327773782151415a40575358"
        );
        Assert.assertEquals(
            BtEncode.deHex("3171617a327773782151415a40575358"),
            "1qaz2wsx!QAZ@WSX"
        );

        String rd1 = BtRandom.randomString(100);
        String rd1Hex = BtEncode.hex(rd1);
        byte[] rd1Bytes = rd1.getBytes(Bt.defaultCharset());
        BtLog.info("rd1: {}, rd1.hex: {}", rd1, rd1Hex);
        Assert.assertEquals(rd1Hex, new String(HexUtil.encodeHex(rd1.getBytes(Bt.defaultCharset()))));
        Assert.assertEquals(
            new String(BtEncode.hex(rd1Bytes, 10, 22)),
            new String(HexUtil.encodeHex(Arrays.copyOfRange(rd1Bytes, 10, 10 + 22)))
        );
        String rd2 = BtRandom.randomString(1);
        String rd2Hex = BtEncode.hex(rd2);
        BtLog.info("rd2: {}, rd2.hex: {}", rd2, rd2Hex);
        Assert.assertEquals(rd2Hex, new String(HexUtil.encodeHex(rd2.getBytes(Bt.defaultCharset()))));

        String rd1DeHex = BtEncode.deHex(rd1Hex);
        byte[] rd1HexBytes = rd1Hex.getBytes(Bt.defaultCharset());
        BtLog.info("rd1.de-hex: {}", rd1DeHex);
        Assert.assertEquals(rd1DeHex, rd1);
        Assert.assertEquals(
            BtEncode.deHex(rd1HexBytes, 4 * 3, 4 * 10),
            HexUtil.decodeHex(new String(Arrays.copyOfRange(rd1HexBytes, 4 * 3, 4 * 3 + 4 * 10)))
        );
        String rd2DeHex = BtEncode.deHex(rd2Hex);
        BtLog.info("rd2.de-hex: {}", rd2DeHex);
        Assert.assertEquals(rd2DeHex, new String(HexUtil.decodeHex(new String(rd2Hex.getBytes(Bt.defaultCharset())))));

        //Test output stream
        BytesBuilder out = new BytesBuilder();
        long enNumber = BtEncode.hex(BtIO.asInputStream(rd1.getBytes(Bt.defaultCharset())), out);
        BtLog.info("enNumber: {}", enNumber);
        Assert.assertEquals(enNumber, rd1.length());
        String outHexString = BtString.toString8Bit(out.toByteArray());
        BtLog.info("outHexString: {}", outHexString);
        Assert.assertEquals(outHexString, rd1Hex);
        BytesBuilder in = new BytesBuilder();
        long deNumber = BtEncode.deHex(BtIO.asInputStream(out.toByteArray()), in);
        BtLog.info("deNumber: {}", deNumber);
        Assert.assertEquals(deNumber, BtEncode.getHexLength(rd1.length()));
        String deHexString = BtString.toString8Bit(in.toByteArray());
        BtLog.info("deHexString: {}", deHexString);
        Assert.assertEquals(deHexString, rd1);
    }
}
