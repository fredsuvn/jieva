package test.java.xyz.srclab.common.base;

import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BString;
import xyz.srclab.common.base.LogPrinter;

import java.io.ByteArrayOutputStream;

public class BLogTest {

    @Test
    public void testLog() {
        BLog.error("This is an error message: {}", "BLog Test!");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        LogPrinter logPrinter = LogPrinter.simpleLogger(LogPrinter.INFO_LEVEL, out);
        logPrinter.trace("This is an trace message: {}", "BLog Test!");
        logPrinter.debug("This is an debug message: {}", "BLog Test!");
        logPrinter.info("This is an info message: {}", "BLog Test!");
        logPrinter.warn("This is an warn message: {}", "BLog Test!");
        logPrinter.error("This is an error message: {}", "BLog Test!");
        logPrinter.log(LogPrinter.INFO_LEVEL - 1, "This is an log message: {}", "BLog Test!");
        logPrinter.log(LogPrinter.INFO_LEVEL + 1, "This is an log message: {}", "BLog Test!");
        String logs = BString.toString(out.toByteArray());
        System.out.println(logs);
    }
}
