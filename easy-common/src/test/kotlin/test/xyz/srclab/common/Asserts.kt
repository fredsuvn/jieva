package test.xyz.srclab.common

import org.testng.Assert

fun doTest(actual: Any?, expected: Any?) {
    println("actual: $actual, expected: $expected")
    Assert.assertEquals(actual, expected)
}