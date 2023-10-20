package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fsgek.common.base.GekLogger;
import xyz.fsgek.common.base.ref.GekRef;
import xyz.fsgek.common.base.ref.IntRef;

public class LoggerTest {

    @Test
    public void testLogger() {
        //info
        GekLogger logger = GekLogger.defaultLogger();
        logger.trace("test trace");
        logger.debug("test debug");
        logger.info("test info");
        logger.warn("test warn");
        logger.error("test error");
        IntRef count = GekRef.ofInt(0);
        GekLogger logger2 = GekLogger.newBuilder()
            .level(GekLogger.Level.DEBUG).formatter(it -> {
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
