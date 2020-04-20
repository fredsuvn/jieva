package test.xyz.srclab.common.array

import org.testng.annotations.Test
import xyz.srclab.common.array.ArrayBuilder
import xyz.srclab.common.array.ArrayHelper
import xyz.srclab.common.reflect.type.TypeRef
import xyz.srclab.test.doAssertEquals
import xyz.srclab.test.doExpectThrowable

object ArrayBuilderTest {

    @Test
    fun testFill() {
        val intArray = ArrayBuilder
            .fill(intArrayOf(1, 2, 3), Int::class.java)
            .setEachElement { i -> i * 100 }
            .build()
        doAssertEquals(intArray, intArrayOf(0, 100, 200))
        doExpectThrowable(IllegalStateException::class.java) {
            ArrayBuilder
                .fill("", Int::class.java)
                .build()
        }

        val stringArray = ArrayBuilder
            .fill(arrayOf(A("000"), A("100"), A("200")), object : TypeRef<A<String>>() {})
            .setEachElement { i -> A(i.toString() + "00") }
            .build()
        doAssertEquals(stringArray, arrayOf(A("000"), A("100"), A("200")))
        doExpectThrowable(IllegalStateException::class.java) {
            ArrayBuilder
                .fill("", object : TypeRef<A<String>>() {})
                .build()
        }
    }

    @Test
    fun testNewArray() {
        val intArray = ArrayBuilder
            .newArray(IntArray::class.java, Int::class.java, 3)
            .setEachElement { i -> i * 100 }
            .build()
        doAssertEquals(intArray, intArrayOf(0, 100, 200))
        doExpectThrowable(IllegalStateException::class.java) {
            ArrayBuilder
                .newArray(String::class.java, Int::class.java, 3)
                .build()
        }

        val rawTypeRefArray = ArrayBuilder
            .newArray(ArrayHelper.findArrayType(A::class.java), object : TypeRef<A<String>>() {}, 3)
            .setEachElement { i -> A(i.toString() + "00") }
            .build()
        doAssertEquals(rawTypeRefArray, arrayOf(A("000"), A("100"), A("200")))
        doExpectThrowable(IllegalStateException::class.java) {
            ArrayBuilder
                .newArray(String::class.java, object : TypeRef<A<String>>() {}, 3)
                .build()
        }

        val typeRefRaw = ArrayBuilder
            .newArray(object : TypeRef<Array<A<String>>>() {}, A::class.java, 3)
            .setEachElement { i -> A(i.toString() + "00") }
            .build()
        doAssertEquals(typeRefRaw, arrayOf(A("000"), A("100"), A("200")))
        doExpectThrowable(IllegalStateException::class.java) {
            ArrayBuilder
                .newArray(object : TypeRef<String>() {}, A::class.java, 3)
                .build()
        }

        val typeRefTypeRefArray = ArrayBuilder
            .newArray(object : TypeRef<Array<A<String>>>() {}, object : TypeRef<A<String>>() {}, 3)
            .setEachElement { i -> A(i.toString() + "00") }
            .build()
        doAssertEquals(typeRefTypeRefArray, arrayOf(A("000"), A("100"), A("200")))
        doExpectThrowable(IllegalStateException::class.java) {
            ArrayBuilder
                .newArray(object : TypeRef<String>() {}, object : TypeRef<A<String>>() {}, 3)
                .build()
        }
    }

    data class A<T>(val content: T)
}