package xyz.srclab.common.egg.v0

import xyz.srclab.common.base.Ref
import java.util.concurrent.CountDownLatch

class CountDownRef(private val ref: Ref<CountDownLatch>) : Ref<CountDownLatch> by ref {

    fun reset() {
        ref.set(CountDownLatch(1))
    }
}