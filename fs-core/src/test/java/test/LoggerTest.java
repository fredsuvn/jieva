package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.FsLogger;
import xyz.srclab.common.base.ref.FsRef;
import xyz.srclab.common.base.ref.IntRef;

public class LoggerTest {

    @Test
    public void testLogger() {
        //info
        FsLogger logger = FsLogger.defaultLogger();
        logger.trace("test trace");
        logger.debug("test debug");
        logger.info("test info");
        logger.warn("test warn");
        logger.error("test error");
        IntRef count = FsRef.ofInt(0);
        FsLogger logger2 = FsLogger.newBuilder()
            .level(FsLogger.Level.DEBUG).formatter(it -> {
                count.getAndIncrement();
            }).build();
        logger2.trace("test trace");
        logger2.debug("test debug");
        logger2.info("test info");
        logger2.warn("test warn");
        logger2.error("test error");
        Assert.assertEquals(count.get(), 4);
    }
}
