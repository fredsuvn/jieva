package test;

import org.testng.annotations.Test;
import xyz.fslabo.common.base.JieLog;

public class LogTest {

    @Test
    public void testLogger() {
        //system
        JieLog sysLog = JieLog.system();
        doLog(sysLog);
        //custom
        StringBuilder sb = new StringBuilder();
        JieLog cusLog = JieLog.of(JieLog.LEVEL_DEBUG, sb);
        doLog(cusLog);
        System.out.println(sb);
    }

    private void doLog(JieLog logger) {
        logger.trace("test trace");
        logger.debug("test debug");
        logger.info("test info");
        logger.warn("test warn");
        logger.error("test error");
        logger.log(JieLog.LEVEL_TRACE, "test trace-level");
        logger.log(JieLog.LEVEL_ERROR, "test error-level");
    }
}
