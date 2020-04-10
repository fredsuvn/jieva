package test.xyz.srclab.common.shell

import org.testng.annotations.Test
import test.xyz.srclab.common.doAssertEquals
import xyz.srclab.common.base.Defaults
import xyz.srclab.common.shell.Shell
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.random.Random

object ShellTest {

    @ExperimentalStdlibApi
    @Test
    fun testShell() {
        val input = System.`in`
        val output = System.out
        val message = Random.nextDouble().toString() + Defaults.LINE_SEPARATOR
        val buf = message.encodeToByteArray()
        val src = ByteArrayInputStream(buf)
        val dest = ByteArrayOutputStream()
        System.setIn(src)
        System.setOut(PrintStream(dest))

        val shell = Shell.newDefault()
        val inputMessage = shell.read()
        shell.println(inputMessage)
        val outputMessage = dest.toString(Defaults.CHARSET.name())
        doAssertEquals(outputMessage, message)

        System.setIn(input)
        System.setOut(output)
    }
}