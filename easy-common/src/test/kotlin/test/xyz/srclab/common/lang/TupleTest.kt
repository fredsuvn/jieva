package test.xyz.srclab.common.lang

import org.testng.annotations.Test
import xyz.srclab.test.doAssertEquals
import xyz.srclab.common.lang.tuple.Pair
import xyz.srclab.common.lang.tuple.Tuple

/**
 * @author sunqian
 */
object TupleTest {

    @Test
    fun testTuple() {
        val pair = Pair.of(1, 2)
        doAssertEquals(pair.get0(), 1)
        doAssertEquals(pair.get1(), 2)

        val tuple = Tuple.of(1, 2, 3)
        doAssertEquals(tuple.get0(), 1)
        doAssertEquals(tuple.get1(), 2)
        doAssertEquals(tuple.get2(), 3)
    }
}