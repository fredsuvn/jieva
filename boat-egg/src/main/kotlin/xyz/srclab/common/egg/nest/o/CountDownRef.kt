package xyz.srclab.common.egg.nest.o

import xyz.srclab.common.base.Ref
import java.util.concurrent.CountDownLatch

internal class CountDownRef(private val ref: Ref<CountDownLatch>) : Ref<CountDownLatch> by ref {

    fun reset() {
        val countDown = ref.getOrNull()
        if (countDown != null && countDown.count > 0) {
            return
        }
        ref.set(CountDownLatch(1))
    }
}