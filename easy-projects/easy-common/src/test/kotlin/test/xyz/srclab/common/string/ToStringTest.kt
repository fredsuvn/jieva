package test.xyz.srclab.common.string

import org.testng.annotations.Test
import xyz.srclab.common.base.Defaults
import xyz.srclab.common.bean.BeanOperator
import xyz.srclab.common.string.LoopElementException
import xyz.srclab.common.string.ToString
import xyz.srclab.common.string.ToStringStyle
import xyz.srclab.test.doAssertEquals
import xyz.srclab.test.doExpectThrowable

/**
 * @author sunqian
 */
object ToStringTest {

    @Test
    fun testToString() {
        doAssertEquals(ToString.buildToString("ss"), "ss")
        doAssertEquals(ToString.buildToString("ss", ToStringStyle.HUMAN_READABLE), "ss")
        val a = A()
        doAssertEquals(
            ToString.buildToString(a),
            "{aaa=aaa," +
                    "array=[1],array2=[]," +
                    "b={class=${B::class.java.name}," +
                    "set=[bbb]},class=${A::class.java.name}," +
                    "map={1=1}}"
        )
        val c = C()
        c.a = c
        doExpectThrowable(LoopElementException::class.java) {
            ToString.buildToString(c)
        }.catch { e ->
            doAssertEquals(e.nameStackTrace, ".a")
        }

        doAssertEquals(
            ToString.buildToString(a, ToStringStyle.HUMAN_READABLE),
            "{" + Defaults.LINE_SEPARATOR +
                    "    aaa = aaa, " + Defaults.LINE_SEPARATOR +
                    "    array = [" + Defaults.LINE_SEPARATOR +
                    "        1" + Defaults.LINE_SEPARATOR +
                    "    ], " + Defaults.LINE_SEPARATOR +
                    "    array2 = [], " + Defaults.LINE_SEPARATOR +
                    "    b = {" + Defaults.LINE_SEPARATOR +
                    "        class = ${B::class.java.name}, " + Defaults.LINE_SEPARATOR +
                    "        set = [" + Defaults.LINE_SEPARATOR +
                    "            bbb" + Defaults.LINE_SEPARATOR +
                    "        ]" + Defaults.LINE_SEPARATOR +
                    "    }, " + Defaults.LINE_SEPARATOR +
                    "    class = ${A::class.java.name}, " + Defaults.LINE_SEPARATOR +
                    "    map = {" + Defaults.LINE_SEPARATOR +
                    "        1 = 1" + Defaults.LINE_SEPARATOR +
                    "    }" + Defaults.LINE_SEPARATOR +
                    "}"
        )

        doAssertEquals(
            ToString.buildToString(a, ToStringStyle.HUMAN_READABLE, BeanOperator.DEFAULT),
            "{" + Defaults.LINE_SEPARATOR +
                    "    aaa = aaa, " + Defaults.LINE_SEPARATOR +
                    "    array = [" + Defaults.LINE_SEPARATOR +
                    "        1" + Defaults.LINE_SEPARATOR +
                    "    ], " + Defaults.LINE_SEPARATOR +
                    "    array2 = [], " + Defaults.LINE_SEPARATOR +
                    "    b = {" + Defaults.LINE_SEPARATOR +
                    "        class = ${B::class.java.name}, " + Defaults.LINE_SEPARATOR +
                    "        set = [" + Defaults.LINE_SEPARATOR +
                    "            bbb" + Defaults.LINE_SEPARATOR +
                    "        ]" + Defaults.LINE_SEPARATOR +
                    "    }, " + Defaults.LINE_SEPARATOR +
                    "    class = ${A::class.java.name}, " + Defaults.LINE_SEPARATOR +
                    "    map = {" + Defaults.LINE_SEPARATOR +
                    "        1 = 1" + Defaults.LINE_SEPARATOR +
                    "    }" + Defaults.LINE_SEPARATOR +
                    "}"
        )

        val toString = ToString(a)
        doAssertEquals(toString.get(), toString.toString())
        doAssertEquals(toString.refreshGet(), toString.toString())
        doAssertEquals(ToString("ss").toString(), "ss")
        doAssertEquals(
            ToString.buildToString("ss", ToStringStyle.DEFAULT, BeanOperator.DEFAULT),
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