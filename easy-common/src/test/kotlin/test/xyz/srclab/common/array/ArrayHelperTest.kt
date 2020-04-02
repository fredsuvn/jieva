package test.xyz.srclab.common.array

import org.testng.annotations.Test
import test.xyz.srclab.common.doAssertEquals
import xyz.srclab.common.array.ArrayHelper

object ArrayHelperTest {

    @Test
    fun testNewArray() {
        val stringArray = arrayOf("")
        val newArray: Array<String> = ArrayHelper.newArray(String::class.java, 0)
        doAssertEquals(newArray.javaClass, stringArray.javaClass)
    }

    @Test
    fun testNewArrayFill() {
        val stringArray = arrayOf("0", "1")
        val newStringArray = ArrayHelper.newArray(arrayOfNulls(2), 0, stringArray.size) { i -> "" + i }
        doAssertEquals(stringArray, newStringArray)

        val byteArray = byteArrayOf(0, 1)
        val newByteArray = ArrayHelper.newArray(ByteArray(2), 0, byteArray.size) { i -> i.toByte() }
        doAssertEquals(byteArray, newByteArray)

        val charArray = charArrayOf(0.toChar(), 1.toChar())
        val newCharArray = ArrayHelper.newArray(CharArray(2), 0, charArray.size) { i -> i.toChar() }
        doAssertEquals(charArray, newCharArray)

        val intArray = intArrayOf(0, 1)
        val newIntArray = ArrayHelper.newArray(IntArray(2), 0, intArray.size) { i -> i }
        doAssertEquals(intArray, newIntArray)

        val longArray = longArrayOf(0L, 1L)
        val newLongArray = ArrayHelper.newArray(LongArray(2), 0, longArray.size) { i -> i + 0L }
        doAssertEquals(longArray, newLongArray)

        val floatArray = floatArrayOf(0f, 1f)
        val newFloatArray = ArrayHelper.newArray(FloatArray(2), 0, floatArray.size) { i -> i + 0f }
        doAssertEquals(floatArray, newFloatArray)

        val doubleArray = doubleArrayOf(0.0, 1.0)
        val newDoubleArray = ArrayHelper.newArray(DoubleArray(2), 0, doubleArray.size) { i -> i + 0.0 }
        doAssertEquals(doubleArray, newDoubleArray)

        val booleanArray = arrayOf(true, false)
        val newBooleanArray = ArrayHelper.newArray(BooleanArray(2), 0, booleanArray.size) { i -> i == 0 }
        doAssertEquals(booleanArray, newBooleanArray)

        val shortArray = shortArrayOf(0, 1)
        val newShortArray = ArrayHelper.newArray(ShortArray(2), 0, shortArray.size) { i -> i.toShort() }
        doAssertEquals(shortArray, newShortArray)
    }

    @Test
    fun testFindArrayType() {
        doAssertEquals(ArrayHelper.findArrayType(Int::class.java), intArrayOf(1).javaClass)
    }

    data class A<T>(val content: T)
}