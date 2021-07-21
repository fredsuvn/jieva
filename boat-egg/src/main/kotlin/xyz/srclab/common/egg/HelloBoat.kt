package xyz.srclab.common.egg

import java.io.PrintStream

class HelloBoat : Egg {

    private lateinit var printer: PrintStream

    override fun hatchOut(spell: String, feed: Map<Any, Any>) {
        if (spell != "Hello, Boat!") {
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
}