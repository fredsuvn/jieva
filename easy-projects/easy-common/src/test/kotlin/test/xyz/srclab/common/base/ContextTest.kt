package test.xyz.srclab.common.base

import org.testng.annotations.Test
import xyz.srclab.common.base.Context
import xyz.srclab.test.doAssertEquals

object ContextTest {

    @Test
    fun testFindPackage() {
        val pkg = Context.getPackage("test.xyz.srclab.common.base")
        doAssertEquals(pkg, Package.getPackage("test.xyz.srclab.common.base"))

        val noPackage = Context.getPackage("test.xyz.srclab.common.base0")
        doAssertEquals(
            Context.hasPackage("test.xyz.srclab.common.base0"),
            false
        )
        doAssertEquals(noPackage, null)
    }

    @Test
    fun testFindClass() {
        val cls = Context.getClass<Class<*>>("test.xyz.srclab.common.base.EnvironmentHelperTest")
        doAssertEquals(
            cls,
            Class.forName("test.xyz.srclab.common.base.EnvironmentHelperTest")
        )

        val noClass = Context.getClass<Class<*>>("test.xyz.srclab.common.base.EnvironmentHelperTest0")
        doAssertEquals(
            Context.hasClass("test.xyz.srclab.common.base.EnvironmentHelperTest0"),
            false
        )
        doAssertEquals(noClass, null)
    }
}