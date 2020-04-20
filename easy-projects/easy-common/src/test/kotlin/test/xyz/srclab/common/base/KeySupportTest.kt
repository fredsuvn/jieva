package test.xyz.srclab.common.base

import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import xyz.srclab.common.lang.key.KeySupport
import xyz.srclab.test.doAssertEquals

object KeySupportTest {

    @Test(dataProvider = "keyHelperDataProvider")
    fun testKeyHelper(actual: Any, expected: Any) {
        doAssertEquals(actual, expected)
    }

    @DataProvider
    fun keyHelperDataProvider(): Array<Array<*>> {
        return arrayOf(
            arrayOf(KeySupport.buildKey(), ""),
            arrayOf(KeySupport.buildKey(Object::class.java), "Ljava/lang/Object;"),
            arrayOf(KeySupport.buildKey(Object::class.java, "ss"), "Ljava/lang/Object;:ss")
        )
    }
}