package test;

import org.testng.annotations.Test;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.base.JieLog;

public class LogTest {

    @Test
    public void testLogger() {
        //info
        JieLog logger = JieLog.system();
        logger.trace("test trace");
        logger.debug("test debug");
        logger.info("test info");
        logger.warn("test warn");
        logger.error("test error");
        Jie.log("Jie.log");
    }
}
