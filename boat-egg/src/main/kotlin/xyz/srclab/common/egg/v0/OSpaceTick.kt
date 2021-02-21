package xyz.srclab.common.egg.v0

import xyz.srclab.common.base.Ref
import java.util.concurrent.CountDownLatch

internal class OSpaceTick(config: OSpaceConfig) {

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
    val tickDuration: Long = config.tickDuration

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
        _time += tickDuration
    }

    fun awaitToGo() {
        countDownRef.get().await()
    }
}