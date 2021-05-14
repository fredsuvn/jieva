package test.java.xyz.srclab.common.lang;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.lang.*;
import xyz.srclab.common.test.TestLogger;

import java.util.Arrays;

public class ShellTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testShell() {
        Shell shell = Shell.DEFAULT;
        shell.println("Hello", ",", "World", "!");
        shell.println(Arrays.asList("Hello", ",", "World", "!"));
        shell.println("123", EscapeChars.linefeed(), "456", EscapeChars.newline(), EscapeChars.reset());
        shell.println(
            SgrChars.foregroundRed("red"),
            SgrChars.backgroundCyan(" "),
            SgrChars.foregroundGreen("green")
        );
        shell.println(
            SgrChars.withParam("bright red", SgrParam.FOREGROUND_BRIGHT_RED),
            SgrChars.backgroundCyan(" "),
            SgrChars.withParam("bright green", SgrParam.FOREGROUND_BRIGHT_GREEN)
        );
        shell.println(
            SgrChars.withParam("color 8", SgrParam.foregroundColor(8)),
            SgrChars.backgroundCyan(" "),
            SgrChars.withParam("rgb(100, 100, 50)", SgrParam.foregroundColor(100, 100, 50))
        );
        shell.println(ControlChars.beep());
        logger.log("123\010456\007");
        shell.println("123", ControlChars.backspaces(), "456", ControlChars.beep());
    }

    @Test
    public void testShellProcess() {
        if (Environment.isOsUnix()) {
            testShellProcessOnUnixLike();
        } else {
            testShellProcessOnWindows();
        }
    }

    private void testShellProcessOnUnixLike() {
        Shell shell = Shell.DEFAULT;
        String content = "first line;" + Default.lineSeparator() +
            "第二行;" + Default.lineSeparator() +
            "third line.";
        ShellProcess shellProcess = shell.run("echo", content);
        String output = shellProcess.readAll();
        logger.log(output);
        Assert.assertEquals(output, content + Default.lineSeparator());
        shellProcess.close();
    }

    private void testShellProcessOnWindows() {
        Shell shell = Shell.DEFAULT;
        ShellProcess shellProcess = shell.run("ping", "127.0.0.1");
        String output = shellProcess.readAll();
        logger.log(output);
    }
}
