package test.xyz.srclab.common.base

import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import xyz.srclab.common.base.Key
import xyz.srclab.test.doAssertEquals

object KeySupportTest {

    @Test(dataProvider = "keyHelperDataProvider")
    fun testKeyHelper(actual: Any, expected: Any) {
        doAssertEquals(actual, expected)
    }

    @DataProvider
    fun keyHelperDataProvider(): Array<Array<*>> {
        return arrayOf(
            arrayOf(Key.keyOf(), ""),
            arrayOf(Key.keyOf(Object::class.java), "Ljava/lang/Object;"),
            arrayOf(Key.keyOf(Object::class.java, "ss"), "Ljava/lang/Object;:ss")
        )
    }
}