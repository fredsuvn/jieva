package test.java.xyz.srclab.common.base;

import cn.hutool.core.util.HexUtil;
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
            BEncode.base64("1qaz2wsx!QAZ@WSX"),
            "MXFhejJ3c3ghUUFaQFdTWA=="
        );
        Assert.assertEquals(
            BEncode.deBase64("MXFhejJ3c3ghUUFaQFdTWA=="),
            "1qaz2wsx!QAZ@WSX"
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
        long enNumber = BEncode.base64(BIO.asInputStream(rd1.getBytes(BDefault.charset())), out);
        BLog.info("enNumber: {}", enNumber);
        Assert.assertEquals(enNumber, rd1.length());
        String outBase64String = BString.toString8Bit(out.toBytes());
        BLog.info("outBase64String: {}", outBase64String);
        Assert.assertEquals(outBase64String, rd1Base64);
        BytesAppender in = new BytesAppender();
        long deNumber = BEncode.deBase64(BIO.asInputStream(out.toBytes()), in);
        BLog.info("deNumber: {}", deNumber);
        Assert.assertEquals(deNumber, BEncode.getBase64Length(rd1.length()));
        String deBase64String = BString.toString8Bit(in.toBytes());
        BLog.info("deBase64String: {}", deBase64String);
        Assert.assertEquals(deBase64String, rd1);
    }

    @Test
    public void testHex() {
        Assert.assertEquals(
            BEncode.hex("1qaz2wsx!QAZ@WSX"),
            "3171617a327773782151415a40575358"
        );
        Assert.assertEquals(
            BEncode.deHex("3171617a327773782151415a40575358"),
            "1qaz2wsx!QAZ@WSX"
        );

        String rd1 = BRandom.randomString(100);
        String rd1Hex = BEncode.hex(rd1);
        byte[] rd1Bytes = rd1.getBytes(BDefault.charset());
        BLog.info("rd1: {}, rd1.hex: {}", rd1, rd1Hex);
        Assert.assertEquals(rd1Hex, new String(HexUtil.encodeHex(rd1.getBytes(BDefault.charset()))));
        Assert.assertEquals(
            new String(BEncode.hex(rd1Bytes, 10, 22)),
            new String(HexUtil.encodeHex(Arrays.copyOfRange(rd1Bytes, 10, 10 + 22)))
        );
        String rd2 = BRandom.randomString(1);
        String rd2Hex = BEncode.hex(rd2);
        BLog.info("rd2: {}, rd2.hex: {}", rd2, rd2Hex);
        Assert.assertEquals(rd2Hex, new String(HexUtil.encodeHex(rd2.getBytes(BDefault.charset()))));

        String rd1DeHex = BEncode.deHex(rd1Hex);
        byte[] rd1HexBytes = rd1Hex.getBytes(BDefault.charset());
        BLog.info("rd1.de-hex: {}", rd1DeHex);
        Assert.assertEquals(rd1DeHex, rd1);
        Assert.assertEquals(
            BEncode.deHex(rd1HexBytes, 4 * 3, 4 * 10),
            HexUtil.decodeHex(new String(Arrays.copyOfRange(rd1HexBytes, 4 * 3, 4 * 3 + 4 * 10)))
        );
        String rd2DeHex = BEncode.deHex(rd2Hex);
        BLog.info("rd2.de-hex: {}", rd2DeHex);
        Assert.assertEquals(rd2DeHex, new String(HexUtil.decodeHex(new String(rd2Hex.getBytes(BDefault.charset())))));

        //Test output stream
        BytesAppender out = new BytesAppender();
        long enNumber = BEncode.hex(BIO.asInputStream(rd1.getBytes(BDefault.charset())), out);
        BLog.info("enNumber: {}", enNumber);
        Assert.assertEquals(enNumber, rd1.length());
        String outHexString = BString.toString8Bit(out.toBytes());
        BLog.info("outHexString: {}", outHexString);
        Assert.assertEquals(outHexString, rd1Hex);
        BytesAppender in = new BytesAppender();
        long deNumber = BEncode.deHex(BIO.asInputStream(out.toBytes()), in);
        BLog.info("deNumber: {}", deNumber);
        Assert.assertEquals(deNumber, BEncode.getHexLength(rd1.length()));
        String deHexString = BString.toString8Bit(in.toBytes());
        BLog.info("deHexString: {}", deHexString);
        Assert.assertEquals(deHexString, rd1);
    }
}
