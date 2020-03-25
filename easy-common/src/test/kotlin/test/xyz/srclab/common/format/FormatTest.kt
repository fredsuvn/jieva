package test.xyz.srclab.common.format

import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import test.xyz.srclab.common.doAssert
import xyz.srclab.common.format.FormatHelper

object FormatTest {

    @Test(dataProvider = "fastFormatDataProvider")
    fun testFastFormat(actual: String, expected: String) {
        doAssert(actual, expected)
    }

    @DataProvider
    fun fastFormatDataProvider(): Array<Array<*>> {
        return arrayOf(
            arrayOf(FormatHelper.fastFormat("123{}56{{}89{}", 4, 7), "123456{789{}")
        )
    }

    @Test(dataProvider = "printfFormatDataProvider")
    fun testPrintfFormat(actual: String, expected: String) {
        doAssert(actual, expected)
    }

    @DataProvider
    fun printfFormatDataProvider(): Array<Array<*>> {
        return arrayOf(
            arrayOf(FormatHelper.printfFormat("123%s56%%%s89", 4, 7), "123456%789")
        )
    }

    @Test(dataProvider = "messageFormatDataProvider")
    fun testMessageFormat(actual: String, expected: String) {
        doAssert(actual, expected)
    }

    @DataProvider
    fun messageFormatDataProvider(): Array<Array<*>> {
        return arrayOf(
            arrayOf(FormatHelper.messageFormat("123{1}56{0}89", 7, 4), "123456789")
        )
    }
}