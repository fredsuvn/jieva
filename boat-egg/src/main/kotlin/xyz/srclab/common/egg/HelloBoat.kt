package xyz.srclab.common.egg

import java.io.PrintStream

class HelloBoat : Egg {

    private lateinit var printer: PrintStream

    override val readme: String = TITLE

    override fun hatchOut(spell: String, feed: Map<Any, Any>) {
        if (spell != TITLE) {
            throw WrongSpellException()
        }
        val out = feed["out"]
        printer = if (out !is PrintStream) {
            System.out
        } else {
            out
        }

        go(spell)
    }

    private fun go(message: String) {
        printer.println(message)
    }

    private companion object {
        private const val TITLE = "Hello, Boat!"
    }
}