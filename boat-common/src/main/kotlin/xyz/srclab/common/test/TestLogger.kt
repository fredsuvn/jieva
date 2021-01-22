package xyz.srclab.common.test

import xyz.srclab.common.base.Current
import xyz.srclab.common.base.Format.Companion.fastFormat
import xyz.srclab.common.base.Format.Companion.printFormat
import xyz.srclab.common.base.asAny
import xyz.srclab.common.reflect.contractSignatureName
import java.io.PrintStream

interface TestLogger {

    fun log(message: Any?)

    @JvmDefault
    fun log(pattern: CharSequence, vararg args: Any?) {
        log(pattern.fastFormat(*args).asAny<Any?>())
    }

    companion object {

        @JvmField
        val DEFAULT: TestLogger = TestLoggerImpl(System.out)

        fun withPrintStream(printStream: PrintStream): TestLogger {
            return TestLoggerImpl(printStream)
        }

        private class TestLoggerImpl(private val printStream: PrintStream) : TestLogger {

            override fun log(message: Any?) {
                val callFrame = Current.callerFrameOrNull(javaClass.name, "log")
                printStream.println(
                    "%-${CLASS_NAME_MAX_LENGTH}s(%-${LINE_NUMBER_MAX_LENGTH}d): %s".printFormat(
                        callFrame?.className?.contractSignatureName(CLASS_NAME_MAX_LENGTH),
                        callFrame?.lineNumber,
                        message
                    )
                )
            }

            companion object {

                private const val CLASS_NAME_MAX_LENGTH = 30

                private const val LINE_NUMBER_MAX_LENGTH = 4
            }
        }
    }
}