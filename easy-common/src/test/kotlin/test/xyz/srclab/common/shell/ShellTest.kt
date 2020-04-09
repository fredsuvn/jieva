package test.xyz.srclab.common.shell

import org.testng.annotations.Test
import xyz.srclab.common.shell.Shell

object ShellTest {

    @Test
    fun testShell() {
        val shell = Shell.DEFAULT
        shell.println("123")
    }
}