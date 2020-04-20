package test.xyz.srclab.common.base

import org.testng.annotations.Test
import xyz.srclab.common.environment.ClassPathHelper
import xyz.srclab.test.doAssertEquals

object ClassPathHelperTest {

    @Test
    fun testFindPackage() {
        val pkg = ClassPathHelper.getPackage("test.xyz.srclab.common.base")
        doAssertEquals(pkg, Package.getPackage("test.xyz.srclab.common.base"))

        val noPackage = ClassPathHelper.getPackage("test.xyz.srclab.common.base0")
        doAssertEquals(
            ClassPathHelper.hasPackage("test.xyz.srclab.common.base0"),
            false
        )
        doAssertEquals(noPackage, null)
    }

    @Test
    fun testFindClass() {
        val cls = ClassPathHelper.getClass("test.xyz.srclab.common.base.EnvironmentHelperTest")
        doAssertEquals(
            cls,
            Class.forName("test.xyz.srclab.common.base.EnvironmentHelperTest")
        )

        val noClass = ClassPathHelper.getClass("test.xyz.srclab.common.base.EnvironmentHelperTest0")
        doAssertEquals(
            ClassPathHelper.hasClass("test.xyz.srclab.common.base.EnvironmentHelperTest0"),
            false
        )
        doAssertEquals(noClass, null)
    }
}