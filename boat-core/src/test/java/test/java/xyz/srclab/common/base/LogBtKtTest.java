package test.java.xyz.srclab.common.base;

import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BString;
import xyz.srclab.common.base.Logger;

import java.io.ByteArrayOutputStream;

public class LogBtKtTest {

    @Test
    public void testLog() {
        BLog.error("This is an error message: {}", "BLog Test!");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Logger logger = Logger.newLogger(Logger.INFO_LEVEL, out);
        logger.trace("This is an trace message: {}", "BLog Test!");
        logger.debug("This is an debug message: {}", "BLog Test!");
        logger.info("This is an info message: {}", "BLog Test!");
        logger.warn("This is an warn message: {}", "BLog Test!");
        logger.error("This is an error message: {}", "BLog Test!");
        logger.log(Logger.INFO_LEVEL - 1, "This is an log message: {}", "BLog Test!");
        logger.log(Logger.INFO_LEVEL + 1, "This is an log message: {}", "BLog Test!");
        String logs = BString.toString(out.toByteArray());
        System.out.println(logs);
    }
}
