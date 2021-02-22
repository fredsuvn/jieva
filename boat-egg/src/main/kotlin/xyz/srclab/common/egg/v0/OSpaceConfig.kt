package xyz.srclab.common.egg.v0

internal data class OSpaceConfig(
    val width: Int = 640,
    val height: Int = 800,
    val preparedHeight: Int = 100,
    val preparedPadding: Int = 30,
    val xUnit: Double = 5.0,
    val yUnit: Double = 5.0,
    val tickDuration: Long = 1,
    val fps: Int = 60,

    //Boards
    val scoreboardWidth: Int = 80,
    val scoreboardHeight: Int = 25,
    val endBoardWidth: Int = 300,
    val endBoardHeight: Int = 75,
    val endBoardFontSize: Int = 40,
    val infoDisplayBoardWidth: Int = 400,
    val infoDisplayBoardHeight: Int = 25,
)