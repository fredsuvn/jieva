package test.xyz.srclab.common.base

import org.testng.annotations.Test
import xyz.srclab.common.base.EnvironmentHelper
import xyz.srclab.test.doAssertEquals

object EnvironmentHelperTest {

    @Test
    fun testFindPackage() {
        val pkg = EnvironmentHelper.findPackage("test.xyz.srclab.common.base")
        doAssertEquals(pkg, Package.getPackage("test.xyz.srclab.common.base"))

        val noPackage = EnvironmentHelper.findPackage("test.xyz.srclab.common.base0")
        doAssertEquals(
            EnvironmentHelper.hasPackage("test.xyz.srclab.common.base0"),
            false
        )
        doAssertEquals(noPackage, null)
    }

    @Test
    fun testFindClass() {
        val cls = EnvironmentHelper.findClass("test.xyz.srclab.common.base.EnvironmentHelperTest")
        doAssertEquals(
            cls,
            Class.forName("test.xyz.srclab.common.base.EnvironmentHelperTest")
        )

        val noClass = EnvironmentHelper.findClass("test.xyz.srclab.common.base.EnvironmentHelperTest0")
        doAssertEquals(
            EnvironmentHelper.hasClass("test.xyz.srclab.common.base.EnvironmentHelperTest0"),
            false
        )
        doAssertEquals(noClass, null)
    }
}