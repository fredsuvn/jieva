package test.xyz.srclab.test.mark

import org.testng.annotations.Test
import xyz.srclab.test.doAssertEquals
import xyz.srclab.test.mark.MarkHelper
import xyz.srclab.test.mark.MarkTesting

object MarkTest {

    @Test
    fun testMark() {
        val testMark = TestMark()
        testMark.mark()
        testMark.mark("hello")
        testMark.mark("world", "!")
        doAssertEquals(testMark.mark, MarkHelper.generateMark(testMark, testMark))
        doAssertEquals(testMark.getMark("hello"), MarkHelper.generateMark(testMark, "hello"))
        doAssertEquals(testMark.getMark("world"), "!")
        testMark.unmark()
        testMark.unmark("hello")
        testMark.unmark("world")
        doAssertEquals(testMark.mark, null)
        doAssertEquals(testMark.getMark("hello"), null)
        doAssertEquals(testMark.getMark("world"), null)

        testMark.mark(1, 2)
        doAssertEquals(testMark.getMark(1), 2)
        testMark.clearMarks()
        doAssertEquals(testMark.getMark(1), null)
        doAssertEquals(testMark.allMarks.size, 0)
    }

    class TestMark : MarkTesting
}