package xyz.srclab.common.test

import xyz.srclab.common.lang.CharsFormat
import xyz.srclab.common.lang.CharsFormat.Companion.fastFormat
import xyz.srclab.common.lang.CharsFormat.Companion.printfFormat
import xyz.srclab.common.lang.Current
import xyz.srclab.common.lang.asAny
import java.io.PrintStream

/**
 * Logger for test.
 */
interface TestLogger {

    fun log(message: Any?)

    /**
     * Using [CharsFormat.fastFormat] to format.
     */
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
                    "%-${CLASS_NAME_MAX_LENGTH}s(%-${LINE_NUMBER_MAX_LENGTH}d): %s".printfFormat(
                        callFrame?.className?.abridgeName(CLASS_NAME_MAX_LENGTH),
                        callFrame?.lineNumber,
                        message
                    )
                )
            }

            /**
             * Contracts full signature name:
             * * abc.xyz.Foo -> a.x.Foo
             *
             * If given [this] has an illegal format, the result will be undefined.
             */
            private fun CharSequence.abridgeName(maxLength: Int = 0): String {

                val split = this.split(".")
                val wordCount = split.size
                val tailIndex = split.size - 1

                fun cutWords(split: List<String>, separatorIndex: Int): String {
                    val buffer = StringBuilder(split[tailIndex])
                    for (i in (0 until tailIndex).reversed()) {
                        buffer.insert(0, ".")
                        if (i < separatorIndex) {
                            buffer.insert(0, split[i][0])
                        } else {
                            buffer.insert(0, split[i])
                        }
                    }
                    return buffer.toString()
                }

                val minLength = (wordCount - 1) * 2 + split[wordCount - 1].length
                if (minLength >= maxLength) {
                    return cutWords(split, tailIndex)
                }
                var newLength = minLength
                for (i in tailIndex - 1 downTo 0) {
                    newLength += split[i].length - 1
                    if (newLength > maxLength) {
                        return cutWords(split, i + 1)
                    }
                    if (newLength == maxLength) {
                        return cutWords(split, i)
                    }
                }
                return cutWords(split, 0)
            }

            companion object {

                private const val CLASS_NAME_MAX_LENGTH = 30

                private const val LINE_NUMBER_MAX_LENGTH = 4
            }
        }
    }
}