package test.java.xyz.srclab.common.base;

import org.testng.annotations.Test;
import xyz.srclab.common.base.BtLog;
import xyz.srclab.common.base.BtString;
import xyz.srclab.common.base.Logger;

import java.io.ByteArrayOutputStream;

public class BtLogTest {

    @Test
    public void testLog() {
        BtLog.error("This is an error message: {}", "BLog Test!");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Logger logger = Logger.newLogger(Logger.LEVEL_INFO, out);
        logger.trace("This is an trace message: {}", "BLog Test!");
        logger.debug("This is an debug message: {}", "BLog Test!");
        logger.info("This is an info message: {}", "BLog Test!");
        logger.warn("This is an warn message: {}", "BLog Test!");
        logger.error("This is an error message: {}", "BLog Test!");
        logger.log(Logger.LEVEL_INFO - 1, "This is an log message: {}", "BLog Test!");
        logger.log(Logger.LEVEL_INFO + 1, "This is an log message: {}", "BLog Test!");
        String logs = BtString.toString(out.toByteArray());
        System.out.println(logs);
    }
}
