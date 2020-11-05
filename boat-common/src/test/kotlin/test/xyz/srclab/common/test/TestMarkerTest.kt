package test.xyz.srclab.common.test

import xyz.srclab.common.test.TestMarker
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