package test.java.xyz.srclab.common.base;

import org.apache.commons.lang.SystemUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Processing;
import xyz.srclab.common.base.Systems;
import xyz.srclab.common.logging.Logger;

import java.time.Duration;

public class ProcessingTest {

    private static final Logger logger = Logger.simpleLogger();

    private static final String ECHO_CONTENT = "ECHO_CONTENT";

    @Test
    public void testProcess() {
        if (SystemUtils.IS_OS_UNIX) {
            testProcessing("echo", ECHO_CONTENT);
        }
        if (SystemUtils.IS_OS_WINDOWS) {
            testProcessing("cmd.exe", "/c", "echo " + ECHO_CONTENT);
        }
    }

    @Test
    public void testPing() {
        testProcessingByPing();
    }

    private void testProcessing(String... command) {
        Processing processing = Processing.start(command);
        processing.await();
        String output = processing.outputString();
        logger.info(output);
        Assert.assertEquals(output, ECHO_CONTENT + Systems.getLineSeparator());
    }

    private void testProcessingByPing() {
        Processing processing = Processing.start("ping 127.0.0.1");
        processing.await(Duration.ofSeconds(2));
        String output = processing.availableOutputString();
        logger.info(output);
        processing.destroy(true);
    }
}
