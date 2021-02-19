package xyz.srclab.common.egg.v0

data class OSpaceConfig(
    val width: Int = 640,
    val height: Int = 800,
    val preparedHeight: Int = 100,
    val preparedPadding: Int = 40,
    val xUnit: Double = 1.0,
    val yUnit: Double = 1.0,
    val tickDuration: Long = 2,
    val fps: Int = 60,
)