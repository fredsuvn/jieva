package test.xyz.srclab.common

import org.apache.commons.lang3.ArrayUtils
import org.apache.commons.lang3.StringUtils
import org.testng.Assert

fun doAssertEquals(actual: Any?, expected: Any?) {
    println("Assert >>> actual: ${toString(actual)}; expected: ${toString(expected)}")
    if (actual is Map<*, *> && expected is Map<*, *>) {
        Assert.assertEquals(actual, expected)
    } else if (actual is Collection<*> && expected is Collection<*>) {
        Assert.assertEquals(actual, expected)
    } else {
        Assert.assertEquals(actual, expected)
    }
}

fun <T : Throwable> doExpectThrowable(expected: Class<T>, runnable: () -> Unit): ThrowableCatcher<T> {
    println("Expect throwable >>> $expected")
    try {
        runnable()
    } catch (t: Throwable) {
        if (expected.isInstance(t)) {
            return object : ThrowableCatcher<T> {
                override fun catch(action: (T) -> Unit) {
                    action(expected.cast(t))
                }
            }
        } else {
            val mismatchMessage = String.format(
                "Expected %s to be thrown, but %s was thrown",
                expected.simpleName, t.javaClass.simpleName
            )
            throw AssertionError(mismatchMessage, t)
        }
    }
    val message = String.format(
        "Expected %s to be thrown, but nothing was thrown", expected.simpleName
    )
    throw AssertionError(message)
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

interface ThrowableCatcher<T> {

    fun catch(action: (T) -> Unit)
}