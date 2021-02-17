package xyz.srclab.common.egg.v0

class OSpaceTick(private val config: OSpaceConfig) {

    private var _time: Long = 0
    private var _going: Boolean = false
    private var _isStop: Boolean = false

    val time: Long = _time
    val isGoing: Boolean = _going
    val isStop: Boolean = _isStop

    fun go() {
        _going = true
    }

    fun pause() {
        _going = false
    }

    fun stop() {
        _isStop = true
    }

    fun tick() {
        _time += config.tickUnit
    }
}