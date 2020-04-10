package test.xyz.srclab.common.base

import org.testng.annotations.Test
import xyz.srclab.test.doExpectThrowable
import xyz.srclab.common.egg.EggHelper
import java.lang.IllegalArgumentException

object EggTest {

    @Test
    fun testLogo() {
        val egg = EggHelper.findEgg("xyz.srclab.common.egg.EasyStarterEgg")
        val field = egg::class.java.getDeclaredField("SPELL")
        println(field)
        field.isAccessible = true
        val spell = field.get(null) as String
        egg.hatchOut(spell)

        doExpectThrowable(IllegalArgumentException::class.java) {
            EggHelper.findEgg("xyz.srclab.common.egg.EasyStarterEgg0")
        }
    }
}