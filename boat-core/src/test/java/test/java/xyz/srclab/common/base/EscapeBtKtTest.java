package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BEscape;
import xyz.srclab.common.io.BIO;
import xyz.srclab.common.io.BytesAppender;

public class EscapeBtKtTest {

    @Test
    public void testEscape() {
        doTestEscape('\\', "\"", "{\"abc\":\"xyz\"}", "{\\\"abc\\\":\\\"xyz\\\"}");
        doTestEscape('\\', "\"{}", "{\"ss\": \"sss\\n\"}", "\\{\\\"ss\\\": \\\"sss\\\\n\\\"\\}");
    }

    @Test
    public void testUnescape() {
        doTestUnescape('\\', "\"", "{\\\"abc\\\":\\\"xyz\\\"}", "{\"abc\":\"xyz\"}");
        doTestUnescape('\\', "\"", "{\\\"abc\\\":\\\"x\\yz\\\"}\\", "{\"abc\":\"x\\yz\"}\\");
        doTestUnescape('\\', "\"{}", "\\{\\\"ss\\\": \\\"sss\\\\n\\\"\\}", "{\"ss\": \"sss\\n\"}");
        doTestUnescape('\\', "\"{}", "\\{\\\"ss\\\": \\\"s\\css\\\\n\\\"\\}", "{\"ss\": \"s\\css\\n\"}");
    }

    private void doTestEscape(char escape, String escapedChars, String input, String expected) {
        String encoded = BEscape.escape(input, escape, escapedChars);
        Assert.assertEquals(encoded, expected);

        BytesAppender bytesDest = new BytesAppender();
        BEscape.escape(BIO.asInputStream(input.getBytes()), (byte) escape, escapedChars.getBytes(), bytesDest);
        Assert.assertEquals(new String(bytesDest.toBytes()), expected);

        StringBuilder charsDest = new StringBuilder();
        BEscape.escape(BIO.asReader(input), escape, escapedChars, charsDest);
        Assert.assertEquals(charsDest.toString(), expected);
    }

    private void doTestUnescape(char escape, String escapedChars, String input, String expected) {
        String encoded = BEscape.unescape(input, escape, escapedChars);
        Assert.assertEquals(encoded, expected);

        BytesAppender bytesDest = new BytesAppender();
        BEscape.unescape(BIO.asInputStream(input.getBytes()), (byte) escape, escapedChars.getBytes(), bytesDest);
        Assert.assertEquals(new String(bytesDest.toBytes()), expected);

        StringBuilder charsDest = new StringBuilder();
        BEscape.unescape(BIO.asReader(input), escape, escapedChars, charsDest);
        Assert.assertEquals(charsDest.toString(), expected);
    }
}
