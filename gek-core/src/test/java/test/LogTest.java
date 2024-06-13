package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.common.base.Gek;
import xyz.fslabo.common.base.GekLog;
import xyz.fslabo.common.ref.Var;
import xyz.fslabo.common.ref.IntVar;

public class LogTest {

    @Test
    public void testLogger() {
        //info
        GekLog logger = GekLog.getInstance();
        logger.trace("test trace");
        logger.debug("test debug");
        logger.info("test info");
        logger.warn("test warn");
        logger.error("test error");
        IntVar count = Var.ofInt(0);
        GekLog logger2 = getGekLog2(count);
        logger2.trace("test trace");
        logger2.debug("test debug");
        logger2.info("test info");
        logger2.warn("test warn");
        logger2.error("test error");
        Assert.assertEquals(count.get(), 4);

        Gek.log("Gek.log");
    }

    private static GekLog getGekLog2(IntVar count) {
        return new GekLog() {
            @Override
            protected Record buildRecord(int traceOffset, Level level, Object... messages) {
                return super.buildRecord(traceOffset + 1, level, messages);
            }

            @Override
            protected void writeRecord(Record record) {
                count.getAndIncrement();
                super.writeRecord(record);
            }

            @Override
            public Level getLevel() {
                return Level.DEBUG;
            }
        };
    }
}
