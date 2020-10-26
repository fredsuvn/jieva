package xyz.srclab.common.base

import java.io.InputStream
import java.io.PrintStream
import java.util.*

interface Shell {

    fun print(any: Any?)

    fun println(any: Any?)

    fun read(): String

    fun readLine(): String

    companion object {

        @JvmField
        val DEFAULT: Shell = DefaultShell
    }
}

abstract class StreamShell(input: InputStream, output: PrintStream) : Shell {

    private val scanner = Scanner(input)
    private val printStream = output

    override fun print(any: Any?) {
        printStream.print(any)
    }

    override fun println(any: Any?) {
        printStream.println(any)
    }

    override fun read(): String {
        return scanner.next()
    }

    override fun readLine(): String {
        return scanner.nextLine()
    }
}

object DefaultShell : StreamShell(System.`in`, System.out)