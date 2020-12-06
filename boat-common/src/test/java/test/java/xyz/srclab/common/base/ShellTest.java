package test.java.xyz.srclab.common.base;

import org.testng.annotations.Test;
import xyz.srclab.common.base.Shell;
import xyz.srclab.common.base.ShellProcess;

public class ShellTest {

    @Test
    public void testShell() {
        Shell shell = Shell.DEFAULT;
        shell.println("Hello", ",", "World", "!");
        ShellProcess shellProcess = shell.run("ping", "127.0.0.1");
        shellProcess.waitFor();
        shell.println(shellProcess.read());
        shell.println(shellProcess.read());
        shell.println(shellProcess.read());
        shell.println(shellProcess.read());
    }
}
