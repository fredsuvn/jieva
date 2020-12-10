package xyz.srclab.common.test

import java.io.PrintStream

interface TestLogger {

    fun log(message: Any?)

    companion object {

        @JvmField
        val DEFAULT: TestLogger = TestLoggerImpl(System.out)

        fun withPrintStream(printStream: PrintStream): TestLogger {
            return TestLoggerImpl(printStream)
        }

        private class TestLoggerImpl(private val printStream: PrintStream) : TestLogger {
            override fun log(message: Any?) {
                printStream.println(message)
            }
        }
    }
}