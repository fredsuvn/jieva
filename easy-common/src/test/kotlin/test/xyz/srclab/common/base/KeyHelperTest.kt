package test.xyz.srclab.common.base

import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import xyz.srclab.test.doAssertEquals
import xyz.srclab.common.base.KeyHelper

object KeyHelperTest {

    @Test(dataProvider = "keyHelperDataProvider")
    fun testKeyHelper(actual: Any, expected: Any) {
        doAssertEquals(actual, expected)
    }

    @DataProvider
    fun keyHelperDataProvider(): Array<Array<*>> {
        return arrayOf(
            arrayOf(KeyHelper.buildKey(), ""),
            arrayOf(KeyHelper.buildKey(Object::class.java), "Ljava/lang/Object;"),
            arrayOf(KeyHelper.buildKey(Object::class.java, "ss"), "Ljava/lang/Object;:ss")
        )
    }
}