package xyz.srclab.common.egg.nest.o

import java.awt.Color

internal object Config {

    //Window
    const val width: Int = 640
    const val height: Int = 800
    const val preparedHeight: Int = 100
    const val preparedPadding: Int = 30
    const val xUnit: Double = 5.0
    const val yUnit: Double = 5.0
    const val tickDuration: Long = 1
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

    //Game
    const val PLAYER_RADIUS = 14.0
    const val ENEMY_RADIUS = 14.0
    const val AMMO_RADIUS = 6.0
    const val PLAYER_MOVE_SPEED = 90
    const val ENEMY_MOVE_SPEED = 50
    const val AMMO_MOVE_SPEED = 80
    const val DEATH_DURATION = 1000L
    const val PLAYER_WEAPON_DAMAGE = 100
    const val ENEMY_WEAPON_DAMAGE = 50
    const val PLAYER_WEAPON_FIRE_SPEED = 95
    const val ENEMY_WEAPON_FIRE_SPEED = 50
    const val ENEMY_CRAZY_WEAPON_FIRE_SPEED = 40

    //Units
    val player1Color: Color = Color.BLUE
    val player1Text: String? = null
    val player1AmmoColor: Color = Color.MAGENTA
    val player1AmmoText: String? = null
    val player2Color: Color = Color.GREEN
    val player2Text: String? = null
    val player2AmmoColor: Color = Color.YELLOW
    val player2AmmoText: String? = null
    val enemyColor: Color = Color.GRAY
    val enemyText: String? = null
    val enemyAmmoColor: Color = Color.GRAY
    val enemyAmmoText: String? = null

    //Debug
    const val isDebug: Boolean = false

    //Version
    const val version: String = "0.0.0"
}