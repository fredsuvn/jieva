package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Format;

/**
 * @author sunqian
 */
public class FormatTest {

    @Test
    public void testFormat() {
        String fastFormat = Format.fastFormat("This is {} {}.", "fast", "format");
        Assert.assertEquals(fastFormat, "This is fast format.");
        String printfFormat = Format.printfFormat("This is %s %s.", "printf", "format");
        Assert.assertEquals(printfFormat, "This is printf format.");
        String messageFormat = Format.messageFormat("This is {0} {1}.", "message", "format");
        Assert.assertEquals(messageFormat, "This is message format.");
    }
}
