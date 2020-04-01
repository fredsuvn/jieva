package test.xyz.srclab.common.array

import org.testng.Assert
import org.testng.annotations.Test
import xyz.srclab.common.array.ArrayBuilder
import xyz.srclab.common.lang.TypeRef

object ArrayBuilderTest {

    @Test
    fun testFill() {
        val intArray = ArrayBuilder
            .fill(intArrayOf(1, 2, 3), Int::class.java)
            .setEachElement { i -> i * 100 }
            .build()
        Assert.assertEquals(intArray, intArrayOf(0, 100, 200))
    }

    @Test
    fun testNewArray() {
        val intArray = ArrayBuilder
            .newArray(IntArray::class.java, Int::class.java, 3)
            .setEachElement { i -> i * 100 }
            .build()
        Assert.assertEquals(intArray, intArrayOf(0, 100, 200))

        val stringArray = ArrayBuilder
            .newArray(object : TypeRef<Array<A<String>>>() {}, object : TypeRef<A<String>>() {}, 3)
            .setEachElement { i -> A(i.toString() + "00") }
            .build()
        Assert.assertEquals(stringArray, arrayOf(A("000"), A("100"), A("200")))
    }

    data class A<T>(val content: T)
}