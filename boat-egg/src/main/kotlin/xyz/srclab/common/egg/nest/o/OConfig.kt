package xyz.srclab.common.egg.nest.o

internal object OConfig {

    //Window
    const val width: Int = 640
    const val height: Int = 800
    const val preparedHeight: Int = 100
    const val preparedPadding: Int = 30
    const val xUnit: Double = 5.0
    const val yUnit: Double = 5.0
    const val tickInterval: Long = 1
    const val fps: Int = 60

    //Boards
    const val scoreboardWidth: Int = 80
    const val scoreboardHeight: Int = 25
    const val endBoardWidth: Int = 300
    const val endBoardHeight: Int = 75
    const val endBoardFontSize: Int = 40
    const val infoDisplayBoardWidth: Int = 400
    const val infoDisplayBoardHeight: Int = 25
    const val infoListSize: Int = 2

    //Debug
    const val isDebug: Boolean = false

    //Version
    const val name: String = "O Battle"
    const val version: String = "0.0.0"
}