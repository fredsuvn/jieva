package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BtEscape;
import xyz.srclab.common.base.BytesBuilder;
import xyz.srclab.common.io.BtIO;

public class BtEscapeTest {

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
        String encoded = BtEscape.escape(input, escape, escapedChars);
        Assert.assertEquals(encoded, expected);

        BytesBuilder bytesDest = new BytesBuilder();
        BtEscape.escape(BtIO.asInputStream(input.getBytes()), (byte) escape, escapedChars.getBytes(), bytesDest);
        Assert.assertEquals(new String(bytesDest.toByteArray()), expected);

        StringBuilder charsDest = new StringBuilder();
        BtEscape.escape(BtIO.asReader(input), escape, escapedChars, charsDest);
        Assert.assertEquals(charsDest.toString(), expected);
    }

    private void doTestUnescape(char escape, String escapedChars, String input, String expected) {
        String encoded = BtEscape.unescape(input, escape, escapedChars);
        Assert.assertEquals(encoded, expected);

        BytesBuilder bytesDest = new BytesBuilder();
        BtEscape.unescape(BtIO.asInputStream(input.getBytes()), (byte) escape, escapedChars.getBytes(), bytesDest);
        Assert.assertEquals(new String(bytesDest.toByteArray()), expected);

        StringBuilder charsDest = new StringBuilder();
        BtEscape.unescape(BtIO.asReader(input), escape, escapedChars, charsDest);
        Assert.assertEquals(charsDest.toString(), expected);
    }
}
