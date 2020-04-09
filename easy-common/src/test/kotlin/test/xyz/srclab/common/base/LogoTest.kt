package test.xyz.srclab.common.base

import org.testng.annotations.Test
import xyz.srclab.common.base.EnvironmentHelper
import xyz.srclab.common.base.logo.LogoHelper

object LogoTest {

    @Test
    fun testLogo() {
        val cls = EnvironmentHelper.findClass("xyz.srclab.common.base.logo.EasyStarterLogo").get()
        val field = cls.getDeclaredField("SECRET_CODE")
        println(field)
        field.isAccessible = true
        val secretCode = field[null] as String
        val logo = LogoHelper.findLogo(secretCode)
        logo.printLgo()
    }
}