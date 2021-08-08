package xyz.srclab.common.egg.o

import xyz.srclab.common.lang.Current

open class OConfig {

    var viewWidth: Int = 500
    var viewHeight: Int = 800
    var viewWidthBuffer: Int = 50
    var viewHeightBuffer: Int = 50

    var unitX: Int = 5
    var unitY: Int = 5

    var playerNumber: Int = 2
    var mode: Mode = Mode.PVE
    var randomSeed: Long = Current.nanos

    enum class Mode { PVE, PVP }
}