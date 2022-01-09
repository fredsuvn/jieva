package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BFormat;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.FastFormat;
import xyz.srclab.common.base.StringFormat;

/**
 * @author sunqian
 */
public class BFormatTest {

    @Test
    public void testFormat() {
        String fastFormat = BFormat.fastFormat("This is {} {}.", "fast", "format");
        Assert.assertEquals(fastFormat, "This is fast format.");
        String fastFormat2 = BFormat.fastFormat("This is {} {} {} {}.", "fast", "format");
        Assert.assertEquals(fastFormat2, "This is fast format {} {}.");
    }

    @Test
    public void testFastFormat() {
        assertEquals(
            "This is fast format. That is a " + new NullPointerException(),
            FastFormat.INSTANCE,
            "This is {} {}. That is a {}",
            "fast", "format", new NullPointerException()
        );
        assertEquals(
            "1, 2, {}, \\} \\ \\",
            FastFormat.INSTANCE,
            "{}, {}, \\{}, \\} \\\\ \\",
            1, 2, 3
        );
    }

    private void assertEquals(
        String expected,
        StringFormat format,
        CharSequence pattern,
        Object... args
    ) {
        String actual = format.format(pattern, args);
        BLog.info("Test CharsFormat ({}): {}", format, actual);
        Assert.assertEquals(actual, expected);
    }
}
