package test.xyz.srclab.common.string

import org.testng.annotations.Test
import test.xyz.srclab.common.doAssertEquals
import test.xyz.srclab.common.doExpectThrowable
import xyz.srclab.common.bean.BeanOperator
import xyz.srclab.common.string.tostring.PropertyOrElementReferenceLoopException
import xyz.srclab.common.string.tostring.ToString
import xyz.srclab.common.string.tostring.ToStringStyle

/**
 * @author sunqian
 */
object ToStringTest {

    @Test
    fun testToString() {
        doAssertEquals(ToString.toString("ss"), "ss")
        val a = A()
        doAssertEquals(
            ToString.toString(a),
            "{aaa=aaa," +
                    "array=[1],array2=[]," +
                    "b={class=${B::class.java.name}," +
                    "set=[bbb]},class=${A::class.java.name}," +
                    "map={1=1}}"
        )
        val c = C()
        c.a = c
        doExpectThrowable(PropertyOrElementReferenceLoopException::class.java) {
            ToString.toString(c)
        }.catch { e ->
            doAssertEquals(e.nameStackTrace, ".a")
        }

        doAssertEquals(
            ToString.toString(a, ToStringStyle.HUMAN_READABLE, BeanOperator.DEFAULT),
            "{\n" +
                    "    aaa = aaa, \n" +
                    "    array = [\n" +
                    "        1\n" +
                    "    ], \n" +
                    "    array2 = [], \n" +
                    "    b = {\n" +
                    "        class = ${B::class.java.name}, \n" +
                    "        set = [\n" +
                    "            bbb\n" +
                    "        ]\n" +
                    "    }, \n" +
                    "    class = ${A::class.java.name}, \n" +
                    "    map = {\n" +
                    "        1 = 1\n" +
                    "    }\n" +
                    "}"
        )

        val toString = ToString(a)
        doAssertEquals(toString.get(), toString.toString())
        doAssertEquals(toString.refreshGet(), toString.toString())
        doAssertEquals(ToString("ss").toString(), "ss")
        doAssertEquals(
            ToString.toString("ss", ToStringStyle.DEFAULT, BeanOperator.DEFAULT),
            "ss"
        )
    }

    open class A {
        var aaa = "aaa"
        var b = B()
        var array = arrayOf(1)
        var array2 = arrayOf<String>()
        var map = mapOf(1 to 1)
    }

    class B {
        var set = setOf("bbb")
    }

    class C : A() {
        var a: A? = null
    }
}