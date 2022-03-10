package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BProcess;
import xyz.srclab.common.base.BSystem;
import xyz.srclab.common.base.ProcWork;

import java.time.Duration;

public class BProcessTest {

    private static final String ECHO_CONTENT = "ECHO_CONTENT";

    @Test
    public void testProcess() {
        if (BSystem.isLinux()) {
            echo("echo", ECHO_CONTENT);
        }
        if (BSystem.isWindows()) {
            echo("cmd.exe", "/c", "echo " + ECHO_CONTENT);
        }
    }

    @Test
    public void testPing() {
        ProcWork work = BProcess.startProcess("ping 127.0.0.1");
        work.getResult(Duration.ofSeconds(2));
        String output = work.availableOutputString();
        BLog.info(output);
        work.cancel(true);
    }

    private void echo(String... command) {
        ProcWork work = BProcess.startProcess(command);
        work.getResult();
        String output = work.outputString();
        BLog.info(output);
        Assert.assertEquals(output, ECHO_CONTENT + BSystem.getLineSeparator());
    }
}
