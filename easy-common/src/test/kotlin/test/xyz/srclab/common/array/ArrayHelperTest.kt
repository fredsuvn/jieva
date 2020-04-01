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
    fun testFindArrayType() {
        doAssertEquals(ArrayHelper.findArrayType(Int::class.java), intArrayOf(1).javaClass)
    }

    data class A<T>(val content: T)
}