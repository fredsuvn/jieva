package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Chars;
import xyz.srclab.common.logging.Logger;

/**
 * @author sunqian
 */
public class CharsTest {

    private static final Logger logger = Logger.simpleLogger();

    @Test
    public void testChars() {
        Assert.assertEquals(Chars.ellipses("123456789", 5), "12...");
    }
}
