package test.java.xyz.srclab.common.lang;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.lang.CharsFormat;
import xyz.srclab.common.test.TestLogger;

/**
 * @author sunqian
 */
public class CharsFormatTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testFormat() {
        String fastFormat = CharsFormat.fastFormat("This is {} {}.", "fast", "format");
        Assert.assertEquals(fastFormat, "This is fast format.");
        String printfFormat = CharsFormat.printfFormat("This is %s %s.", "printf", "format");
        Assert.assertEquals(printfFormat, "This is printf format.");
        String messageFormat = CharsFormat.messageFormat("This is {0} {1}.", "message", "format");
        Assert.assertEquals(messageFormat, "This is message format.");
    }

    @Test
    public void testFastFormat() {
        assertEquals(
            "This is fast format. That is a " + new NullPointerException(),
            CharsFormat.FAST_FORMAT,
            "This is {} {}. That is a {}",
            "fast", "format", new NullPointerException()
        );
        assertEquals(
            "1, 2, {}, \\\\",
            CharsFormat.FAST_FORMAT,
            "{}, {}, \\{}, \\\\",
            1, 2, 3
        );
    }

    @Test
    public void testPrintfFormat() {
        assertEquals(
            "This is printf format. That is a " + new NullPointerException(),
            CharsFormat.PRINTF_FORMAT,
            "This is %s %s. That is a %s",
            "printf", "format", new NullPointerException()
        );
    }

    @Test
    public void testMessageFormat() {
        assertEquals(
            "This is message format. That is a " + new NullPointerException(),
            CharsFormat.MESSAGE_FORMAT,
            "This is {0} {1}. That is a {2}",
            "message", "format", new NullPointerException()
        );

    }

    private void assertEquals(
        String expected,
        CharsFormat format,
        CharSequence pattern,
        Object... args
    ) {
        String actual = format.format(pattern, args);
        logger.log("Test CharsFormat ({}): {}", format, actual);
        Assert.assertEquals(actual, expected);
    }
}
