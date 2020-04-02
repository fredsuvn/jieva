package test.xyz.srclab.common

import org.apache.commons.lang3.ArrayUtils
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
    if (any is ByteArray) {
        return "[${StringUtils.join(ArrayUtils.toObject(any), ",")}]"
    }
    if (any is CharArray) {
        return "[${StringUtils.join(ArrayUtils.toObject(any), ",")}]"
    }
    if (any is IntArray) {
        return "[${StringUtils.join(ArrayUtils.toObject(any), ",")}]"
    }
    if (any is LongArray) {
        return "[${StringUtils.join(ArrayUtils.toObject(any), ",")}]"
    }
    if (any is FloatArray) {
        return "[${StringUtils.join(ArrayUtils.toObject(any), ",")}]"
    }
    if (any is DoubleArray) {
        return "[${StringUtils.join(ArrayUtils.toObject(any), ",")}]"
    }
    if (any is BooleanArray) {
        return "[${StringUtils.join(ArrayUtils.toObject(any), ",")}]"
    }
    if (any is ShortArray) {
        return "[${StringUtils.join(ArrayUtils.toObject(any), ",")}]"
    }
    return any.toString()
}