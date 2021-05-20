package test.xyz.srclab.common.codec;

import org.apache.commons.codec.binary.Hex;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import xyz.srclab.common.lang.Chars;
import xyz.srclab.common.lang.Defaults;
import xyz.srclab.common.test.TestLogger;

import java.util.Base64;

/**
 * @author sunqian
 */
public class CodecLibsTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    private String plainString = "好好学习天天向上！好好学习天天向上！好好学习天天向上！好好学习天天向上！";
    private String base64String;
    private String hexString;

    @BeforeTest
    public void init() {
        base64String = Base64.getEncoder().encodeToString(plainString.getBytes(Defaults.charset()));
        hexString = Hex.encodeHexString(plainString.getBytes(Defaults.charset()));
        logger.log("base64String: {}", base64String);
        logger.log("hexString: {}", hexString);
    }

    @Test
    public void testJdkBase64() {
        byte[] bytes = Base64.getDecoder().decode(base64String);
        String decode = Chars.toChars(bytes);
        logger.log("decode base64 by JDK: {}", decode);
        Assert.assertEquals(decode, plainString);
    }

    @Test
    public void testApacheBase64() {
        byte[] bytes = org.apache.commons.codec.binary.Base64.decodeBase64(base64String);
        String decode = Chars.toChars(bytes);
        logger.log("decode base64 by Apache: {}", decode);
        Assert.assertEquals(decode, plainString);
    }

    @Test
    public void testBouncycastleBase642() {
        byte[] bytes = org.bouncycastle.util.encoders.Base64.decode(base64String);
        String decode = Chars.toChars(bytes);
        logger.log("decode base64 by Bouncycastle: {}", decode);
        Assert.assertEquals(decode, plainString);
    }

    @Test
    public void testApacheHex() throws Exception {
        byte[] bytes = Hex.decodeHex(hexString);
        String decode = Chars.toChars(bytes);
        logger.log("decode hex by Apache: {}", decode);
        Assert.assertEquals(decode, plainString);
    }

    @Test
    public void testBouncycastleHex() {
        byte[] bytes = org.bouncycastle.util.encoders.Hex.decode(hexString);
        String decode = Chars.toChars(bytes);
        logger.log("decode hex by Bouncycastle: {}", decode);
        Assert.assertEquals(decode, plainString);
    }
}
