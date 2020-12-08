package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.*;

import java.util.Arrays;

public class ShellTest {

    @Test
    public void testShell() {
        Shell shell = Shell.DEFAULT;
        shell.println("Hello", ",", "World", "!");
        shell.println(Arrays.asList("Hello", ",", "World", "!"));
        shell.println("123", EscapeChars.linefeed(), "456", EscapeChars.newline(), EscapeChars.reset());
        shell.println(
                SgiChars.foregroundRed("red"),
                SgiChars.backgroundCyan(" "),
                SgiChars.foregroundGreen("green")
        );
        shell.println(
                SgiChars.withParam("bright red", SgiParam.FOREGROUND_BRIGHT_RED),
                SgiChars.backgroundCyan(" "),
                SgiChars.withParam("bright green", SgiParam.FOREGROUND_BRIGHT_GREEN)
        );
        shell.println(
                SgiChars.withParam("color 8", SgiParam.foregroundColor(8)),
                SgiChars.backgroundCyan(" "),
                SgiChars.withParam("rgb(100, 100, 50)", SgiParam.foregroundColor(100, 100, 50))
        );
        shell.println(ControlChars.beep());
        System.out.println("123\010456\007");
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
        String content = "first line;" + Defaults.lineSeparator() +
                "第二行;" + Defaults.lineSeparator() +
                "third line.";
        ShellProcess shellProcess = shell.run("echo", content);
        String output = shellProcess.readAll();
        System.out.println(output);
        Assert.assertEquals(output, content + Defaults.lineSeparator());
        shellProcess.close();
    }

    private void testShellProcessOnWindows() {
        Shell shell = Shell.DEFAULT;
        ShellProcess shellProcess = shell.run("ping", "127.0.0.1");
        String output = shellProcess.readAll();
        System.out.println(output);
    }
}
