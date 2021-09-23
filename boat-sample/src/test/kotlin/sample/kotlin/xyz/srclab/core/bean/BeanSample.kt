package sample.kotlin.xyz.srclab.core.bean

import org.testng.annotations.Test
import xyz.srclab.common.bean.copyProperties

class BeanSample {

    @Test
    fun testBean() {
        val a = A()
        a.p1 = "1"
        a.p2 = "2"
        val b = a.copyProperties(B())
        val b1 = b.p1
        val b2 = b.p2
        //1
        logger.log("b1: {}", b1)
        //2
        logger.log("b1: {}", b2)
    }

    class A {
        var p1: String? = null
        var p2: String? = null
    }

    class B {
        var p1 = 0
        var p2 = 0
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}