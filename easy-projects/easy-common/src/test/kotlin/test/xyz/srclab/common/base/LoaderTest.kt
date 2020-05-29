package test.xyz.srclab.common.base

import org.testng.annotations.Test
import xyz.srclab.common.base.Loader
import xyz.srclab.test.doAssertEquals

object LoaderTest {

    @Test
    fun testFindPackage() {
        val pkg = Loader.getPackage("test.xyz.srclab.common.base")
        doAssertEquals(pkg, Package.getPackage("test.xyz.srclab.common.base"))

        val noPackage = Loader.getPackage("test.xyz.srclab.common.base0")
        doAssertEquals(
            Loader.hasPackage("test.xyz.srclab.common.base0"),
            false
        )
        doAssertEquals(noPackage, null)
    }

    @Test
    fun testFindClass() {
        val cls = Loader.loadClass<Class<*>>("test.xyz.srclab.common.base.EnvironmentHelperTest")
        doAssertEquals(
            cls,
            Class.forName("test.xyz.srclab.common.base.EnvironmentHelperTest")
        )

        val noClass = Loader.loadClass<Class<*>>("test.xyz.srclab.common.base.EnvironmentHelperTest0")
        doAssertEquals(
            Loader.hasClass("test.xyz.srclab.common.base.EnvironmentHelperTest0"),
            false
        )
        doAssertEquals(noClass, null)
    }
}