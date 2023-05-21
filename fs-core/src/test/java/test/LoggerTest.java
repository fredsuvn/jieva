package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.FsLogger;
import xyz.srclab.common.base.ref.IntRef;

public class LoggerTest {

    @Test
    public void testLogger() {
        FsLogger logger = FsLogger.ofLevel(FsLogger.INFO_LEVEL);
        logger.trace("test trace");
        logger.debug("test debug");
        logger.info("test info");
        logger.warn("test warn");
        logger.error("test error");
        IntRef count = new IntRef();
        FsLogger logger2 = FsLogger.newLogger(
            FsLogger.INFO_LEVEL,
            it -> count.getAndIncrement()
        );
        logger2.trace("test trace");
        logger2.debug("test debug");
        logger2.info("test info");
        logger2.warn("test warn");
        logger2.error("test error");
        Assert.assertEquals(count.get(), 3);
    }
}
