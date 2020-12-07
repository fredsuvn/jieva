package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Defaults;
import xyz.srclab.common.base.Environment;
import xyz.srclab.common.base.Shell;
import xyz.srclab.common.base.ShellProcess;

import java.util.Arrays;

public class ShellTest {

    @Test
    public void testShell() {
        Shell shell = Shell.DEFAULT;
        shell.println("Hello", ",", "World", "!");
        shell.println(Arrays.asList("Hello", ",", "World", "!"));
        ShellProcess shellProcess = shell.run("ping", "127.0.0.1");
        //shellProcess.waitFor();
        shell.println(shellProcess.readLine());
        shell.println(shellProcess.readLine());
        shell.println(shellProcess.readLine());
        shell.println(shellProcess.readLine());

        shellProcess = shell.run("echo", "-e", "\033[31m中文");
        shell.println(shellProcess.readLine());

        shell.println("\u001b[31m中文");
        System.out.println("\u001b[31m中文");

        shellProcess = shell.run("ls", "-ahl");
        shell.println(shellProcess.readAll());

        shellProcess = shell.run("ls");
        shell.println(shellProcess.readAll());

        shellProcess = shell.run("ls");
        shell.println(shellProcess.readAll());

        shell.println("123\u0008456");
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
