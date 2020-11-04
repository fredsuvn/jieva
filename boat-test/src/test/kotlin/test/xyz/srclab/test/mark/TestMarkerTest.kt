package test.xyz.srclab.test.mark

import xyz.srclab.test.mark.TestMarker
import kotlin.test.Test

object TestMarkerTestKt {

    @Test
    fun testTestMarker() {
        val testMarker = TestMarker.newTestMarker()
        testMarker.mark("hello")
        testMarker.mark("world", "!")
        println(testMarker)
    }
}