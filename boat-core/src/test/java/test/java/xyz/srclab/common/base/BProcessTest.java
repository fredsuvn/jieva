package test.java.xyz.srclab.common.base;

import org.apache.commons.lang.SystemUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BProcess;
import xyz.srclab.common.base.BSystem;
import xyz.srclab.common.base.ProcessWork;

import java.time.Duration;

public class BProcessTest {

    private static final String ECHO_CONTENT = "ECHO_CONTENT";

    @Test
    public void testProcess() {
        if (SystemUtils.IS_OS_UNIX) {
            echo("echo", ECHO_CONTENT);
        }
        if (SystemUtils.IS_OS_WINDOWS) {
            echo("cmd.exe", "/c", "echo " + ECHO_CONTENT);
        }
    }

    @Test
    public void testPing() {
        ProcessWork processing = BProcess.startProcess("ping 127.0.0.1");
        processing.await(Duration.ofSeconds(2));
        String output = processing.availableOutputString();
        BLog.info(output);
        processing.destroy(true);
    }

    private void echo(String... command) {
        ProcessWork processWork = BProcess.startProcess(command);
        processWork.await();
        String output = processWork.outputString();
        BLog.info(output);
        Assert.assertEquals(output, ECHO_CONTENT + BSystem.getLineSeparator());
    }
}
