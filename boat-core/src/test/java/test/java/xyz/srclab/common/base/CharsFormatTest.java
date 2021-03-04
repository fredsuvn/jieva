package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.CharsFormat;

/**
 * @author sunqian
 */
public class CharsFormatTest {

    @Test
    public void testFormat() {
        String fastFormat = CharsFormat.fastFormat("This is {} {}.", "fast", "format");
        Assert.assertEquals(fastFormat, "This is fast format.");
        String printfFormat = CharsFormat.printfFormat("This is %s %s.", "printf", "format");
        Assert.assertEquals(printfFormat, "This is printf format.");
        String messageFormat = CharsFormat.messageFormat("This is {0} {1}.", "message", "format");
        Assert.assertEquals(messageFormat, "This is message format.");

        String escapeFormat = CharsFormat.fastFormat("{}, {}, \\{}, \\\\", 1, 2, 3);
        Assert.assertEquals(escapeFormat, "1, 2, {}, \\\\");
    }
}
