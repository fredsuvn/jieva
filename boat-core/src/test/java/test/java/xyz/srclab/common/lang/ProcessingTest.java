package test.java.xyz.srclab.common.lang;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.lang.Default;
import xyz.srclab.common.lang.Environment;
import xyz.srclab.common.lang.Processing;
import xyz.srclab.common.test.TestLogger;

public class ProcessingTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    private static final String ECHO_CONTENT = "ECHO_CONTENT";

    @Test
    public void testProcess() {
        if (Environment.isOsUnix()) {
            testProcessing("echo", ECHO_CONTENT);
        }
        if (Environment.isOsWindows()) {
            testProcessing("cmd.exe", "/c", "echo " + ECHO_CONTENT);
        }
    }

    @Test
    public void testPing() {
        testProcessingByPing();
    }

    private void testProcessing(String... command) {
        Processing processing = Processing.newProcessing(command);
        processing.waitForTermination();
        String output = processing.outputString();
        logger.log(output);
        Assert.assertEquals(output, ECHO_CONTENT + Default.lineSeparator());
    }

    private void testProcessingByPing() {
        Processing processing = Processing.newProcessing("ping", "127.0.0.1");
        String output = processing.outputString();
        processing.waitForTermination();
        logger.log(output);
    }
}
