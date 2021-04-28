package xyz.srclab.common.egg.nest.o

import xyz.srclab.common.base.Ref
import java.util.concurrent.CountDownLatch

internal class OTick {

    private var _time: Long = 0
    private var _going: Boolean = false
    private var _isStop: Boolean = false

    private val countDownRef = CountDownRef(Ref.of(CountDownLatch((1))))

    val time: Long
        get() = _time
    val isGoing: Boolean
        get() = _going
    val isStop: Boolean
        get() = _isStop

    @Synchronized
    fun go() {
        _going = true
        countDownRef.get().countDown()
    }

    @Synchronized
    fun pause() {
        _going = false
        countDownRef.reset()
    }

    @Synchronized
    fun stop() {
        _isStop = true
        countDownRef.get().countDown()
    }

    @Synchronized
    fun tick() {
        _time += OConfig.tickInterval
    }

    fun awaitToGo() {
        countDownRef.get().await()
    }
}