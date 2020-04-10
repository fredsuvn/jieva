package test.xyz.srclab.common.base

import org.testng.annotations.Test
import xyz.srclab.test.doAssertEquals
import xyz.srclab.test.doExpectThrowable
import xyz.srclab.common.base.EnvironmentHelper

object EnvironmentHelperTest {

    @Test
    fun testFindPackage() {
        val pkg = EnvironmentHelper.findPackage("test.xyz.srclab.common.base")
        doAssertEquals(pkg.get(), Package.getPackage("test.xyz.srclab.common.base"))

        val noPackage = EnvironmentHelper.findPackage("test.xyz.srclab.common.base0")
        doExpectThrowable(NoSuchElementException::class.java) {
            noPackage.get()
        }
    }

    @Test
    fun testFindClass() {
        val cls = EnvironmentHelper.findClass("test.xyz.srclab.common.base.EnvironmentHelperTest")
        doAssertEquals(
            cls.get(),
            Class.forName("test.xyz.srclab.common.base.EnvironmentHelperTest")
        )

        val noClass = EnvironmentHelper.findClass("test.xyz.srclab.common.base.EnvironmentHelperTest0")
        doExpectThrowable(NoSuchElementException::class.java) {
            noClass.get()
        }
    }
}