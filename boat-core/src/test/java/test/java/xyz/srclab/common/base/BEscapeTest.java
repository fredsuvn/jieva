package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BEscape;

public class BEscapeTest {

    @Test
    public void testEscape() {
        String text = "{\"abc\":\"xyz\"}";
        String eText = BEscape.escape(text, '\\', '\"');
        Assert.assertEquals(eText, "{\\\"abc\\\":\\\"xyz\\\"}");

        text = "{\\\"abc\\\":\\\"xyz\\\"}";
        String uText = BEscape.unescape(text, '\\', '\"');
        Assert.assertEquals(uText, "{\"abc\":\"xyz\"}");

        text = "{\\\"abc\\\":\\\"x\\yz\\\"}\\";
        uText = BEscape.unescape(text, '\\', '\"');
        Assert.assertEquals(uText, "{\"abc\":\"x\\yz\"}\\");
    }
}
