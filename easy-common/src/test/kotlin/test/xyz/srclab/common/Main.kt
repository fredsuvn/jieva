package test.xyz.srclab.common

import test.xyz.srclab.common.main.MainTest

fun main() {
    val main = MainTest()
    println("main.testNull(): ${main.testNull("a").chars()}")
    println("main.testNotNull(): ${main.testNotNull(null).chars()}")
    println("main.testNullable(): ${main.testNullable(null).chars()}")
}