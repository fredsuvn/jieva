package test.java.xyz.srclab.common.base;

import org.apache.commons.lang.SystemUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BProcessing;
import xyz.srclab.common.base.Systems;
import xyz.srclab.common.logging.Logs;

import java.time.Duration;

public class ProcessingTest {

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
        BProcessing processing = BProcessing.start(command);
        processing.await();
        String output = processing.outputString();
        Logs.info(output);
        Assert.assertEquals(output, ECHO_CONTENT + Systems.getLineSeparator());
    }

    private void testProcessingByPing() {
        BProcessing processing = BProcessing.start("ping 127.0.0.1");
        processing.await(Duration.ofSeconds(2));
        String output = processing.availableOutputString();
        Logs.info(output);
        processing.destroy(true);
    }
}
