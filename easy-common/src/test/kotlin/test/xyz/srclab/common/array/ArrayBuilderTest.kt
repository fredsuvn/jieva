package test.xyz.srclab.common.array

import org.testng.Assert
import org.testng.annotations.Test
import xyz.srclab.common.array.ArrayBuilder

object ArrayBuilderTest {

    @Test
    fun testWithArray() {
        val intArray = ArrayBuilder.fill<Int, IntArray>(intArrayOf(0, 1, 2))
            .setEachElement { i -> i * 100 }
            .build()
        Assert.assertEquals(intArray, intArrayOf(0, 100, 200))

        val stringArray = ArrayBuilder.fill<String, Array<String>>(arrayOf("1", "2", "3"))
            .setEachElement { i -> i.toString() + "00" }
            .build()
        Assert.assertEquals(stringArray, arrayOf("000", "100", "200"))


    }
}