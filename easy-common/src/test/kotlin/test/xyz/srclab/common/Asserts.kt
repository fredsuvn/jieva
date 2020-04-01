package test.xyz.srclab.common

import org.apache.commons.lang3.StringUtils
import org.testng.Assert

fun doAssertEquals(actual: Any?, expected: Any?) {
    println("Assert >>> actual: ${toString(actual)}; expected: ${toString(expected)}");
    Assert.assertEquals(actual, expected);
}

private fun toString(any: Any?): String {
    if (any == null) {
        return any.toString()
    }
    if (any is Collection<*>) {
        return "[${StringUtils.join(any, ",")}]"
    }
    if (any is Array<*>) {
        return "[${StringUtils.join(any, ",")}]"
    }
    return any.toString()
}