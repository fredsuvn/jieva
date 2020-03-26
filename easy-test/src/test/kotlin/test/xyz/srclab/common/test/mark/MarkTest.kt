package test.xyz.srclab.common.test.mark

import org.testng.annotations.Test
import xyz.srclab.common.test.asserts.AssertHelper
import xyz.srclab.common.test.mark.MarkHelper
import xyz.srclab.common.test.mark.Marked

object MarkTest {

    @Test
    fun testMark() {
        val testMark = TestMark()
        testMark.mark()
        testMark.mark("hello")
        testMark.mark("world", "!")
        AssertHelper.printAssert(testMark.actualMark, MarkHelper.generateDefaultMark(testMark, testMark))
        AssertHelper.printAssert(testMark.getActualMark("hello"), MarkHelper.generateDefaultMark(testMark, "hello"))
        AssertHelper.printAssert(testMark.getActualMark("world"), "!")
        testMark.clearMarks()
        AssertHelper.printAssert(testMark.actualMark, null)
        AssertHelper.printAssert(testMark.getActualMark("hello"), null)
        AssertHelper.printAssert(testMark.getActualMark("world"), null)
    }

    class TestMark : Marked
}