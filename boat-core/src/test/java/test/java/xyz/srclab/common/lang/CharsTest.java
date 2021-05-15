package test.java.xyz.srclab.common.lang;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.lang.Chars;
import xyz.srclab.common.test.TestLogger;

/**
 * @author sunqian
 */
public class CharsTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testChars() {
        Assert.assertEquals(Chars.ellipses("123456789", 5), "12...");
    }
}
