package test.xyz.srclab.common.base

import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import xyz.srclab.common.lang.key.Key
import xyz.srclab.test.doAssertEquals

object KeySupportTest {

    @Test(dataProvider = "keyHelperDataProvider")
    fun testKeyHelper(actual: Any, expected: Any) {
        doAssertEquals(actual, expected)
    }

    @DataProvider
    fun keyHelperDataProvider(): Array<Array<*>> {
        return arrayOf(
            arrayOf(Key.from(), ""),
            arrayOf(Key.from(Object::class.java), "Ljava/lang/Object;"),
            arrayOf(Key.from(Object::class.java, "ss"), "Ljava/lang/Object;:ss")
        )
    }
}