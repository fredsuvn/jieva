package test.xyz.srclab.test.mark

import org.testng.annotations.Test
import xyz.srclab.test.doAssertEquals
import xyz.srclab.test.mark.MarkHelper
import xyz.srclab.test.mark.Marked

object MarkTest {

    @Test
    fun testMark() {
        val testMark = TestMark()
        testMark.mark()
        testMark.mark("hello")
        testMark.mark("world", "!")
        doAssertEquals(testMark.actualMark, MarkHelper.generateDefaultMark(testMark, testMark))
        doAssertEquals(testMark.getActualMark("hello"), MarkHelper.generateDefaultMark(testMark, "hello"))
        doAssertEquals(testMark.getActualMark("world"), "!")
        testMark.clearMarks()
        doAssertEquals(testMark.actualMark, null)
        doAssertEquals(testMark.getActualMark("hello"), null)
        doAssertEquals(testMark.getActualMark("world"), null)
    }

    class TestMark : Marked
}