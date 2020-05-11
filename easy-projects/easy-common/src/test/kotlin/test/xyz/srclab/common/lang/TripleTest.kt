package test.xyz.srclab.common.lang

import org.testng.annotations.Test
import xyz.srclab.common.lang.Pair
import xyz.srclab.common.lang.Triple
import xyz.srclab.test.doAssertEquals

/**
 * @author sunqian
 */
object TripleTest {

    @Test
    fun testTuple() {
        val pair = Pair.of(1, 2)
        doAssertEquals(pair.get0(), 1)
        doAssertEquals(pair.get1(), 2)

        val tuple = Triple.of(1, 2, 3)
        doAssertEquals(tuple.get0(), 1)
        doAssertEquals(tuple.get1(), 2)
        doAssertEquals(tuple.get2(), 3)
    }
}